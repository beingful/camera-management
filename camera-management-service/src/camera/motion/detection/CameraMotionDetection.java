package camera.motion.detection;

import camera.logging.ILogger;
import camera.messaging.IPullMessageQueue;
import camera.messaging.QueueConsumer;
import camera.messaging.SharedMessage;
import camera.retry.Retry;
import camera.threading.InLoopTaskExecutor;
import camera.threading.ThreadServiceType;
import camera.threading.IThreadPool;
import camera.validation.ValidationSupport;
import org.opencv.core.Mat;

import java.util.ArrayList;
import java.util.List;

public class CameraMotionDetection extends InLoopTaskExecutor {
    private final IPullMessageQueue<SharedMessage<Mat>> frameQueue;
    private final MoG2MotionDetection motionDetection;
    private final List<IMotionDetectionObserver> observers;
    private final ILogger logger;

    public CameraMotionDetection(
            IPullMessageQueue<SharedMessage<Mat>> frameQueue,
            MoG2MotionDetection motionDetection,
            IThreadPool threadPool,
            ILogger logger,
            Retry retry) {
        super(ThreadServiceType.MotionDetection, threadPool, logger, retry);

        ValidationSupport.validateRequired("Motion detection frame queue", frameQueue);
        ValidationSupport.validateRequired("MOG2 motion detection", motionDetection);
        ValidationSupport.validateRequired("Logger", logger);

        this.frameQueue = frameQueue;
        this.motionDetection = motionDetection;
        this.logger = logger;
        this.observers = new ArrayList<>();

        this.frameQueue.subscribe(new QueueConsumer<>());
    }

    public synchronized void subscribe(IMotionDetectionObserver observer) {
        ValidationSupport.validateRequired("Motion detection observer", observer);

        observers.add(observer);
    }

    public synchronized void start() {
        logger.info("Starting motion detection.");
        startTask();
    }

    public void stop() {
        stopTask();
    }

    @Override
    protected void execute() {
        SharedMessage<Mat> frame = frameQueue.dequeue();

        if (frame == null) {
            return;
        }

        boolean motionDetected;

        try {
            motionDetected = motionDetection.detect(frame.message());
        }
        finally {
            frame.close();
        }

        if (!Thread.currentThread().isInterrupted()) {
            notifyDetectionResult(motionDetected);
        }
    }

    @Override
    protected void cleanup() {
        motionDetection.release();
    }

    private void notifyDetectionResult(boolean motionDetected) {
        List<IMotionDetectionObserver> currentObservers;

        synchronized (this) {
            currentObservers = List.copyOf(observers);
        }

        for (IMotionDetectionObserver observer : currentObservers) {
            observer.onDetectionResult(motionDetected);
        }
    }
}
