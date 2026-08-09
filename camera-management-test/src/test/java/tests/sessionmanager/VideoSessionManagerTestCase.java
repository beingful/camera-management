package tests.sessionmanager;

import camera.recording.video.session.VideoSessionSettings;

import java.time.Duration;

public record VideoSessionManagerTestCase(
        String name,
        VideoSessionSettings settings,
        Duration timeout) {
    @Override
    public String toString() {
        return name;
    }
}

