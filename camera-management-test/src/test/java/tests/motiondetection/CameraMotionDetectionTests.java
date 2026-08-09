package tests.motiondetection;

import camera.motion.state.MotionState;
import camera.motion.state.MotionStateMachine;
import camera.motion.state.transitions.InMotionTransition;
import camera.motion.state.transitions.NoMotionTransition;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class CameraMotionDetectionTests {
    @ParameterizedTest(name = "{0}")
    @MethodSource({
            "tests.motiondetection.MotionTransitionTestCases#noMotionDetected",
            "tests.motiondetection.MotionTransitionTestCases#firstDetectedFrame",
            "tests.motiondetection.MotionTransitionTestCases#beforeNoMotionThreshold",
            "tests.motiondetection.MotionTransitionTestCases#atNoMotionThreshold",
            "tests.motiondetection.MotionTransitionTestCases#resumesBeforeThreshold"
    })
    void testMotionDetectedAndUndetectedTransitions(MotionTransitionTestCase testCase) {
        MotionStateMachine machine = new MotionStateMachine();
        machine.addTransition(new InMotionTransition());
        machine.addTransition(new NoMotionTransition(testCase.motionEndFrameCount()));

        MotionState state = null;
        for (boolean detection : testCase.detections()) {
            state = machine.transition(detection);
        }

        assertNotNull(state, testCase.name() + ": should execute at least one transition");
        assertEquals(testCase.expectedMotionDetected(), state.motionDetected,
                testCase.name() + ": final motion state");
        assertEquals(testCase.expectedTransitioned(), state.transitioned,
                testCase.name() + ": final transitioned flag");
        assertEquals(testCase.expectedNoMotionFrameCount(), state.noMotionFrameCount,
                testCase.name() + ": final no-motion frame count");
    }
}
