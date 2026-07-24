package camera.retry;

import camera.validation.ValidationSupport;

import java.util.concurrent.TimeUnit;

public class Retry {
    private final RetryPolicy retryPolicy;
    private int failureCounter;

    public Retry(RetryPolicy retryPolicy) {
        ValidationSupport.validateRequired("Retry policy", retryPolicy);

        this.retryPolicy = retryPolicy;
        this.failureCounter = 0;
    }

    public void tryExecute(RetryTask task) throws Exception {
        ValidationSupport.validateRequired("Retry task", task);

        failureCounter = 0;

        while (true) {
            try {
                task.execute();
                failureCounter = 0;
                return;
            }
            catch (InterruptedException exception) {
                Thread.currentThread().interrupt();
                throw exception;
            }
            catch (Exception exception) {
                failureCounter++;

                if (failureCounter > retryPolicy.retryCount) {
                    throw new IllegalStateException("Retry count exceeded.", exception);
                }

                TimeUnit.MILLISECONDS.sleep(calculateWaitTime());
            }
        }
    }

    private long calculateWaitTime() {
        long waitTime = switch (retryPolicy.retryWaitStrategy) {
            case Lineal -> retryPolicy.baseWaitTimeMilliseconds * failureCounter;
        };

        return Math.min(waitTime, retryPolicy.maximumWaitTimeMilliseconds);
    }
}
