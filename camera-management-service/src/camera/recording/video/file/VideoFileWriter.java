package camera.recording.video.file;

import camera.logging.ILogger;
import camera.messaging.IPushMessageQueue;
import camera.recording.video.VideoSettings;
import camera.recording.video.VideoWriterFactory;
import camera.recording.video.session.VideoFile;
import camera.recording.video.session.VideoSessionManager;
import camera.validation.ValidationSupport;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoWriter;

public class VideoFileWriter {
    private final VideoSettings videoFileSettings;
    private final IPushMessageQueue<VideoFile> videoFileQueue;
    private final VideoWriterFactory videoWriterFactory;
    private final VideoSessionManager videoSessionManager;
    private final ILogger logger;

    private VideoWriter videoWriter;
    private VideoFile videoFile;

    public VideoFileWriter(
            VideoSettings videoFileSettings,
            IPushMessageQueue<VideoFile> videoFileQueue,
            VideoWriterFactory videoWriterFactory,
            VideoSessionManager videoSessionManager,
            ILogger logger) {
        ValidationSupport.validateRequired("Video file settings", videoFileSettings);
        ValidationSupport.validateRequired("Video file queue", videoFileQueue);
        ValidationSupport.validateRequired("Video writer factory", videoWriterFactory);
        ValidationSupport.validateRequired("Video session manager", videoSessionManager);
        ValidationSupport.validateRequired("Logger", logger);

        this.videoFileSettings = videoFileSettings;
        this.videoFileQueue = videoFileQueue;
        this.videoWriterFactory = videoWriterFactory;
        this.videoSessionManager = videoSessionManager;
        this.logger = logger;
    }

    public synchronized void start() {
        if (videoFile == null) {
            startNewWritingSession();
        }
    }

    public synchronized void stop() {
        if (videoFile != null) {
            completeWritingSession();
        }
    }

    public synchronized void write(Mat mat) {
        if (mat != null && !mat.empty()) {
            if (!videoFile.session.isAlive()) {
                completeWritingSession();
                startNewWritingSession();
            }

            Mat writableFrame = normalizeFrame(mat);

            try {
                videoWriter.write(writableFrame);
            }
            finally {
                if (writableFrame != mat) {
                    writableFrame.release();
                }
            }
        }
    }

    private Mat normalizeFrame(Mat frame) {
        Mat normalizedFrame = frame;

        if (frame.channels() == 1) {
            normalizedFrame = new Mat();
            Imgproc.cvtColor(frame, normalizedFrame, Imgproc.COLOR_GRAY2BGR);
        }
        else if (frame.channels() == 4) {
            normalizedFrame = new Mat();
            Imgproc.cvtColor(frame, normalizedFrame, Imgproc.COLOR_BGRA2BGR);
        }

        Size expectedSize = videoFileSettings.frameSettings().size;
        if (normalizedFrame.width() == (int) expectedSize.width
                && normalizedFrame.height() == (int) expectedSize.height) {
            return normalizedFrame;
        }

        Mat resizedFrame = new Mat();
        Imgproc.resize(normalizedFrame, resizedFrame, expectedSize);

        if (normalizedFrame != frame) {
            normalizedFrame.release();
        }

        return resizedFrame;
    }

    private void completeWritingSession() {
        logger.info("Completing video writing session.");

        if (videoWriter != null) {
            videoWriter.release();
            videoWriter = null;
        }

        videoSessionManager.terminateSession(videoFile.session);

        logger.info("Publishing completed video file.");
        videoFileQueue.enqueue(videoFile);

        videoFile = null;
    }

    private void startNewWritingSession() {
        logger.info("Starting new video writing session.");

        videoFile = new VideoFile(
                videoFileSettings.fileSettings(),
                videoSessionManager.refreshSession());

        VideoSettings videoSettings = new VideoSettings(
                new FileSettings(videoFile.getSessionIdBasedFilePath()),
                videoFileSettings.frameSettings(),
                videoFileSettings.streamingSettings());

        videoWriter = videoWriterFactory.create(videoSettings);
    }
}
