package tests.sessionfile;

import java.util.List;

public class VideoSessionFileTestCases {
    public static List<VideoSessionFileTestCase> mp4TemporaryFileName() {
        return List.of(new VideoSessionFileTestCase(
                "session file uses session id with mp4 extension",
                "camera-recording",
                ".mp4",
                ".mp4"));
    }

    public static List<VideoSessionFileTestCase> extensionWithoutLeadingDot() {
        return List.of(new VideoSessionFileTestCase(
                "session file normalizes extension without leading dot",
                "camera-recording",
                "avi",
                ".avi"));
    }
}

