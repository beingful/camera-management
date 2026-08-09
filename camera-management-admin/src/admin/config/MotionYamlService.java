package admin.config;

import admin.model.MotionConfig;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator;

import java.io.IOException;

public class MotionYamlService {
    private final ObjectMapper objectMapper;

    public MotionYamlService() {
        objectMapper = new ObjectMapper(YAMLFactory.builder()
                .disable(YAMLGenerator.Feature.WRITE_DOC_START_MARKER)
                .build());
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
    }

    public MotionConfig parse(String yaml) {
        try {
            return objectMapper.readValue(yaml, MotionConfig.class);
        }
        catch (IOException exception) {
            throw new IllegalArgumentException("Could not parse motion YAML.", exception);
        }
    }

    public String write(MotionConfig config) {
        try {
            return objectMapper.writeValueAsString(config);
        }
        catch (IOException exception) {
            throw new IllegalArgumentException("Could not write motion YAML.", exception);
        }
    }
}
