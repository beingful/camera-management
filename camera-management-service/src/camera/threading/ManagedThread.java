package camera.threading;

import camera.error.SurveillanceError;
import camera.messaging.IPushMessageQueue;
import camera.validation.ValidationSupport;

import java.util.concurrent.Future;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class ManagedThread {
    private final Future<?> task;
    private final CountDownLatch stopped;
    private final int cameraId;
    private final String name;

    private ManagedThread(Future<?> task, CountDownLatch stopped, int cameraId, String name) {
        ValidationSupport.validateRequired("Thread task", task);
        ValidationSupport.validateRequired("Stopped latch", stopped);
        ValidationSupport.validateNotBlank("Thread name", name);

        this.task = task;
        this.stopped = stopped;
        this.cameraId = cameraId;
        this.name = name;
    }

    public static ManagedThread submit(
            java.util.concurrent.ExecutorService executor,
            Runnable task,
            int cameraId,
            String name,
            IPushMessageQueue<SurveillanceError> errorQueue) {
        ValidationSupport.validateRequired("Executor", executor);
        ValidationSupport.validateRequired("Thread task", task);
        ValidationSupport.validateNotBlank("Thread name", name);
        ValidationSupport.validateRequired("Error queue", errorQueue);

        CountDownLatch stopped = new CountDownLatch(1);
        Future<?> future = executor.submit(() -> {
            java.lang.Thread currentThread = java.lang.Thread.currentThread();
            String previousName = currentThread.getName();

            currentThread.setName(name);

            try {
                task.run();
            }
            catch (Throwable throwable) {
                try {
                    errorQueue.enqueue(new SurveillanceError(cameraId, throwable));
                }
                catch (Exception exception) {
                    throwable.addSuppressed(exception);
                }

                throw throwable;
            }
            finally {
                currentThread.setName(previousName);
                stopped.countDown();
            }
        });

        return new ManagedThread(future, stopped, cameraId, name);
    }

    public void interrupt() {
        task.cancel(true);
    }

    public boolean isInterrupted() {
        return task.isCancelled();
    }

    public boolean isAlive() {
        return !task.isDone();
    }

    public boolean awaitStopped(long timeout, TimeUnit timeUnit) throws InterruptedException {
        ValidationSupport.validateRequired("Time unit", timeUnit);

        return stopped.await(timeout, timeUnit);
    }

    public int cameraId() {
        return cameraId;
    }

    public String name() {
        return name;
    }
}
