package camera.models;

import camera.validation.ValidationSupport;
import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.nio.file.Path;

public class StorageSettings {
    @NotNull
    public final Path directoryPath;
    @NotBlank
    public final String fileExtension;

    @JsonCreator
    public StorageSettings(
            @JsonProperty("directoryPath")
            @JsonAlias({"path", "parentDirectory"})
            Path directoryPath,
            @JsonProperty("fileExtension")
            @JsonAlias("extension")
            String fileExtension) {
        this.directoryPath = directoryPath;
        this.fileExtension = fileExtension;

        ValidationSupport.validate(this);
    }
}
