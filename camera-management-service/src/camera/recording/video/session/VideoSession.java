package camera.recording.video.session;

import java.time.LocalDateTime;
import java.util.UUID;

public class VideoSession {
    private final UUID id;
    private final LocalDateTime startTime;
    private LocalDateTime endTime;

    public VideoSession() {
        this(LocalDateTime.now(), LocalDateTime.MIN);
    }

    private VideoSession(LocalDateTime startTime, LocalDateTime endTime) {
        id = UUID.randomUUID();
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public UUID id() {
        return id;
    }

    public LocalDateTime startTime() {
        return startTime;
    }

    public LocalDateTime endTime() {
        return endTime;
    }

    public boolean isAlive() {
        return startTime.isAfter(endTime);
    }

    void end() {
        endTime = LocalDateTime.now();
    }
}
