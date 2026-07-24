package camera.motion;

import camera.logging.ILogger;
import camera.motion.detection.CameraMotionDetection;
import camera.motion.detection.IMotionDetectionObserver;
import camera.motion.state.MotionState;
import camera.motion.state.MotionStateMachine;
import camera.validation.ValidationSupport;

public class CameraMotionSignalization implements IMotionDetectionObserver {
    private final MotionStateMachine motionStateMachine;
    private final ILogger logger;
    private ICameraMotionController motionController;

    public CameraMotionSignalization(
            CameraMotionDetection cameraMotionDetection,
            MotionStateMachine motionStateMachine,
            ILogger logger) {
        ValidationSupport.validateRequired("Camera motion detection", cameraMotionDetection);
        ValidationSupport.validateRequired("Motion state machine", motionStateMachine);
        ValidationSupport.validateRequired("Logger", logger);

        this.motionStateMachine = motionStateMachine;
        this.logger = logger;
        cameraMotionDetection.subscribe(this);
    }

    public void setMotionController(ICameraMotionController motionController) {
        ValidationSupport.validateRequired("Motion controller", motionController);

        this.motionController = motionController;
    }

    @Override
    public synchronized void onDetectionResult(boolean motionDetected) {
        MotionState motionState = motionStateMachine.transition(motionDetected);

        if (!motionState.transitioned) {
            logger.info("Motion signalization state unchanged. motionDetected="
                    + motionState.motionDetected
                    + ", noMotionFrameCount="
                    + motionState.noMotionFrameCount);
            return;
        }

        if (motionState.motionDetected) {
            logger.info("Motion signalization detected started motion.");
            if (motionController != null) {
                motionController.onMotionStarted();
            }
        }
        else {
            logger.info("Motion signalization detected stopped motion.");
            if (motionController != null) {
                motionController.onMotionStopped();
            }
        }
    }
}
