package camera.motion.state.transitions;

import camera.motion.state.MotionState;
import camera.motion.state.MotionTransition;
import camera.validation.ValidationSupport;

public class NoMotionTransition implements MotionTransition {
    private final int motionEndFrameCount;

    public NoMotionTransition(int motionEndFrameCount) {
        if (motionEndFrameCount < 1) {
            throw new IllegalArgumentException("Motion end frame count must be greater than 0.");
        }

        this.motionEndFrameCount = motionEndFrameCount;
    }

    @Override
    public MotionState transition(boolean motionDetected, MotionState currentState) {
        ValidationSupport.validateRequired("Current motion state", currentState);

        if (motionDetected) {
            return null;
        }

        if (!currentState.motionDetected) {
            return new MotionState(0, false, false);
        }

        int noMotionFrameCount = currentState.noMotionFrameCount + 1;

        if (noMotionFrameCount >= motionEndFrameCount) {
            return new MotionState(0, false, true);
        }

        return new MotionState(noMotionFrameCount, true, false);
    }
}
