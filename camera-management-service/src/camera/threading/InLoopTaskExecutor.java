package camera.threading;

import camera.logging.ILogger;
import camera.retry.Retry;
import camera.validation.ValidationSupport;

import java.util.concurrent.TimeUnit;

public abstract class InLoopTaskExecutor {
    private static final long STOP_TIMEOUT_SECONDS = 30;

    private final ThreadServiceType serviceType;
    private final IThreadPool threadPool;
    private final ILogger logger;
    private final Retry retry;

    private ManagedThread task;
    protected volatile boolean executing;

    protected InLoopTaskExecutor(
            ThreadServiceType serviceType,
            IThreadPool threadPool,
            ILogger logger,
            Retry retry) {
        ValidationSupport.validateRequired("Thread service type", serviceType);
        ValidationSupport.validateRequired("Thread pool", threadPool);
        ValidationSupport.validateRequired("Logger", logger);
        ValidationSupport.validateRequired("Retry", retry);

        this.serviceType = serviceType;
        this.threadPool = threadPool;
        this.logger = logger;
        this.retry = retry;
        this.executing = false;
    }

    protected synchronized boolean startTask() {
        if (task != null) {
            return false;
        }

        executing = true;
        task = threadPool.submit(serviceType, this::run);

        return true;
    }

    protected boolean stopTask() {
        ManagedThread currentTask;

        synchronized (this) {
            currentTask = task;
            executing = false;

            if (currentTask == null) {
                return false;
            }
        }

        logger.warning("Interrupting loop task: " + serviceType);

        currentTask.interrupt();

        try {
            if (!currentTask.awaitStopped(STOP_TIMEOUT_SECONDS, TimeUnit.SECONDS)) {
                logger.warning("Loop task did not stop within timeout: " + serviceType);
            }
        }
        catch (InterruptedException exception) {
            logger.error("Interrupted while waiting for loop task to stop: " + serviceType, exception);
            java.lang.Thread.currentThread().interrupt();
        }

        return true;
    }

    protected boolean canExecute() {
        return true;
    }

    protected abstract void execute() throws Exception;

    protected void run() {
        try {
            beforeLoop();

            retry.tryExecute(this::performTask);
        }
        catch (InterruptedException exception) {
            logger.error("Loop task thread was interrupted: " + serviceType, exception);
            java.lang.Thread.currentThread().interrupt();
        }
        catch (Exception exception) {
            logger.severe("Loop task failed: " + serviceType, exception);
            throw new IllegalStateException("Loop task failed: " + serviceType, exception);
        }
        finally {
            if (java.lang.Thread.currentThread().isInterrupted()) {
                logger.warning("Loop task thread was interrupted: " + serviceType);
            }

            try {
                afterLoop();
                cleanup();
            }
            catch (Exception exception) {
                logger.severe("Loop task cleanup failed: " + serviceType, exception);
            }
            finally {
                synchronized (this) {
                    task = null;
                    executing = false;
                }
            }
        }
    }

    private void performTask() throws Exception {
        while (executing && canExecute() && !java.lang.Thread.currentThread().isInterrupted()) {
            execute();
        }
    }

    protected void beforeLoop() throws Exception {
    }

    protected void afterLoop() {
    }

    protected void cleanup() {
    }
}
