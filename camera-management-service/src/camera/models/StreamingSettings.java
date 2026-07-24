package camera.models;

import camera.validation.ValidationSupport;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

public class StreamingSettings {
    @PositiveOrZero
    public final int streamingServiceCode;
    @NotBlank
    @Size(min = 4, max = 4)
    public final String encoding;

    @JsonCreator
    public StreamingSettings(
            @JsonProperty("streamingServiceCode") int streamingServiceCode,
            @JsonProperty("encoding") String encoding) {
        this.streamingServiceCode = streamingServiceCode;
        this.encoding = encoding;

        ValidationSupport.validate(this);
    }
}
