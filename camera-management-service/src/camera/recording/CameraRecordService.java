package camera.recording;

import camera.logging.ILogger;
import camera.validation.ValidationSupport;

public class CameraRecordService {
    private final CameraStreamWriter streamWriter;
    private final ILogger logger;

    public CameraRecordService(CameraStreamWriter cameraStreamWriter, ILogger logger) {
        ValidationSupport.validateRequired("Camera stream writer", cameraStreamWriter);
        ValidationSupport.validateRequired("Logger", logger);

        this.streamWriter = cameraStreamWriter;
        this.logger = logger;
    }

    public void start() {
        logger.info("Starting camera recording service.");
        streamWriter.start();
    }

    public void stop() {
        logger.info("Stopping camera recording service.");
        streamWriter.stop();
    }
}
