package camera.configuration.serialization;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import org.opencv.core.Size;

import java.io.IOException;

public class SizeDeserializer extends JsonDeserializer<Size> {
    @Override
    public Size deserialize(JsonParser jp, DeserializationContext context) throws IOException {
        JsonNode node = jp.getCodec().readTree(jp);
        JsonNode widthNode = node.get("width");
        JsonNode heightNode = node.get("height");

        if (widthNode == null || heightNode == null) {
            throw JsonMappingException.from(jp, "Size must contain width and height.");
        }

        int width = widthNode.asInt();
        int height = heightNode.asInt();

        return new Size(width, height);
    }
}
