package camera.motion.state;

public class MotionState {
    public final int noMotionFrameCount;
    public final boolean motionDetected;
    public final boolean transitioned;

    public MotionState(int noMotionFrameCount, boolean motionDetected, boolean transitioned) {
        if (noMotionFrameCount < 0) {
            throw new IllegalArgumentException("No motion frame count must be greater than or equal to 0.");
        }

        this.noMotionFrameCount = noMotionFrameCount;
        this.motionDetected = motionDetected;
        this.transitioned = transitioned;
    }
}
