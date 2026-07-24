package camera.motion.state;

import camera.validation.ValidationSupport;

import java.util.ArrayList;
import java.util.List;

public class MotionStateMachine {
    private MotionState currentState;
    private final List<MotionTransition> transitions;

    public MotionStateMachine() {
        this.currentState = new MotionState(0, false, false);
        this.transitions = new ArrayList<>();
    }

    public synchronized void addTransition(MotionTransition transition) {
        ValidationSupport.validateRequired("Motion transition", transition);

        transitions.add(transition);
    }

    public synchronized MotionState transition(boolean motionDetected) {
        MotionState nextState;

        for (MotionTransition transition : transitions) {
            nextState = transition.transition(motionDetected, currentState);

            if (nextState != null) {
                currentState = nextState;
                return currentState;
            }
        }

        return currentState;
    }
}
