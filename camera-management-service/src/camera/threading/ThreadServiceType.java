package camera.threading;

import camera.validation.ValidationSupport;

public enum ThreadServiceType {
    ConfigurationFileWatcher("configuration-file-watcher", false),
    SurveillanceError("surveillance-error-handler", false),
    StreamReader("stream-reader", true),
    StreamWriter("stream-writer", true),
    MotionDetection("motion-detection", true);

    private final String threadName;
    private final boolean cameraSpecific;

    ThreadServiceType(String threadName, boolean cameraSpecific) {
        this.threadName = threadName;
        this.cameraSpecific = cameraSpecific;
    }

    public String resolveThreadName(String cameraName) {
        if (cameraSpecific) {
            ValidationSupport.validateNotBlank("Camera name", cameraName);
            return cameraName + "-" + threadName;
        }

        return threadName;
    }

    public boolean isCameraSpecific() {
        return cameraSpecific;
    }
}
