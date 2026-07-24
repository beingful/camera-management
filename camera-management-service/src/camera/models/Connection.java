package camera.models;

import camera.validation.ValidationSupport;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class Connection {
    @NotBlank
    @Pattern(regexp = "^rtsp://.+", message = "must use the rtsp protocol")
    public final String url;

    @JsonCreator
    public Connection(@JsonProperty("url") String url) {
        this.url = url;

        ValidationSupport.validate(this);
    }
}
