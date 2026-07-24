package camera.connection;

import camera.models.Connection;

import camera.logging.ILogger;
import camera.messaging.IQueueSubscriber;
import camera.models.StreamingSettings;
import camera.retry.Retry;
import camera.threading.IThreadPool;
import camera.threading.InLoopTaskExecutor;
import camera.threading.ThreadServiceType;
import camera.validation.ValidationSupport;
import org.opencv.core.Mat;
import org.opencv.videoio.VideoCapture;

public class CameraStreamReader extends InLoopTaskExecutor {
    private final Connection connection;
    private final StreamingSettings streamingSettings;
    private final IQueueSubscriber<Mat> frameSubscriber;
    private final ILogger logger;

    private VideoCapture videoCapture;

    public CameraStreamReader(
            Connection connection,
            StreamingSettings streamingSettings,
            IQueueSubscriber<Mat> frameSubscriber,
            IThreadPool threadPool,
            ILogger logger,
            Retry retry) {
        super(ThreadServiceType.StreamReader, threadPool, logger, retry);

        ValidationSupport.validateRequired("Camera connection", connection);
        ValidationSupport.validateRequired("Camera streaming settings", streamingSettings);
        ValidationSupport.validateRequired("Frame subscriber", frameSubscriber);
        ValidationSupport.validateRequired("Logger", logger);

        this.connection = connection;
        this.streamingSettings = streamingSettings;
        this.frameSubscriber = frameSubscriber;
        this.logger = logger;
    }

    public synchronized void start() {
        logger.info("Starting stream reader.");
        startTask();
    }

    public void stop() {
        stopTask();
    }

    @Override
    protected void beforeLoop() {
        videoCapture = new VideoCapture(
                connection.url, streamingSettings.streamingServiceCode);

        if (!videoCapture.isOpened()) {
            throw new IllegalStateException("Camera stream could not be opened: " + connection.url);
        }
    }

    @Override
    protected void execute() {
        Mat frame = new Mat();

        if (videoCapture.isOpened() && videoCapture.read(frame)) {
            frameSubscriber.push(frame);
        }
        else {
            frame.release();
        }
    }

    @Override
    protected void cleanup() {
        if (videoCapture != null) {
            videoCapture.release();
            videoCapture = null;
        }
    }
}
