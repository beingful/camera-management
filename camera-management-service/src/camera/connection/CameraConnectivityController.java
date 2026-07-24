package camera.connection;

import camera.logging.ILogger;
import camera.validation.ValidationSupport;

public class CameraConnectivityController implements ICameraConnectivityController {
    private final CameraConnectivityService cameraConnectivityService;
    private final ILogger logger;
    private boolean isConnected;

    public CameraConnectivityController(CameraConnectivityService cameraConnectivityService, ILogger logger) {
        ValidationSupport.validateRequired("Camera connectivity service", cameraConnectivityService);
        ValidationSupport.validateRequired("Logger", logger);

        this.cameraConnectivityService = cameraConnectivityService;
        this.logger = logger;
        this.isConnected = false;
    }

    @Override
    public synchronized void connect() {
        if (isConnected) {
            return;
        }

        logger.info("Connecting camera.");
        cameraConnectivityService.connect();
        isConnected = true;
    }

    @Override
    public synchronized boolean isConnected() {
        return isConnected;
    }

    @Override
    public synchronized void disconnect() {
        if (!isConnected) {
            return;
        }

        logger.info("Disconnecting camera.");
        cameraConnectivityService.disconnect();
        isConnected = false;
    }
}
