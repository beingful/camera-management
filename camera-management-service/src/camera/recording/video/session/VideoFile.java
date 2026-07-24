package camera.recording.video.session;

import camera.recording.video.file.FilePathBuilder;
import camera.recording.video.file.FileSettings;
import camera.validation.ValidationSupport;

import java.nio.file.Path;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

public class VideoFile {
    private final static DateTimeFormatter dateTimeFormatter;

    static {
        dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss");
    }

    public final FileSettings settings;
    public final VideoSession session;

    public VideoFile(FileSettings settings, VideoSession session) {
        ValidationSupport.validateRequired("File settings", settings);
        ValidationSupport.validateRequired("Video session", session);

        this.settings = settings;
        this.session = session;
    }

    public Path getSessionIdBasedFilePath() {
        return FilePathBuilder.build(
                settings.filePath.getParent(),
                session.id().toString(),
                settings.fileExtension());
    }

    public Path getSessionDateTimeBasedFilePath() {
        if (session.isAlive()) {
            throw new IllegalStateException("Cannot build final video file path for an active session: " + session.id());
        }

        return FilePathBuilder.build(
                settings.filePath.getParent(),
                Arrays.asList(
                        session.startTime().format(dateTimeFormatter),
                        session.endTime().format(dateTimeFormatter)),
                "_",
                settings.fileExtension());
    }
}
