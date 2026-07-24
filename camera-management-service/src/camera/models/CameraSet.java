package camera.models;

import camera.validation.ValidCameraSet;
import camera.validation.ValidationSupport;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.List;

@ValidCameraSet
public class CameraSet {
    @Valid
    @NotNull
    private final List<@NotNull @Valid Camera> cameras;

    @JsonCreator
    public CameraSet(
            @JsonProperty("cameras") List<@NotNull @Valid Camera> cameras,
            @JsonProperty("localStorage") StorageSettings localStorage
    ) {
        this.cameras = cameras;
        ValidationSupport.validate(this);
        ValidationSupport.validateRequired("Local storage", localStorage);

        this.cameras.forEach(camera -> camera.withLocalStorage(localStorage));

        ValidationSupport.validate(this);
    }

    public CameraSet(List<Camera> cameras) {
        this.cameras = cameras;

        ValidationSupport.validate(this);
    }

    public List<Camera> cameras() {
        return cameras;
    }
}
