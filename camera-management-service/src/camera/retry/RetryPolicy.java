package camera.retry;

import camera.validation.ValidationSupport;

public class RetryPolicy {
    public final int retryCount;
    public final RetryWaitStrategy retryWaitStrategy;
    public final long baseWaitTimeMilliseconds;
    public final long maximumWaitTimeMilliseconds;

    public RetryPolicy(
            int retryCount,
            RetryWaitStrategy retryWaitStrategy,
            long baseWaitTimeMilliseconds,
            long maximumWaitTimeMilliseconds) {
        ValidationSupport.validateRequired("Retry wait strategy", retryWaitStrategy);

        if (retryCount < 0) {
            throw new IllegalArgumentException("Retry count must be greater than or equal to 0.");
        }

        if (baseWaitTimeMilliseconds < 0) {
            throw new IllegalArgumentException("Base wait time must be greater than or equal to 0.");
        }

        if (maximumWaitTimeMilliseconds < 0) {
            throw new IllegalArgumentException("Maximum wait time must be greater than or equal to 0.");
        }

        this.retryCount = retryCount;
        this.retryWaitStrategy = retryWaitStrategy;
        this.baseWaitTimeMilliseconds = baseWaitTimeMilliseconds;
        this.maximumWaitTimeMilliseconds = maximumWaitTimeMilliseconds;
    }
}
