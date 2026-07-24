package camera.recording.video.session;

import camera.logging.LoggerFactory;
import camera.logging.ILogger;
import camera.validation.ValidationSupport;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;

public class VideoSessionManager {
    private final VideoSessionSettings sessionSettings;
    private final Map<UUID, ScheduledFuture<?>> sessionTerminationTasks;
    private final ILogger logger;

    private ScheduledExecutorService sessionTerminationService;

    public VideoSessionManager(VideoSessionSettings sessionSettings) {
        ValidationSupport.validateRequired("Session settings", sessionSettings);

        this.sessionSettings = sessionSettings;
        this.sessionTerminationTasks = new ConcurrentHashMap<>();
        this.logger = LoggerFactory.systemLogger(VideoSessionManager.class);
    }

    public VideoSession refreshSession() {
        if (sessionTerminationService == null) {
            createSessionTerminationService();
        }

        VideoSession videoSession = new VideoSession();

        ScheduledFuture<?> sessionTerminationTask = sessionTerminationService.schedule(
                () -> endSession(videoSession),
                sessionSettings.duration(),
                sessionSettings.timeUnit());

        sessionTerminationTasks.put(videoSession.id(), sessionTerminationTask);

        return videoSession;
    }

    public void terminateSession(VideoSession videoSession) {
        ValidationSupport.validateRequired("Video session", videoSession);

        if (sessionTerminationTasks.containsKey(videoSession.id())) {
            sessionTerminationTasks
                    .get(videoSession.id())
                    .cancel(false);

            endSession(videoSession);
        }
    }

    private void endSession(VideoSession videoSession) {
        videoSession.end();

        sessionTerminationTasks.remove(videoSession.id());

        if (sessionTerminationTasks.isEmpty()) {
            logger.warning("Interrupting video session termination service.");
            sessionTerminationService.shutdownNow();
            sessionTerminationService = null;
        }
    }

    private void createSessionTerminationService() {
        sessionTerminationService = Executors.newSingleThreadScheduledExecutor(
                Thread.ofPlatform().daemon()
                        .name("session-terminator-") // Optional: names threads sequentially
                        .factory());
    }
}
