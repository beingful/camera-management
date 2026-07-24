package camera.motion.state;

public interface MotionTransition {
    MotionState transition(boolean motionDetected, MotionState currentState);
}
