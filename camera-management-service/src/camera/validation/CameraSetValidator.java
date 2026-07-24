package camera.validation;

import camera.models.Camera;
import camera.models.CameraSet;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.HashSet;
import java.util.Set;

public class CameraSetValidator implements ConstraintValidator<ValidCameraSet, CameraSet> {
    @Override
    public boolean isValid(CameraSet cameraSet, ConstraintValidatorContext context) {
        return cameraSet == null ||
                cameraSet.cameras() == null ||
                cameraSet.cameras().size() <= 1 ||
                isValid(cameraSet.cameras());
    }

    private boolean isValid(Iterable<Camera> cameras) {
        if (cameras == null) {
            return true;
        }

        Set<Integer> cameraIds = new HashSet<>();
        Set<String> cameraNames = new HashSet<>();
        Set<String> connectionUrls = new HashSet<>();

        for (Camera camera : cameras) {
            if (camera == null || camera.identity == null || camera.connection == null) {
                continue;
            }

            if (!cameraIds.add(camera.identity.id)) {
                return false;
            }

            if (!cameraNames.add(camera.identity.name)) {
                return false;
            }

            if (!connectionUrls.add(camera.connection.url)) {
                return false;
            }
        }

        return true;
    }
}
