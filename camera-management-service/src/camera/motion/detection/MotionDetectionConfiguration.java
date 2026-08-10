package camera.motion.detection;

import camera.configuration.ConfigurationFileResolver;
import camera.configuration.Configuration;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

public class MotionDetectionConfiguration extends Configuration<MotionDetectionSettings> {
    public MotionDetectionConfiguration() {
        super(
                ConfigurationFileResolver.resolveBuildOutput("motion-detection.yaml"),
                new TypeReference<MotionDetectionSettings>() {},
                new ObjectMapper(new YAMLFactory())
        );
    }
}
