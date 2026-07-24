package camera.recording.video.file;

import camera.validation.ValidationSupport;

import java.nio.file.Path;
import java.util.List;

public class FilePathBuilder {
    public static Path build(Path directoryPath, List<String> fileNameParameters, String separator, String extension) {
        ValidationSupport.validateRequired("Directory path", directoryPath);
        ValidationSupport.validateRequired("File name parameters", fileNameParameters);
        ValidationSupport.validateNotBlank("Separator", separator);
        ValidationSupport.validateNotBlank("Extension", extension);

        return build(directoryPath, String.join(separator, fileNameParameters), extension);
    }

    public static Path build(Path directoryPath, String fileName, String extension) {
        ValidationSupport.validateRequired("Directory path", directoryPath);
        ValidationSupport.validateNotBlank("File name", fileName);
        ValidationSupport.validateNotBlank("Extension", extension);

        if (!extension.startsWith(".")) {
            extension = "." + extension;
        }

        return directoryPath.resolve(fileName + extension);
    }
}
