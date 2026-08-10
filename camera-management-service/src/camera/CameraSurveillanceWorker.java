package camera;

import camera.error.IErrorSubscriber;
import camera.error.SurveillanceError;
import camera.logging.LoggerFactory;
import camera.logging.ILogger;
import camera.validation.ValidationSupport;

public class CameraSurveillanceWorker implements IErrorSubscriber {
    private final CameraSurveillanceController cameraSurveillanceController;
    private final ILogger logger;

    public CameraSurveillanceWorker() {
        CameraSurveillanceServiceProvider serviceProvider =
                new CameraSurveillanceServiceProvider();

        this.logger = LoggerFactory.systemLogger(CameraSurveillanceWorker.class);
        this.cameraSurveillanceController = serviceProvider.getController(this);
        registerShutdownHook();
    }

    public void start() {
        logger.info("Starting camera surveillance worker.");
        cameraSurveillanceController.connect();
    }

    public void stop() {
        logger.info("Stopping camera surveillance worker.");
        cameraSurveillanceController.disconnect();
    }

    private void registerShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            logger.info("Camera surveillance worker shutdown hook invoked.");
            cameraSurveillanceController.shutdown();
        }, "camera-surveillance-shutdown-hook"));
    }

    @Override
    public synchronized void onError(SurveillanceError error) {
        ValidationSupport.validateRequired("Error", error);

        if (error.isCameraError) {
            logger.error("Camera error detected; disconnecting camera: " + error.cameraId, error.internalError);
            cameraSurveillanceController.disconnectCamera(error.cameraId);
        }
        else {
            logger.error("System error detected; disconnecting all cameras.", error.internalError);
            cameraSurveillanceController.shutdown();
        }
    }
}
