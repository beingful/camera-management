package camera.runtime;

import camera.connection.ICameraConnectivityController;
import camera.logging.ILogger;
import camera.messaging.MessageBus;
import camera.recording.ICameraRecordController;
import camera.validation.ValidationSupport;

public class CameraRuntime {
    private final ICameraConnectivityController cameraConnectivityController;
    private final ICameraRecordController cameraRecordController;
    private final MessageBus messageBus;
    private final ILogger logger;

    private boolean running;

    public CameraRuntime(
            ICameraConnectivityController cameraConnectivityController,
            ICameraRecordController cameraRecordController,
            MessageBus messageBus,
            ILogger logger) {
        ValidationSupport.validateRequired("Camera connectivity controller", cameraConnectivityController);
        ValidationSupport.validateRequired("Camera record controller", cameraRecordController);
        ValidationSupport.validateRequired("Message bus", messageBus);
        ValidationSupport.validateRequired("Logger", logger);

        this.cameraConnectivityController = cameraConnectivityController;
        this.cameraRecordController = cameraRecordController;
        this.messageBus = messageBus;
        this.logger = logger;
        this.running = false;
    }

    public boolean start() {
        if (running) {
            return false;
        }

        logger.info("Starting camera runtime.");
        cameraRecordController.start();
        cameraConnectivityController.connect();
        running = true;

        return true;
    }

    public boolean stop() {
        if (!running) {
            return false;
        }

        logger.info("Stopping camera runtime.");
        try {
            cameraRecordController.stop();
        }
        finally {
            try {
                cameraConnectivityController.disconnect();
            }
            finally {
                messageBus.close();
                running = false;
            }
        }

        return true;
    }
}
