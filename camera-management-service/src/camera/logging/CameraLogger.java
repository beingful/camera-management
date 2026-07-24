package camera.logging;

import camera.models.Identity;

import java.util.logging.Logger;

public class CameraLogger extends SystemLogger {
    private final Identity cameraIdentity;

    CameraLogger(Logger logger, Identity cameraIdentity) {
        super(logger);

        this.cameraIdentity = cameraIdentity;
    }

    @Override
    public void info(String message) {
        super.info(format(message));
    }

    @Override
    public void warning(String message) {
        super.warning(format(message));
    }

    @Override
    public void error(String message, Throwable exception) {
        super.error(format(message), exception);
    }

    @Override
    public void severe(String message, Throwable exception) {
        super.severe(format(message), exception);
    }

    public Identity cameraIdentity() {
        return cameraIdentity;
    }

    private String format(String message) {
        return "[cameraId=" + cameraIdentity.id + ", cameraName=" + cameraIdentity.name + "] " + message;
    }
}
