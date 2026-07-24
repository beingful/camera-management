package camera.threading;

import camera.error.SurveillanceError;
import camera.logging.LoggerFactory;
import camera.logging.ILogger;
import camera.messaging.IPushMessageQueue;
import camera.models.Identity;
import camera.validation.ValidationSupport;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class CameraThreadPool implements IThreadPool {
    private final ThreadPoolExecutor executor;
    private final Identity cameraIdentity;
    private final IPushMessageQueue<SurveillanceError> errorQueue;
    private final ILogger logger;

    public CameraThreadPool(int threadCount, Identity cameraIdentity, IPushMessageQueue<SurveillanceError> errorQueue) {
        if (threadCount < 1) {
            throw new IllegalArgumentException("Thread count must be greater than 0.");
        }
        ValidationSupport.validateRequired("Camera identity", cameraIdentity);
        ValidationSupport.validateRequired("Error queue", errorQueue);

        this.cameraIdentity = cameraIdentity;
        this.errorQueue = errorQueue;
        this.logger = LoggerFactory.cameraLogger(CameraThreadPool.class, cameraIdentity);
        this.executor = new ThreadPoolExecutor(
                threadCount,
                threadCount,
                5,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(),
                new WorkerThreadFactory());
        this.executor.allowCoreThreadTimeOut(true);
    }

    @Override
    public ManagedThread submit(ThreadServiceType serviceType, Runnable task) {
        ValidationSupport.validateRequired("Thread service type", serviceType);
        ValidationSupport.validateRequired("Thread pool task", task);

        if (!serviceType.isCameraSpecific()) {
            throw new IllegalArgumentException("Camera thread pool requires camera-specific service type.");
        }

        String threadName = serviceType.resolveThreadName(cameraIdentity.name);

        return ManagedThread.submit(executor, task, cameraIdentity.id, threadName, errorQueue);
    }

    @Override
    public void shutdown() {
        logger.warning("Shutting down camera thread pool now.");
        executor.shutdownNow();
    }

    private static class WorkerThreadFactory implements java.util.concurrent.ThreadFactory {
        private final AtomicInteger threadNumber = new AtomicInteger();

        @Override
        public java.lang.Thread newThread(Runnable runnable) {
            return new java.lang.Thread(runnable, "camera-worker-" + threadNumber.incrementAndGet());
        }
    }
}
