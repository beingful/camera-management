package camera.motion.detection;

import camera.validation.ValidationSupport;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

public class MotionDetectionSettings {
    @Positive
    public final int history;
    @DecimalMin("0.0")
    public final double varThreshold;
    public final boolean detectShadows;
    public final double learningRate;
    @PositiveOrZero
    public final double minimumMotionArea;
    @Min(1)
    public final int motionEndFrameCount;

    @JsonCreator
    public MotionDetectionSettings(
            @JsonProperty("history") Integer history,
            @JsonProperty("varThreshold") Double varThreshold,
            @JsonProperty("detectShadows") Boolean detectShadows,
            @JsonProperty("learningRate") Double learningRate,
            @JsonProperty("minimumMotionArea") Double minimumMotionArea,
            @JsonProperty("motionEndFrameCount") Integer motionEndFrameCount) {
        this.history = history == null ? 500 : history;
        this.varThreshold = varThreshold == null ? 16.0 : varThreshold;
        this.detectShadows = detectShadows == null || detectShadows;
        this.learningRate = learningRate == null ? -1.0 : learningRate;
        this.minimumMotionArea = minimumMotionArea == null ? 1_500.0 : minimumMotionArea;
        this.motionEndFrameCount = motionEndFrameCount == null ? 15 : motionEndFrameCount;

        ValidationSupport.validate(this);

        if (this.learningRate < 0.0 && this.learningRate != -1.0) {
            throw new IllegalArgumentException("Learning rate must be -1.0 or greater than or equal to 0.0.");
        }
    }
}
