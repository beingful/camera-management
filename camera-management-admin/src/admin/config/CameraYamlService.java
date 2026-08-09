package admin.config;

import admin.model.CameraConfig;
import admin.model.CameraEntry;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CameraYamlService {
    private final ObjectMapper objectMapper;

    public CameraYamlService() {
        objectMapper = new ObjectMapper(YAMLFactory.builder()
                .disable(YAMLGenerator.Feature.WRITE_DOC_START_MARKER)
                .build());
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
    }

    public CameraConfig parse(String yaml) {
        try {
            return fromDocument(objectMapper.readValue(yaml, CameraDocument.class));
        }
        catch (IOException exception) {
            throw new IllegalArgumentException("Could not parse camera YAML.", exception);
        }
    }

    public String write(CameraConfig config) {
        try {
            return objectMapper.writeValueAsString(toDocument(config));
        }
        catch (IOException exception) {
            throw new IllegalArgumentException("Could not write camera YAML.", exception);
        }
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

    private CameraConfig fromDocument(CameraDocument document) {
        CameraConfig config = new CameraConfig();
        config.storagePath = value(document.localStorage.path);
        config.fileExtension = value(document.localStorage.fileExtension);

        for (CameraDocument.CameraNode node : document.cameras) {
            CameraEntry camera = new CameraEntry();
            camera.id = node.identity.id;
            camera.name = value(node.identity.name);
            camera.url = value(node.connection.url);
            camera.rate = node.frame.rate;
            camera.width = node.frame.width;
            camera.height = node.frame.height;
            camera.streamingServiceCode = node.streamingSettings.streamingServiceCode;
            camera.encoding = value(node.streamingSettings.encoding);
            config.cameras.add(camera);
        }

        return config;
    }

    private CameraDocument toDocument(CameraConfig config) {
        CameraDocument document = new CameraDocument();
        document.localStorage.path = value(config.storagePath);
        document.localStorage.fileExtension = value(config.fileExtension);

        for (CameraEntry camera : config.cameras) {
            CameraDocument.CameraNode node = new CameraDocument.CameraNode();
            node.identity.id = camera.id;
            node.identity.name = value(camera.name);
            node.connection.url = value(camera.url);
            node.frame.rate = camera.rate;
            node.frame.width = camera.width;
            node.frame.height = camera.height;
            node.streamingSettings.streamingServiceCode = camera.streamingServiceCode;
            node.streamingSettings.encoding = value(camera.encoding);
            document.cameras.add(node);
        }

        return document;
    }

    private String value(String value) {
        return value == null ? "" : value;
    }

    public static class CameraDocument {
        public Storage localStorage = new Storage();
        public List<CameraNode> cameras = new ArrayList<>();

        public static class Storage {
            public String path = "";
            public String fileExtension = "";
        }

        public static class CameraNode {
            public Identity identity = new Identity();
            public Connection connection = new Connection();
            public Frame frame = new Frame();
            public StreamingSettings streamingSettings = new StreamingSettings();
        }

        public static class Identity {
            public int id;
            public String name = "";
        }

        public static class Connection {
            public String url = "";
        }

        public static class Frame {
            public int rate;
            public int width;
            public int height;
        }

        public static class StreamingSettings {
            public int streamingServiceCode;
            public String encoding = "";
        }
    }
}
