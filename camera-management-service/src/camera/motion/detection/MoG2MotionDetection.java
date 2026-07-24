package camera.motion.detection;

import camera.validation.ValidationSupport;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.imgproc.Imgproc;
import org.opencv.video.BackgroundSubtractorMOG2;
import org.opencv.video.Video;

import java.util.ArrayList;
import java.util.List;

public class MoG2MotionDetection {
    private final MotionDetectionSettings settings;
    private final BackgroundSubtractorMOG2 backgroundSubtractor;
    private final Mat foregroundMask;

    public MoG2MotionDetection(MotionDetectionSettings settings) {
        ValidationSupport.validateRequired("Motion detection settings", settings);

        this.settings = settings;
        this.backgroundSubtractor = Video.createBackgroundSubtractorMOG2(
                settings.history,
                settings.varThreshold,
                settings.detectShadows);
        this.foregroundMask = new Mat();
    }

    public boolean detect(Mat frame) {
        if (frame == null || frame.empty()) {
            return false;
        }

        backgroundSubtractor.apply(frame, foregroundMask, settings.learningRate);

        return hasMotion(foregroundMask);
    }

    public void release() {
        foregroundMask.release();
        backgroundSubtractor.clear();
    }

    private boolean hasMotion(Mat foregroundMask) {
        Mat thresholdMask = new Mat();
        Mat hierarchy = new Mat();
        List<MatOfPoint> contours = new ArrayList<>();

        try {
            Imgproc.threshold(foregroundMask, thresholdMask, 244, 255, Imgproc.THRESH_BINARY);
            Imgproc.findContours(thresholdMask, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);

            double motionArea = contours.stream()
                    .mapToDouble(Imgproc::contourArea)
                    .sum();

            return motionArea >= settings.minimumMotionArea;
        }
        finally {
            thresholdMask.release();
            hierarchy.release();
            contours.forEach(Mat::release);
        }
    }
}
