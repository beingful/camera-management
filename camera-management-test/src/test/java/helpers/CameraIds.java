package helpers;

import camera.models.Camera;
import camera.models.CameraSet;

import java.util.List;

public class CameraIds {
    public static List<Integer> from(CameraSet cameraSet) {
        return from(cameraSet.cameras());
    }

    public static List<Integer> from(List<Camera> cameras) {
        return cameras.stream().map(camera -> camera.identity.id).toList();
    }
}
