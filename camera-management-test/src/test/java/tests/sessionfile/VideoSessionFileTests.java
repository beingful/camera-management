package tests.sessionfile;

import camera.recording.video.file.FileSettings;
import camera.recording.video.session.VideoFile;
import camera.recording.video.session.VideoSession;
import camera.recording.video.session.VideoSessionManager;
import camera.recording.video.session.VideoSessionSettings;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.nio.file.Path;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class VideoSessionFileTests {
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss");

    @ParameterizedTest(name = "{0}")
    @MethodSource({
            "tests.sessionfile.VideoSessionFileTestCases#mp4TemporaryFileName",
            "tests.sessionfile.VideoSessionFileTestCases#extensionWithoutLeadingDot"
    })
    void testTemporarySessionFileName(VideoSessionFileTestCase testCase, @TempDir Path directory) {
        VideoSession session = new VideoSession();
        FileSettings fileSettings = new FileSettings(directory, testCase.originalFileName(), testCase.configuredExtension());
        VideoFile videoFile = new VideoFile(fileSettings, session);

        Path expected = directory.resolve(session.id() + testCase.expectedExtension());

        assertEquals(expected, videoFile.getSessionIdBasedFilePath());
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource({
            "tests.sessionfile.VideoSessionFileTestCases#mp4TemporaryFileName",
            "tests.sessionfile.VideoSessionFileTestCases#extensionWithoutLeadingDot"
    })
    void testTimeBasedSessionFileNameRequiresCompletedSession(VideoSessionFileTestCase testCase, @TempDir Path directory) {
        VideoSession session = new VideoSession();
        FileSettings fileSettings = new FileSettings(directory, testCase.originalFileName(), testCase.configuredExtension());
        VideoFile videoFile = new VideoFile(fileSettings, session);

        assertThrows(IllegalStateException.class, videoFile::getSessionDateTimeBasedFilePath);
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource({
            "tests.sessionfile.VideoSessionFileTestCases#mp4TemporaryFileName",
            "tests.sessionfile.VideoSessionFileTestCases#extensionWithoutLeadingDot"
    })
    void testCompletedSessionTimeBasedFileName(VideoSessionFileTestCase testCase, @TempDir Path directory) {
        VideoSessionManager manager = new VideoSessionManager(new VideoSessionSettings(1, TimeUnit.HOURS));
        VideoSession session = manager.refreshSession();
        manager.terminateSession(session);

        FileSettings fileSettings = new FileSettings(directory, testCase.originalFileName(), testCase.configuredExtension());
        VideoFile videoFile = new VideoFile(fileSettings, session);
        String fileName = session.startTime().format(DATE_TIME_FORMATTER)
                + "_"
                + session.endTime().format(DATE_TIME_FORMATTER)
                + testCase.expectedExtension();

        assertEquals(directory.resolve(fileName), videoFile.getSessionDateTimeBasedFilePath());
    }
}
