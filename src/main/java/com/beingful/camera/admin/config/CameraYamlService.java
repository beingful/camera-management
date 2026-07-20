package com.beingful.camera.admin.config;

import com.beingful.camera.admin.model.CameraConfig;
import com.beingful.camera.admin.model.CameraEntry;

import java.util.List;

public class CameraYamlService {
    public CameraConfig parse(String yaml) {
        CameraConfig config = new CameraConfig();
        CameraEntry currentCamera = null;
        String section = "";

        for (String line : yaml.split("\\R")) {
            String trimmed = line.trim();

            if (trimmed.isEmpty()) {
                continue;
            }

            if (trimmed.equals("localStorage:")) {
                section = "localStorage";
                continue;
            }

            if (trimmed.equals("cameras:")) {
                section = "cameras";
                continue;
            }

            if (trimmed.equals("identity:") || trimmed.equals("connection:")
                    || trimmed.equals("frame:") || trimmed.equals("streamingSettings:")) {
                section = trimmed.substring(0, trimmed.length() - 1);
                continue;
            }

            if (trimmed.startsWith("- ")) {
                currentCamera = new CameraEntry();
                config.cameras.add(currentCamera);
                String remainder = trimmed.substring(2).trim();
                if (!remainder.isEmpty()) {
                    readCameraValue(currentCamera, section, remainder);
                }
                continue;
            }

            if (section.equals("localStorage")) {
                readStorageValue(config, trimmed);
            }
            else if (currentCamera != null) {
                readCameraValue(currentCamera, section, trimmed);
            }
        }

        return config;
    }

    public String write(CameraConfig config) {
        StringBuilder yaml = new StringBuilder();
        yaml.append("localStorage:\n");
        yaml.append("  path: \"").append(escape(config.storagePath)).append("\"\n");
        yaml.append("  fileExtension: \"").append(escape(config.fileExtension)).append("\"\n");
        yaml.append("cameras:\n");

        for (CameraEntry camera : config.cameras) {
            yaml.append("  - identity:\n");
            yaml.append("      id: ").append(camera.id).append("\n");
            yaml.append("      name: \"").append(escape(camera.name)).append("\"\n");
            yaml.append("    connection:\n");
            yaml.append("      url: \"").append(escape(camera.url)).append("\"\n");
            yaml.append("    frame:\n");
            yaml.append("      rate: ").append(camera.rate).append("\n");
            yaml.append("      width: ").append(camera.width).append("\n");
            yaml.append("      height: ").append(camera.height).append("\n");
            yaml.append("    streamingSettings:\n");
            yaml.append("      streamingServiceCode: ").append(camera.streamingServiceCode).append("\n");
            yaml.append("      encoding: \"").append(escape(camera.encoding)).append("\"\n");
        }

        return yaml.toString();
    }

    public boolean hasDuplicateCameraKeys(List<CameraEntry> cameras, CameraEntry selectedCamera) {
        for (CameraEntry camera : cameras) {
            if (camera == selectedCamera) {
                continue;
            }

            if (camera.id == selectedCamera.id
                    || camera.name.equals(selectedCamera.name)
                    || camera.url.equals(selectedCamera.url)) {
                return true;
            }
        }

        return false;
    }

    private void readStorageValue(CameraConfig config, String line) {
        String key = key(line);
        String value = value(line);

        if (key.equals("path")) {
            config.storagePath = value;
        }
        else if (key.equals("fileExtension")) {
            config.fileExtension = value;
        }
    }

    private void readCameraValue(CameraEntry camera, String section, String line) {
        String key = key(line);
        String value = value(line);

        switch (section) {
            case "identity" -> {
                if (key.equals("id")) {
                    camera.id = parseInt(value);
                }
                else if (key.equals("name")) {
                    camera.name = value;
                }
            }
            case "connection" -> {
                if (key.equals("url")) {
                    camera.url = value;
                }
            }
            case "frame" -> {
                if (key.equals("rate")) {
                    camera.rate = parseInt(value);
                }
                else if (key.equals("width")) {
                    camera.width = parseInt(value);
                }
                else if (key.equals("height")) {
                    camera.height = parseInt(value);
                }
            }
            case "streamingSettings" -> {
                if (key.equals("streamingServiceCode")) {
                    camera.streamingServiceCode = parseInt(value);
                }
                else if (key.equals("encoding")) {
                    camera.encoding = value;
                }
            }
            default -> {
            }
        }
    }

    private String key(String line) {
        int index = line.indexOf(':');
        return index < 0 ? line : line.substring(0, index).trim();
    }

    private String value(String line) {
        int index = line.indexOf(':');
        if (index < 0) {
            return "";
        }

        String value = line.substring(index + 1).trim();
        if (value.startsWith("\"") && value.endsWith("\"") && value.length() >= 2) {
            return value.substring(1, value.length() - 1);
        }

        return value;
    }

    private int parseInt(String value) {
        return Integer.parseInt(value.trim());
    }

    private String escape(String value) {
        return value == null ? "" : value.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
