package camera.recording.video;

import camera.validation.ValidationSupport;
import org.opencv.videoio.VideoWriter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class VideoWriterFactory {
    public VideoWriter create(VideoSettings settings) {
        ValidationSupport.validateRequired("Video settings", settings);

        String encoding = settings.streamingSettings().encoding;

        int fourcc = VideoWriter.fourcc(
                encoding.charAt(0), encoding.charAt(1),
                encoding.charAt(2), encoding.charAt(3));

        Path videoFile = settings.fileSettings().filePath;
        createParentDirectory(videoFile);

        String videoFilePath = videoFile.toString();

        VideoWriter videoWriter = new VideoWriter(
                videoFilePath,
                settings.streamingSettings().streamingServiceCode,
                fourcc,
                settings.frameSettings().rate,
                settings.frameSettings().size
        );

        if (!videoWriter.isOpened()) {
            throw new IllegalStateException("Could not open video writer: " + videoFilePath);
        }

        return videoWriter;
    }

    private void createParentDirectory(Path videoFile) {
        Path parentDirectory = videoFile.getParent();

        if (parentDirectory == null) {
            return;
        }

        try {
            Files.createDirectories(parentDirectory);
        }
        catch (IOException exception) {
            throw new IllegalStateException("Could not create video file directory: " + parentDirectory, exception);
        }
    }
}
