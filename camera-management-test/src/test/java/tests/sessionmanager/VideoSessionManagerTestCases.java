package tests.sessionmanager;

import camera.recording.video.session.VideoSessionSettings;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class VideoSessionManagerTestCases {
    public static List<VideoSessionManagerTestCase> activeSession() {
        return List.of(new VideoSessionManagerTestCase(
                "session manager handles active hour-long session",
                new VideoSessionSettings(1, TimeUnit.HOURS),
                Duration.ofSeconds(1)));
    }

    public static List<VideoSessionManagerTestCase> shortSession() {
        return List.of(new VideoSessionManagerTestCase(
                "session manager completes short millisecond session",
                new VideoSessionSettings(50, TimeUnit.MILLISECONDS),
                Duration.ofSeconds(2)));
    }
}

