package camera.recording.video.session;

import camera.validation.ValidationSupport;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.concurrent.TimeUnit;

public record VideoSessionSettings(
        @Positive int duration,
        @NotNull TimeUnit timeUnit
) {
    public VideoSessionSettings(int duration, TimeUnit timeUnit) {
        this.duration = duration;
        this.timeUnit = timeUnit;

        ValidationSupport.validate(this);
    }
}
