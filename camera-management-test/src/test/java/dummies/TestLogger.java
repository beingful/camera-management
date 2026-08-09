package dummies;

import camera.logging.ILogger;

public class TestLogger implements ILogger {
    @Override
    public void info(String message) {
    }

    @Override
    public void warning(String message) {
    }

    @Override
    public void error(String message, Throwable exception) {
    }

    @Override
    public void severe(String message, Throwable exception) {
    }
}
