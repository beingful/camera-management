package camera.models;

import camera.validation.ValidationSupport;
import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

public class Identity{
    @Positive
    public final int id;
    @NotBlank
    public final String name;

    @JsonCreator
    public Identity(
            @JsonProperty("id") int id,
            @JsonProperty("name")
            @JsonAlias("fileName")
            String name) {
        this.id = id;
        this.name = name;

        ValidationSupport.validate(this);
    }
}
