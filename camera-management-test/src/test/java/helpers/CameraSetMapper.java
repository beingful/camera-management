package helpers;

import camera.configuration.serialization.SizeDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.opencv.core.Size;

public class CameraSetMapper {
    public static ObjectMapper create() {
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        SimpleModule module = new SimpleModule();
        module.addDeserializer(Size.class, new SizeDeserializer());
        mapper.registerModule(module);
        return mapper;
    }
}
