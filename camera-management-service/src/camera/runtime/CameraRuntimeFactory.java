package camera.runtime;

import camera.connection.CameraConnectivityFactory;
import camera.connection.ICameraConnectivityController;
import camera.logging.ILogger;
import camera.logging.LoggerFactory;
import camera.messaging.IPullMessageQueue;
import camera.messaging.MessageBus;
import camera.messaging.MessageQueueBuilder;
import camera.messaging.SharedMessage;
import camera.messaging.SharedMessageFanout;
import camera.models.Camera;
import camera.motion.detection.CameraMotionDetection;
import camera.motion.CameraMotionSignalization;
import camera.motion.detection.MoG2MotionDetection;
import camera.motion.detection.MotionDetectionSettings;
import camera.motion.state.transitions.InMotionTransition;
import camera.motion.state.MotionStateMachine;
import camera.motion.state.transitions.NoMotionTransition;
import camera.recording.CameraMotionRecordController;
import camera.recording.CameraRecordService;
import camera.recording.CameraRecorderFactory;
import camera.retry.Retry;
import camera.retry.RetryPolicy;
import camera.retry.RetryWaitStrategy;
import camera.threading.IThreadPool;
import camera.threading.ThreadPoolManager;
import camera.validation.ValidationSupport;
import org.opencv.core.Mat;

import java.util.List;

public class CameraRuntimeFactory {
    private final CameraConnectivityFactory cameraConnectivityFactory;
    private final CameraRecorderFactory cameraRecorderFactory;
    private final MotionDetectionSettings motionDetectionSettings;
    private final ThreadPoolManager threadPoolManager;
    private final MessageQueueBuilder messageQueueBuilder;

    public CameraRuntimeFactory(
            CameraConnectivityFactory cameraConnectivityFactory,
            CameraRecorderFactory cameraRecorderFactory,
            MotionDetectionSettings motionDetectionSettings,
            ThreadPoolManager threadPoolManager,
            MessageQueueBuilder messageQueueBuilder) {
        ValidationSupport.validateRequired("Camera connectivity factory", cameraConnectivityFactory);
        ValidationSupport.validateRequired("Camera recorder factory", cameraRecorderFactory);
        ValidationSupport.validateRequired("Motion detection settings", motionDetectionSettings);
        ValidationSupport.validateRequired("Thread pool manager", threadPoolManager);
        ValidationSupport.validateRequired("Message queue builder", messageQueueBuilder);

        this.cameraConnectivityFactory = cameraConnectivityFactory;
        this.cameraRecorderFactory = cameraRecorderFactory;
        this.motionDetectionSettings = motionDetectionSettings;
        this.threadPoolManager = threadPoolManager;
        this.messageQueueBuilder = messageQueueBuilder;
    }

    public CameraRuntime create(Camera camera) {
        ValidationSupport.validateRequired("Camera", camera);

        MessageBus cameraMessageBus = new MessageBus();
        IThreadPool cameraThreadPool = threadPoolManager.cameraThreadPool(camera);
        ILogger logger = LoggerFactory.cameraLogger(CameraRuntimeFactory.class, camera.identity);

        int framePollingTimeoutMilliseconds = camera.frameSettings.frameIntervalMilliseconds * 2;
        IPullMessageQueue<SharedMessage<Mat>> cameraFrameQueue = createCameraFrameQueue(
                cameraMessageBus,
                framePollingTimeoutMilliseconds);
        IPullMessageQueue<SharedMessage<Mat>> motionDetectionFrameQueue = createCameraFrameQueue(
                cameraMessageBus,
                framePollingTimeoutMilliseconds);

        ICameraConnectivityController cameraConnectivityController = cameraConnectivityFactory.create(
                camera.connection,
                camera.streamingSettings,
                new SharedMessageFanout<>(List.of(cameraFrameQueue, motionDetectionFrameQueue), Mat::clone, Mat::release),
                cameraThreadPool,
                logger);

        CameraRecordService cameraRecordService = cameraRecorderFactory.create(
                camera.frameSettings, camera.streamingSettings,
                camera.storageSettings(), cameraFrameQueue, cameraThreadPool, logger);

        CameraMotionDetection cameraMotionDetection = new CameraMotionDetection(
                motionDetectionFrameQueue,
                new MoG2MotionDetection(motionDetectionSettings),
                cameraThreadPool,
                logger,
                createRetry());

        MotionStateMachine motionStateMachine = new MotionStateMachine();
        motionStateMachine.addTransition(new InMotionTransition());
        motionStateMachine.addTransition(new NoMotionTransition(motionDetectionSettings.motionEndFrameCount));

        CameraMotionSignalization cameraMotionSignalization = new CameraMotionSignalization(
                cameraMotionDetection,
                motionStateMachine,
                logger);

        CameraMotionRecordController cameraMotionRecordController = new CameraMotionRecordController(
                cameraRecordService,
                cameraMotionDetection,
                cameraMotionSignalization,
                logger);

        return new CameraRuntime(
                cameraConnectivityController,
                cameraMotionRecordController,
                cameraMessageBus,
                logger);
    }

    private IPullMessageQueue<SharedMessage<Mat>> createCameraFrameQueue(
            MessageBus cameraMessageBus,
            int framePollingTimeoutMilliseconds) {
        IPullMessageQueue<SharedMessage<Mat>> queue = messageQueueBuilder.<SharedMessage<Mat>>createPull()
                .withQueueCapacity(1)
                .withDequeueTimeout(framePollingTimeoutMilliseconds)
                .build();

        cameraMessageBus.addQueue(queue);

        return queue;
    }

    private Retry createRetry() {
        return new Retry(new RetryPolicy(3, RetryWaitStrategy.Lineal, 1000, 30000));
    }
}
