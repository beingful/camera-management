package camera.runtime;

import camera.logging.LoggerFactory;
import camera.logging.ILogger;
import camera.messaging.MessageBus;
import camera.models.Camera;
import camera.models.CameraSet;
import camera.validation.ValidationSupport;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CameraSetRuntime {
    private final CameraRuntimeFactory cameraRuntimeFactory;
    private final MessageBus systemMessageBus;
    private final Map<Integer, CameraRuntime> cameraRuntimes;
    private final ILogger logger;

    public CameraSetRuntime(CameraRuntimeFactory cameraRuntimeFactory, MessageBus systemMessageBus) {
        ValidationSupport.validateRequired("Camera runtime factory", cameraRuntimeFactory);
        ValidationSupport.validateRequired("System message bus", systemMessageBus);

        this.cameraRuntimeFactory = cameraRuntimeFactory;
        this.systemMessageBus = systemMessageBus;
        this.cameraRuntimes = new HashMap<>();
        this.logger = LoggerFactory.systemLogger(CameraSetRuntime.class);
    }

    public synchronized void connect(CameraSet cameraSet) {
        ValidationSupport.validateRequired("Camera set", cameraSet);

        for (Camera camera : cameraSet.cameras()) {
            int cameraId = camera.identity.id;

            if (!cameraRuntimes.containsKey(cameraId)) {
                logger.info("Starting camera runtime: " + camera.identity.name);
                CameraRuntime cameraRuntime = cameraRuntimeFactory.create(camera);
                cameraRuntimes.put(cameraId, cameraRuntime);
                cameraRuntime.start();
            }
        }
    }

    public synchronized void disconnect(CameraSet cameraSet) {
        ValidationSupport.validateRequired("Camera set", cameraSet);

        for (Camera camera : cameraSet.cameras()) {
            int cameraId = camera.identity.id;

            CameraRuntime cameraRuntime = cameraRuntimes.getOrDefault(cameraId, null);

            if (cameraRuntime != null) {
                logger.info("Stopping camera runtime: " + camera.identity.name);
                cameraRuntime.stop();
                cameraRuntimes.remove(cameraId);
            }
        }
    }

    public synchronized void disconnect(int cameraId) {
        CameraRuntime cameraRuntime = cameraRuntimes.getOrDefault(cameraId, null);

        if (cameraRuntime != null) {
            logger.info("Stopping camera runtime: " + cameraId);
            cameraRuntime.stop();
            cameraRuntimes.remove(cameraId);
        }
    }

    public synchronized void disconnectAll() {
        for (CameraRuntime cameraRuntime : List.copyOf(cameraRuntimes.values())) {
            logger.info("Stopping camera runtime.");
            cameraRuntime.stop();
        }

        cameraRuntimes.clear();
    }

    public synchronized void closeSystemQueues() {
        systemMessageBus.close();
    }
}
