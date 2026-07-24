package camera.recording.video.file;

import camera.validation.ValidationSupport;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.nio.file.Path;

public class FileSettings {
    @NotNull
    public final Path filePath;

    public FileSettings(@NotNull Path parentDirectory, @NotBlank String fileName, @NotBlank String fileExtension) {
        ValidationSupport.validateRequired("Parent directory", parentDirectory);
        ValidationSupport.validateNotBlank("File name", fileName);
        ValidationSupport.validateNotBlank("File extension", fileExtension);

        this.filePath = FilePathBuilder.build(parentDirectory, fileName, fileExtension);

        ValidationSupport.validate(this);
    }

    public FileSettings(@NotNull Path filePath) {
        this.filePath = filePath;

        ValidationSupport.validate(this);
    }

    public String fileExtension() {
        String extension = "";

        String fileName = filePath.getFileName().toString();

        int extensionStartPosition = fileName.lastIndexOf('.');

        if (extensionStartPosition >= 0) {
            extension = fileName.substring(extensionStartPosition);
        }

        return extension;
    }
}
