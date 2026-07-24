package camera.logging;

import java.util.logging.Level;
import java.util.logging.Logger;

public class SystemLogger implements ILogger {
    private final Logger logger;

    SystemLogger(Logger logger) {
        this.logger = logger;
    }

    @Override
    public void info(String message) {
        logger.info(message);
    }

    @Override
    public void warning(String message) {
        logger.warning(message);
    }

    @Override
    public void error(String message, Throwable exception) {
        logger.log(Level.SEVERE, message, exception);
    }

    @Override
    public void severe(String message, Throwable exception) {
        logger.log(Level.SEVERE, message, exception);
    }
}
