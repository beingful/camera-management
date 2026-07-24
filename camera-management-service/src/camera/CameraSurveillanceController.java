package camera;

import camera.configuration.Configuration;
import camera.configuration.DataController;
import camera.configuration.DataObserver;
import camera.logging.ILogger;
import camera.models.Camera;
import camera.models.CameraSet;
import camera.runtime.CameraSetRuntime;
import camera.snapshot.SnapshotCapturer;
import camera.snapshot.SnapshotDifference;
import camera.threading.ThreadPoolManager;
import camera.validation.ValidationSupport;

import java.util.List;

public class CameraSurveillanceController extends DataController<CameraSet, Camera> {
    private final CameraSetRuntime cameraSetRuntime;
    private final ThreadPoolManager threadPoolManager;
    private final ILogger logger;
    private boolean running;
    private boolean terminated;

    public CameraSurveillanceController(
            DataObserver<CameraSet> cameraSetObserver,
            Configuration<CameraSet> cameraSetConfiguration,
            SnapshotCapturer<CameraSet, Camera> snapshotCapturer,
            CameraSetRuntime cameraSetRuntime,
            ThreadPoolManager threadPoolManager,
            ILogger logger) {
        super(cameraSetObserver, cameraSetConfiguration, snapshotCapturer, logger);
        ValidationSupport.validateRequired("Camera set runtime", cameraSetRuntime);
        ValidationSupport.validateRequired("Thread pool manager", threadPoolManager);
        ValidationSupport.validateRequired("Logger", logger);

        this.cameraSetRuntime = cameraSetRuntime;
        this.threadPoolManager = threadPoolManager;
        this.logger = logger;
        this.running = false;
        this.terminated = false;
    }

    @Override
    public synchronized void connect() {
        if (terminated) {
            throw new IllegalStateException("Camera surveillance controller has been shut down.");
        }

        if (running) {
            return;
        }

        logger.info("Connecting camera surveillance controller.");
        super.connect();
        running = true;
    }

    @Override
    protected synchronized void updateData() {
        if (terminated) {
            return;
        }

        SnapshotDifference<Camera> cameraSet = dataChanges();

        logger.info("Updating camera set.");

        disconnect(cameraSet.deleted);
        disconnect(cameraSet.updated);

        threadPoolManager.shutdownCameraThreadPools(cameraSet.deleted);
        threadPoolManager.shutdownCameraThreadPools(cameraSet.updated);

        threadPoolManager.createCameraThreadPools(cameraSet.created);
        threadPoolManager.createCameraThreadPools(cameraSet.updated);

        connect(cameraSet.created);
        connect(cameraSet.updated);
    }

    private void connect(List<Camera> cameras) {
        if (!cameras.isEmpty()) {
            cameraSetRuntime.connect(new CameraSet(cameras));
        }
    }

    private void disconnect(List<Camera> cameras) {
        if (!cameras.isEmpty()) {
            cameraSetRuntime.disconnect(new CameraSet(cameras));
        }
    }

    @Override
    public synchronized void disconnect() {
        if (terminated) {
            return;
        }

        logger.info("Disconnecting camera surveillance controller.");
        terminated = true;
        running = false;

        super.disconnect();

        cameraSetRuntime.disconnectAll();
        threadPoolManager.shutdown();
        cameraSetRuntime.closeSystemQueues();
    }

    public synchronized void disconnectCamera(int cameraId) {
        if (terminated) {
            return;
        }

        logger.warning("Disconnecting camera after error: " + cameraId);
        cameraSetRuntime.disconnect(cameraId);
        threadPoolManager.shutdownCameraThreadPool(cameraId);
    }

    public synchronized void shutdown() {
        if (terminated) {
            return;
        }

        logger.warning("Shutting down camera surveillance controller after system error.");
        disconnect();
    }

}
