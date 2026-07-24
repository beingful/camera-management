package camera.connection;

import camera.logging.ILogger;
import camera.messaging.IQueueSubscriber;
import camera.models.StreamingSettings;
import camera.retry.Retry;
import camera.retry.RetryPolicy;
import camera.retry.RetryWaitStrategy;
import camera.threading.IThreadPool;
import camera.models.Connection;
import camera.validation.ValidationSupport;
import org.opencv.core.Mat;

public class CameraConnectivityFactory {
    public CameraConnectivityFactory() {
    }

    public ICameraConnectivityController create(
            Connection cameraConnection,
            StreamingSettings streamingSettings,
            IQueueSubscriber<Mat> frameSubscriber,
            IThreadPool threadPool,
            ILogger logger) {
        ValidationSupport.validateRequired("Camera connection", cameraConnection);
        ValidationSupport.validateRequired("Frame subscriber", frameSubscriber);
        ValidationSupport.validateRequired("Thread pool", threadPool);
        ValidationSupport.validateRequired("Logger", logger);

        CameraStreamReader cameraStreamReader = new CameraStreamReader(
                cameraConnection,
                streamingSettings,
                frameSubscriber,
                threadPool,
                logger,
                createRetry());

        return new CameraConnectivityController(
                new CameraConnectivityService(cameraStreamReader, logger),
                logger);
    }

    private Retry createRetry() {
        return new Retry(new RetryPolicy(3, RetryWaitStrategy.Lineal, 1000, 30000));
    }
}
