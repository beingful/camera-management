package camera.threading;

import camera.error.SurveillanceError;
import camera.logging.ILogger;
import camera.logging.LoggerFactory;
import camera.messaging.IPushMessageQueue;
import camera.validation.ValidationSupport;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class SystemThreadPool implements IThreadPool {
    private final ThreadPoolExecutor executor;
    private final IPushMessageQueue<SurveillanceError> errorQueue;
    private final ILogger logger;

    public SystemThreadPool(int threadCount, IPushMessageQueue<SurveillanceError> errorQueue) {
        if (threadCount < 1) {
            throw new IllegalArgumentException("Thread count must be greater than 0.");
        }
        ValidationSupport.validateRequired("Error queue", errorQueue);

        this.errorQueue = errorQueue;
        this.logger = LoggerFactory.systemLogger(SystemThreadPool.class);
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

        if (serviceType.isCameraSpecific()) {
            throw new IllegalArgumentException("System thread pool requires non-camera-specific service type.");
        }

        String threadName = serviceType.resolveThreadName(null);

        return ManagedThread.submit(executor, task, SurveillanceError.SYSTEM_CAMERA_ID, threadName, errorQueue);
    }

    @Override
    public void shutdown() {
        logger.warning("Shutting down system thread pool now.");
        executor.shutdownNow();
    }

    private static class WorkerThreadFactory implements java.util.concurrent.ThreadFactory {
        private final AtomicInteger threadNumber = new AtomicInteger();

        @Override
        public java.lang.Thread newThread(Runnable runnable) {
            return new java.lang.Thread(runnable, "system-worker-" + threadNumber.incrementAndGet());
        }
    }
}
