package camera.recording.video.file;

import camera.logging.ILogger;
import camera.messaging.IPushMessageQueue;
import camera.messaging.IQueueSubscriber;
import camera.recording.video.session.VideoFile;
import camera.validation.ValidationSupport;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class VideoFileProcessor implements IQueueSubscriber<VideoFile> {
    private final ILogger logger;

    public VideoFileProcessor(IPushMessageQueue<VideoFile> videoFileQueue, ILogger logger) {
        ValidationSupport.validateRequired("Video file queue", videoFileQueue);
        ValidationSupport.validateRequired("Logger", logger);

        this.logger = logger;
        videoFileQueue.subscribe(this);
    }

    @Override
    public void push(VideoFile videoFile) {
        ValidationSupport.validateRequired("Video file", videoFile);

        Path temporaryPathToFile = videoFile.getSessionIdBasedFilePath();
        Path finalPathToFile = videoFile.getSessionDateTimeBasedFilePath();

        try {
            Files.move(temporaryPathToFile, finalPathToFile);
            logger.info("Processed completed video file: " + finalPathToFile);
        }
        catch (IOException exception) {
            logger.error("Could not move temporary video file to final path.", exception);
        }
    }
}
