package tests.sessionmanager;

import camera.recording.video.session.VideoSession;
import camera.recording.video.session.VideoSessionManager;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class VideoSessionManagerTests {
    @ParameterizedTest(name = "{0}")
    @MethodSource("tests.sessionmanager.VideoSessionManagerTestCases#activeSession")
    void testRefreshSessionCreatesActiveSession(VideoSessionManagerTestCase testCase) {
        VideoSessionManager manager = new VideoSessionManager(testCase.settings());

        VideoSession session = manager.refreshSession();

        assertTrue(session.isAlive(), testCase.name() + ": session should be alive");
        manager.terminateSession(session);
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("tests.sessionmanager.VideoSessionManagerTestCases#activeSession")
    void testTerminateSessionCompletesActiveSession(VideoSessionManagerTestCase testCase) {
        VideoSessionManager manager = new VideoSessionManager(testCase.settings());
        VideoSession session = manager.refreshSession();

        manager.terminateSession(session);

        assertFalse(session.isAlive(), testCase.name() + ": session should be completed");
        assertTrue(!session.endTime().isBefore(session.startTime()), testCase.name() + ": end time should follow start time");
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("tests.sessionmanager.VideoSessionManagerTestCases#shortSession")
    void testSessionCompletesAfterConfiguredDuration(VideoSessionManagerTestCase testCase) throws InterruptedException {
        VideoSessionManager manager = new VideoSessionManager(testCase.settings());
        VideoSession session = manager.refreshSession();

        waitUntilCompleted(session, testCase.timeout());

        assertFalse(session.isAlive(), testCase.name() + ": session should complete automatically");
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("tests.sessionmanager.VideoSessionManagerTestCases#activeSession")
    void testTerminatingOneSessionDoesNotCompleteAnother(VideoSessionManagerTestCase testCase) {
        VideoSessionManager manager = new VideoSessionManager(testCase.settings());
        VideoSession first = manager.refreshSession();
        VideoSession second = manager.refreshSession();

        manager.terminateSession(first);

        assertFalse(first.isAlive(), testCase.name() + ": terminated session should be completed");
        assertTrue(second.isAlive(), testCase.name() + ": separate session should remain active");
        manager.terminateSession(second);
    }

    private static void waitUntilCompleted(VideoSession session, Duration timeout) throws InterruptedException {
        LocalDateTime deadline = LocalDateTime.now().plus(timeout);
        while (session.isAlive() && LocalDateTime.now().isBefore(deadline)) {
            Thread.sleep(10);
        }
    }
}

