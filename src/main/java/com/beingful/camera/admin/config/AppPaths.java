package com.beingful.camera.admin.config;

import java.nio.file.Path;

public final class AppPaths {
    public static final Path PLUGIN_ROOT = Path.of("/Users/hanna/IdeaProjects/camera-management-plugin");
    public static final Path CAMERAS_FILE = PLUGIN_ROOT.resolve("src/camera/configuration/files/cameras.yaml");
    public static final Path MOTION_FILE = PLUGIN_ROOT.resolve("src/camera/configuration/files/motion-detection.yaml");

    private AppPaths() {
    }
}
