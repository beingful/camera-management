package camera.recording;

import camera.logging.ILogger;
import camera.messaging.IPullMessageQueue;
import camera.messaging.IPushMessageQueue;
import camera.messaging.SharedMessage;
import camera.models.FrameSettings;
import camera.models.StorageSettings;
import camera.models.StreamingSettings;
import camera.recording.video.VideoSettings;
import camera.recording.video.VideoWriterFactory;
import camera.recording.video.file.FileSettings;
import camera.recording.video.file.VideoFileWriter;
import camera.recording.video.session.VideoFile;
import camera.retry.Retry;
import camera.retry.RetryPolicy;
import camera.retry.RetryWaitStrategy;
import camera.threading.IThreadPool;
import camera.recording.video.session.VideoSessionManager;
import camera.validation.ValidationSupport;
import org.opencv.core.Mat;

import java.util.UUID;

public class CameraRecorderFactory {
    private final IPushMessageQueue<VideoFile> videoFileQueue;
    private final VideoWriterFactory videoWriterFactory;
    private final VideoSessionManager videoSessionManager;

    public CameraRecorderFactory(
            IPushMessageQueue<VideoFile> videoFileQueue,
            VideoWriterFactory videoWriterFactory,
            VideoSessionManager videoSessionManager) {
        ValidationSupport.validateRequired("Video file queue", videoFileQueue);
        ValidationSupport.validateRequired("Video writer factory", videoWriterFactory);
        ValidationSupport.validateRequired("Video session manager", videoSessionManager);

        this.videoFileQueue = videoFileQueue;
        this.videoWriterFactory = videoWriterFactory;
        this.videoSessionManager = videoSessionManager;
    }

    public CameraRecordService create(
            FrameSettings cameraFrameSettings,
            StreamingSettings cameraStreamingSettings,
            StorageSettings cameraStorageSettings,
            IPullMessageQueue<SharedMessage<Mat>> cameraFrameQueue,
            IThreadPool threadPool,
            ILogger logger) {
        ValidationSupport.validateRequired("Camera frame settings", cameraFrameSettings);
        ValidationSupport.validateRequired("Camera streaming settings", cameraStreamingSettings);
        ValidationSupport.validateRequired("Camera storage settings", cameraStorageSettings);
        ValidationSupport.validateRequired("Camera frame queue", cameraFrameQueue);
        ValidationSupport.validateRequired("Thread pool", threadPool);
        ValidationSupport.validateRequired("Logger", logger);

        VideoFileWriter videoFileWriter = createVideoFileWriter(
                cameraFrameSettings, cameraStreamingSettings, cameraStorageSettings, logger);

        CameraStreamWriter cameraStreamWriter =
                new CameraStreamWriter(
                        cameraFrameQueue,
                        videoFileWriter,
                        threadPool,
                        logger,
                        createRetry());

        return new CameraRecordService(cameraStreamWriter, logger);
    }

    public VideoFileWriter createVideoFileWriter(
            FrameSettings cameraFrameSettings,
            StreamingSettings cameraStreamingSettings,
            StorageSettings cameraStorageSettings,
            ILogger logger) {
        VideoSettings videoSettings = new VideoSettings(
                new FileSettings(
                        cameraStorageSettings.directoryPath,
                        UUID.randomUUID().toString(),
                        cameraStorageSettings.fileExtension),
                cameraFrameSettings,
                cameraStreamingSettings);

        return new VideoFileWriter(videoSettings, videoFileQueue, videoWriterFactory, videoSessionManager, logger);
    }

    private Retry createRetry() {
        return new Retry(new RetryPolicy(3, RetryWaitStrategy.Lineal, 1000, 30000));
    }
}
