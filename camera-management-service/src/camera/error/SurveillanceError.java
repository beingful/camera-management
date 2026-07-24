package camera.error;

import camera.validation.ValidationSupport;

public class SurveillanceError {
    public static final int SYSTEM_CAMERA_ID = -1;

    public final int cameraId;
    public final boolean isCameraError;
    public final Throwable internalError;

    public SurveillanceError(int cameraId, Throwable internalError) {
        ValidationSupport.validateRequired("Internal error", internalError);

        this.cameraId = cameraId;
        this.isCameraError = cameraId != SYSTEM_CAMERA_ID;
        this.internalError = internalError;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }

        if (!(object instanceof SurveillanceError other)) {
            return false;
        }

        return isCameraError && other.isCameraError && cameraId == other.cameraId;
    }

    @Override
    public int hashCode() {
        if (!isCameraError) {
            return System.identityHashCode(this);
        }

        return Integer.hashCode(cameraId);
    }
}
