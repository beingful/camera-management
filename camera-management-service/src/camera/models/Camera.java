package camera.models;

import camera.snapshot.ICapturable;
import camera.validation.ValidationSupport;
import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.nio.file.Path;
import java.util.Objects;

public class Camera implements ICapturable {
    @Valid
    @NotNull
    public final Identity identity;
    @Valid
    @NotNull
    public final Connection connection;
    @Valid
    @NotNull
    public final FrameSettings frameSettings;
    @Valid
    @NotNull
    public final StreamingSettings streamingSettings;
    @Valid
    private StorageSettings storageSettings;

    @JsonCreator
    public Camera(
            @JsonProperty("identity") Identity identity,
            @JsonProperty("connection") Connection connection,
            @JsonProperty("frameSettings")
            @JsonAlias("frame")
            FrameSettings frameSettings,
            @JsonProperty("streamingSettings") StreamingSettings streamingSettings
    ) {
        this.identity = identity;
        this.connection = connection;
        this.frameSettings = frameSettings;
        this.streamingSettings = streamingSettings;

        ValidationSupport.validate(this);
    }

    public StorageSettings storageSettings() {
        return storageSettings;
    }

    public void withLocalStorage(StorageSettings rootStorage) {
        ValidationSupport.validateRequired("Root storage", rootStorage);

        Path cameraStoragePath = rootStorage.directoryPath.resolve(identity.name);

        storageSettings = new StorageSettings(cameraStoragePath, rootStorage.fileExtension);

        ValidationSupport.validate(this);
    }

    @Override
    public int id() {
        return identity.id;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }

        if (!(object instanceof Camera camera)) {
            return false;
        }

        return identity.id == camera.identity.id &&
                frameSettings.rate == camera.frameSettings.rate &&
                Double.compare(frameSettings.size.width, camera.frameSettings.size.width) == 0 &&
                Double.compare(frameSettings.size.height, camera.frameSettings.size.height) == 0 &&
                streamingSettings.streamingServiceCode == camera.streamingSettings.streamingServiceCode &&
                Objects.equals(identity.name, camera.identity.name) &&
                Objects.equals(connection.url, camera.connection.url) &&
                Objects.equals(streamingSettings.encoding, camera.streamingSettings.encoding) &&
                Objects.equals(directoryPath(), camera.directoryPath()) &&
                Objects.equals(fileExtension(), camera.fileExtension());
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                identity.id,
                identity.name,
                connection.url,
                frameSettings.rate,
                frameSettings.size.width,
                frameSettings.size.height,
                streamingSettings.streamingServiceCode,
                streamingSettings.encoding,
                directoryPath(),
                fileExtension());
    }

    private Path directoryPath() {
        return storageSettings == null ? null : storageSettings.directoryPath;
    }

    private String fileExtension() {
        return storageSettings == null ? null : storageSettings.fileExtension;
    }
}
