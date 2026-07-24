package camera.recording;

import camera.logging.ILogger;
import camera.messaging.IPullMessageQueue;
import camera.messaging.QueueConsumer;
import camera.messaging.SharedMessage;
import camera.recording.video.file.VideoFileWriter;
import camera.retry.Retry;
import camera.threading.IThreadPool;
import camera.threading.InLoopTaskExecutor;
import camera.threading.ThreadServiceType;
import camera.validation.ValidationSupport;
import org.opencv.core.Mat;

public class CameraStreamWriter extends InLoopTaskExecutor {
    private final IPullMessageQueue<SharedMessage<Mat>> cameraFrameQueue;
    private final VideoFileWriter videoFileWriter;
    private final ILogger logger;

    public CameraStreamWriter(
            IPullMessageQueue<SharedMessage<Mat>> cameraFrameQueue,
            VideoFileWriter videoFileWriter,
            IThreadPool threadPool,
            ILogger logger,
            Retry retry
    ) {
        super(ThreadServiceType.StreamWriter, threadPool, logger, retry);

        ValidationSupport.validateRequired("Camera frame queue", cameraFrameQueue);
        ValidationSupport.validateRequired("Video file writer", videoFileWriter);
        ValidationSupport.validateRequired("Logger", logger);

        this.cameraFrameQueue = cameraFrameQueue;
        this.videoFileWriter = videoFileWriter;
        this.logger = logger;

        this.cameraFrameQueue.subscribe(new QueueConsumer<>());
    }

    public synchronized void start() {
        if (!executing) {
            logger.info("Starting stream writer.");
            startTask();
        }
    }

    public void stop() {
        stopTask();
    }

    @Override
    protected void execute() {
        SharedMessage<Mat> frame = cameraFrameQueue.dequeue();

        if (frame != null) {
            try {
                videoFileWriter.write(frame.message());
            }
            finally {
                frame.close();
            }
        }
    }

    @Override
    protected void beforeLoop() {
        videoFileWriter.start();
    }

    @Override
    protected void cleanup() {
        videoFileWriter.stop();
    }
}
