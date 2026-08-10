package admin.config;

import java.nio.file.Path;

public final class AppPaths {
    public static final Path SERVICE_ROOT = Path.of("/Users/hanna/IdeaProjects/camera-management/camera-management-service");
    public static final Path CAMERAS_FILE = SERVICE_ROOT.resolve("target/classes/camera/configuration/files/cameras.yaml");
    public static final Path MOTION_FILE = SERVICE_ROOT.resolve("target/classes/camera/configuration/files/motion-detection.yaml");

    private AppPaths() {
    }
}
