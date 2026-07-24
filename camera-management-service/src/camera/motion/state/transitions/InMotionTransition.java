package camera.motion.state.transitions;

import camera.motion.state.MotionState;
import camera.motion.state.MotionTransition;
import camera.validation.ValidationSupport;

public class InMotionTransition implements MotionTransition {
    @Override
    public MotionState transition(boolean motionDetected, MotionState currentState) {
        ValidationSupport.validateRequired("Current motion state", currentState);

        if (!motionDetected) {
            return null;
        }

        if (!currentState.motionDetected) {
            return new MotionState(0, true, true);
        }

        return new MotionState(0, true, false);
    }
}
