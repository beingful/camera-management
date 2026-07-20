package com.beingful.camera.admin.config;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileService {
    public String read(Path path) {
        try {
            return Files.readString(path, StandardCharsets.UTF_8);
        }
        catch (IOException exception) {
            throw new IllegalStateException("Could not read file: " + path, exception);
        }
    }

    public void write(Path path, String content) {
        try {
            Files.writeString(path, content, StandardCharsets.UTF_8);
        }
        catch (IOException exception) {
            throw new IllegalStateException("Could not write file: " + path, exception);
        }
    }
}
