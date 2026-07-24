package camera.retry;

@FunctionalInterface
public interface RetryTask {
    void execute() throws Exception;
}
