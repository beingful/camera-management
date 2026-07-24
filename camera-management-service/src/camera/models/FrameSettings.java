package camera.models;

import camera.validation.ValidFrameSize;
import camera.validation.ValidationSupport;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.opencv.core.Size;

public class FrameSettings {
    @NotNull
    @ValidFrameSize
    public final Size size;
    @Positive
    public final int rate;
    public final int frameIntervalMilliseconds;

    @JsonCreator
    public FrameSettings(
            @JsonProperty("size") Size size,
            @JsonProperty("width") Integer width,
            @JsonProperty("height") Integer height,
            @JsonProperty("rate") int rate) {
        ValidationSupport.validateValue(FrameSettings.class, "rate", rate);

        if (size == null && width != null && height != null) {
            size = new Size(width, height);
        }

        this.size = size;
        this.rate = rate;
        this.frameIntervalMilliseconds = 1_000 / rate;

        ValidationSupport.validate(this);
    }
}
