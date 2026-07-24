package camera.recording;

import camera.motion.detection.CameraMotionDetection;
import camera.logging.ILogger;
import camera.motion.CameraMotionSignalization;
import camera.motion.ICameraMotionController;
import camera.validation.ValidationSupport;

public class CameraMotionRecordController implements ICameraRecordController, ICameraMotionController {
    private final CameraRecordService cameraRecordService;
    private final CameraMotionDetection cameraMotionDetection;
    private final CameraMotionSignalization cameraMotionSignalization;
    private final ILogger logger;
    private boolean isRecording;
    private boolean running;

    public CameraMotionRecordController(
            CameraRecordService cameraRecordService,
            CameraMotionDetection cameraMotionDetection,
            CameraMotionSignalization cameraMotionSignalization,
            ILogger logger) {
        ValidationSupport.validateRequired("Camera record service", cameraRecordService);
        ValidationSupport.validateRequired("Camera motion detection", cameraMotionDetection);
        ValidationSupport.validateRequired("Camera motion signalization", cameraMotionSignalization);
        ValidationSupport.validateRequired("Logger", logger);

        this.cameraRecordService = cameraRecordService;
        this.cameraMotionDetection = cameraMotionDetection;
        this.cameraMotionSignalization = cameraMotionSignalization;
        this.logger = logger;
        this.cameraMotionSignalization.setMotionController(this);
        this.isRecording = false;
        this.running = false;
    }

    @Override
    public synchronized void start() {
        if (running) {
            return;
        }

        running = true;
        logger.info("Starting camera motion record controller.");
        cameraMotionDetection.start();
    }

    @Override
    public void stop() {
        synchronized (this) {
            if (!running) {
                stopRecording("Stopping active camera recording.");
                return;
            }

            running = false;
        }

        try {
            cameraMotionDetection.stop();
        }
        finally {
            synchronized (this) {
                stopRecording("Stopping active camera recording.");
            }
        }
    }

    @Override
    public synchronized void onMotionStarted() {
        if (!running) {
            return;
        }

        if (!isRecording) {
            logger.info("Motion started; starting recording.");
            cameraRecordService.start();
            isRecording = true;
        }
    }

    @Override
    public synchronized void onMotionStopped() {
        if (!running) {
            return;
        }

        stopRecording("Motion stopped; stopping recording.");
    }

    private void stopRecording(String message) {
        if (isRecording) {
            logger.info(message);
            cameraRecordService.stop();
            isRecording = false;
        }
    }
}
