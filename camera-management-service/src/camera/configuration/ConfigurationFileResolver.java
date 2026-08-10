package camera.configuration;

import camera.validation.ValidationSupport;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

public class ConfigurationFileResolver {
    private static final String CONFIGURATION_DIRECTORY_PROPERTY = "camera.configuration.directory";
    private static final String CONFIGURATION_DIRECTORY_ENVIRONMENT_VARIABLE = "CAMERA_CONFIGURATION_DIRECTORY";
    private static final String BUNDLED_CONFIGURATION_DIRECTORY = "camera/configuration/files";
    private static final String BUILD_CONFIGURATION_DIRECTORY = "target/classes/camera/configuration/files";
    private static final String DEFAULT_CONFIGURATION_DIRECTORY = "config";

    public static Path resolve(String fileName) {
        ValidationSupport.validateNotBlank("Configuration file name", fileName);

        try {
            Path configurationDirectory = configurationDirectory();
            Files.createDirectories(configurationDirectory);

            Path configurationFile = configurationDirectory.resolve(fileName).toAbsolutePath().normalize();

            if (!Files.isRegularFile(configurationFile)) {
                copyBundledConfiguration(fileName, configurationFile);
            }

            return configurationFile;
        }
        catch (IOException exception) {
            throw new IllegalStateException("Could not resolve configuration file: " + fileName, exception);
        }
    }

    public static Path resolveBuildOutput(String fileName) {
        ValidationSupport.validateNotBlank("Configuration file name", fileName);

        try {
            Path configurationDirectory = Path.of(BUILD_CONFIGURATION_DIRECTORY).toAbsolutePath().normalize();
            Files.createDirectories(configurationDirectory);

            Path configurationFile = configurationDirectory.resolve(fileName).toAbsolutePath().normalize();

            if (!Files.isRegularFile(configurationFile)) {
                copyBundledConfiguration(fileName, configurationFile);
            }

            return configurationFile;
        }
        catch (IOException exception) {
            throw new IllegalStateException("Could not resolve build output configuration file: " + fileName, exception);
        }
    }

    private static Path configurationDirectory() {
        String configuredDirectory = System.getProperty(CONFIGURATION_DIRECTORY_PROPERTY);

        if (configuredDirectory == null || configuredDirectory.isBlank()) {
            configuredDirectory = System.getenv(CONFIGURATION_DIRECTORY_ENVIRONMENT_VARIABLE);
        }

        if (configuredDirectory == null || configuredDirectory.isBlank()) {
            configuredDirectory = DEFAULT_CONFIGURATION_DIRECTORY;
        }

        return Path.of(configuredDirectory).toAbsolutePath().normalize();
    }

    private static void copyBundledConfiguration(String fileName, Path configurationFile) throws IOException {
        String resourcePath = BUNDLED_CONFIGURATION_DIRECTORY + "/" + fileName;

        try (InputStream inputStream = ConfigurationFileResolver.class
                .getClassLoader()
                .getResourceAsStream(resourcePath)) {
            if (inputStream == null) {
                throw new IOException("Bundled configuration resource does not exist: " + resourcePath);
            }

            Files.copy(inputStream, configurationFile);
        }
    }
}
