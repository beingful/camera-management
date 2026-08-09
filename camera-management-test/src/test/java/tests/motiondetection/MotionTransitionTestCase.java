package tests.motiondetection;

import java.util.List;

public record MotionTransitionTestCase(
        String name,
        int motionEndFrameCount,
        List<Boolean> detections,
        boolean expectedMotionDetected,
        boolean expectedTransitioned,
        int expectedNoMotionFrameCount) {
    @Override
    public String toString() {
        return name;
    }
}
