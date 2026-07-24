package camera.threading;

import camera.error.SurveillanceError;
import camera.logging.LoggerFactory;
import camera.logging.ILogger;
import camera.messaging.IPushMessageQueue;
import camera.models.Camera;
import camera.validation.ValidationSupport;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ThreadPoolManager {
    private static final int SYSTEM_THREAD_COUNT = 1;
    private static final int SURVEILLANCE_ERROR_THREAD_COUNT = 1;
    private static final int CAMERA_THREAD_COUNT = 3;

    private final SystemThreadPool systemThreadPool;
    private final SystemThreadPool surveillanceErrorThreadPool;
    private final Map<Integer, CameraThreadPool> cameraThreadPools;
    private final IPushMessageQueue<SurveillanceError> errorQueue;
    private final ILogger logger;

    public ThreadPoolManager(IPushMessageQueue<SurveillanceError> errorQueue) {
        ValidationSupport.validateRequired("Error queue", errorQueue);

        this.errorQueue = errorQueue;
        logger = LoggerFactory.systemLogger(ThreadPoolManager.class);
        systemThreadPool = new SystemThreadPool(SYSTEM_THREAD_COUNT, errorQueue);
        surveillanceErrorThreadPool = new SystemThreadPool(SURVEILLANCE_ERROR_THREAD_COUNT, errorQueue);
        cameraThreadPools = new HashMap<>();
    }

    public IThreadPool systemThreadPool() {
        return systemThreadPool;
    }

    public IThreadPool surveillanceErrorThreadPool() {
        return surveillanceErrorThreadPool;
    }

    public synchronized void createCameraThreadPools(List<Camera> cameras) {
        ValidationSupport.validateRequired("Cameras", cameras);

        for (Camera camera : cameras) {
            ValidationSupport.validateRequired("Camera", camera);

            CameraThreadPool existingCameraThreadPool = cameraThreadPools.remove(camera.identity.id);

            if (existingCameraThreadPool != null) {
                logger.warning("Replacing existing camera thread pool: " + camera.identity.name);
                existingCameraThreadPool.shutdown();
            }

            logger.info("Creating camera thread pool: " + camera.identity.name);
            cameraThreadPools.put(
                    camera.identity.id,
                    new CameraThreadPool(CAMERA_THREAD_COUNT, camera.identity, errorQueue));
        }
    }

    public synchronized IThreadPool cameraThreadPool(Camera camera) {
        ValidationSupport.validateRequired("Camera", camera);

        CameraThreadPool cameraThreadPool = cameraThreadPools.get(camera.identity.id);

        if (cameraThreadPool == null) {
            throw new IllegalStateException("Camera thread pool does not exist: " + camera.identity.name);
        }

        return cameraThreadPool;
    }

    public synchronized void shutdownCameraThreadPools(List<Camera> cameras) {
        ValidationSupport.validateRequired("Cameras", cameras);

        for (Camera camera : cameras) {
            ValidationSupport.validateRequired("Camera", camera);

            CameraThreadPool cameraThreadPool = cameraThreadPools.remove(camera.identity.id);

            if (cameraThreadPool != null) {
                logger.warning("Shutting down camera thread pool: " + camera.identity.name);
                cameraThreadPool.shutdown();
            }
        }
    }

    public synchronized void shutdownCameraThreadPool(int cameraId) {
        CameraThreadPool cameraThreadPool = cameraThreadPools.remove(cameraId);

        if (cameraThreadPool != null) {
            logger.warning("Shutting down camera thread pool: " + cameraId);
            cameraThreadPool.shutdown();
        }
    }

    public synchronized void shutdown() {
        logger.warning("Shutting down all camera thread pools.");
        systemThreadPool.shutdown();
        surveillanceErrorThreadPool.shutdown();

        for (CameraThreadPool cameraThreadPool : cameraThreadPools.values()) {
            cameraThreadPool.shutdown();
        }

        cameraThreadPools.clear();
    }
}
