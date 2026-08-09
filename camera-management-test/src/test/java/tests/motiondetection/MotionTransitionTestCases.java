package tests.motiondetection;

import java.util.List;

public class MotionTransitionTestCases {
    public static List<MotionTransitionTestCase> noMotionDetected() {
        return List.of(new MotionTransitionTestCase(
                "motion remains stopped when no motion is detected",
                2,
                List.of(false),
                false,
                false,
                0));
    }

    public static List<MotionTransitionTestCase> firstDetectedFrame() {
        return List.of(new MotionTransitionTestCase(
                "motion starts on first detected frame",
                2,
                List.of(true),
                true,
                true,
                0));
    }

    public static List<MotionTransitionTestCase> beforeNoMotionThreshold() {
        return List.of(new MotionTransitionTestCase(
                "motion does not stop before no-motion threshold",
                3,
                List.of(true, false, false),
                true,
                false,
                2));
    }

    public static List<MotionTransitionTestCase> atNoMotionThreshold() {
        return List.of(new MotionTransitionTestCase(
                "motion stops when no-motion threshold is reached",
                2,
                List.of(true, false, false),
                false,
                true,
                0));
    }

    public static List<MotionTransitionTestCase> resumesBeforeThreshold() {
        return List.of(new MotionTransitionTestCase(
                "motion resumes before threshold resets no-motion counter",
                3,
                List.of(true, false, true),
                true,
                false,
                0));
    }
}
