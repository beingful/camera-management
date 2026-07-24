package admin.config;

import admin.model.MotionConfig;

public class MotionYamlService {
    public MotionConfig parse(String yaml) {
        MotionConfig config = new MotionConfig();

        for (String line : yaml.split("\\R")) {
            String trimmed = line.trim();
            if (trimmed.isEmpty() || !trimmed.contains(":")) {
                continue;
            }

            String key = trimmed.substring(0, trimmed.indexOf(':')).trim();
            String value = trimmed.substring(trimmed.indexOf(':') + 1).trim();

            switch (key) {
                case "history" -> config.history = Integer.parseInt(value);
                case "varThreshold" -> config.varThreshold = Double.parseDouble(value);
                case "detectShadows" -> config.detectShadows = Boolean.parseBoolean(value);
                case "learningRate" -> config.learningRate = Double.parseDouble(value);
                case "minimumMotionArea" -> config.minimumMotionArea = Double.parseDouble(value);
                case "motionEndFrameCount" -> config.motionEndFrameCount = Integer.parseInt(value);
                default -> {
                }
            }
        }

        return config;
    }

    public String write(MotionConfig config) {
        return "history: " + config.history + "\n"
                + "varThreshold: " + config.varThreshold + "\n"
                + "detectShadows: " + config.detectShadows + "\n"
                + "learningRate: " + config.learningRate + "\n"
                + "minimumMotionArea: " + config.minimumMotionArea + "\n"
                + "motionEndFrameCount: " + config.motionEndFrameCount + "\n";
    }
}
