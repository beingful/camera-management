package camera.configuration;

import camera.validation.ValidationSupport;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Configuration<T> {
    public final Path filePath;
    private final TypeReference<T> type;
    private final ObjectMapper objectMapper;
    private T configuration;

    public Configuration(Path filePath, TypeReference<T> type, ObjectMapper objectMapper) {
        ValidationSupport.validateRequired("Configuration file path", filePath);
        ValidationSupport.validateRequired("Configuration type", type);
        ValidationSupport.validateRequired("Object mapper", objectMapper);

        this.filePath = filePath;
        this.type = type;
        this.objectMapper = objectMapper;
    }

    public T getConfiguration() throws IOException {
        if (configuration == null) {
            return reload();
        }

        return configuration;
    }

    public T reload() throws IOException {
        if (!Files.isRegularFile(filePath)) {
            throw new IOException("Configuration file does not exist: " + filePath);
        }

        configuration = objectMapper.readValue(filePath.toFile(), type);

        return configuration;
    }
}
