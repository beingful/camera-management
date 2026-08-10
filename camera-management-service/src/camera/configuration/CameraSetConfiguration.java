package camera.configuration;

import camera.configuration.serialization.SizeDeserializer;
import camera.models.CameraSet;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.opencv.core.Size;

public class CameraSetConfiguration extends Configuration<CameraSet> {
    public CameraSetConfiguration() {
        super(
                ConfigurationFileResolver.resolveBuildOutput("cameras.yaml"),
                new TypeReference<CameraSet>() {},
                createObjectMapper()
        );
    }

    private static ObjectMapper createObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory());
        SimpleModule module = new SimpleModule();
        module.addDeserializer(Size.class, new SizeDeserializer());

        objectMapper.registerModule(module);
        return objectMapper;
    }
}
