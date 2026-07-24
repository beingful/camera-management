package camera.logging;

public interface ILogger {
    void info(String message);

    void warning(String message);

    void error(String message, Throwable exception);

    void severe(String message, Throwable exception);
}
