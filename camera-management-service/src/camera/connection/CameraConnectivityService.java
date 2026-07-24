package camera.connection;

import camera.logging.ILogger;
import camera.validation.ValidationSupport;

public class CameraConnectivityService {
    private final CameraStreamReader streamReader;
    private final ILogger logger;

    public CameraConnectivityService(CameraStreamReader streamReader, ILogger logger) {
        ValidationSupport.validateRequired("Stream reader", streamReader);
        ValidationSupport.validateRequired("Logger", logger);

        this.streamReader = streamReader;
        this.logger = logger;
    }

    public void connect() {
        logger.info("Connecting camera stream.");
        streamReader.start();
    }

    public void disconnect() {
        logger.info("Disconnecting camera stream.");
        streamReader.stop();
    }
}
