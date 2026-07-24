package camera;

import camera.configuration.CameraSetConfiguration;
import camera.configuration.DataObserver;
import camera.connection.CameraConnectivityFactory;
import camera.error.IErrorSubscriber;
import camera.error.SurveillanceError;
import camera.messaging.IPushMessageQueue;
import camera.messaging.MessageBus;
import camera.messaging.MessageQueueBuilder;
import camera.models.Camera;
import camera.models.CameraSet;
import camera.motion.detection.MotionDetectionConfiguration;
import camera.motion.detection.MotionDetectionSettings;
import camera.logging.LoggerFactory;
import camera.recording.CameraRecorderFactory;
import camera.recording.video.VideoWriterFactory;
import camera.recording.video.file.VideoFileProcessor;
import camera.recording.video.session.VideoFile;
import camera.recording.video.session.VideoSessionManager;
import camera.recording.video.session.VideoSessionSettings;
import camera.retry.Retry;
import camera.retry.RetryPolicy;
import camera.retry.RetryWaitStrategy;
import camera.runtime.CameraRuntimeFactory;
import camera.runtime.CameraSetRuntime;
import camera.snapshot.SnapshotCapturer;
import camera.threading.ThreadPoolManager;
import camera.validation.ValidationSupport;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class CameraSurveillanceServiceProvider {

    public CameraSurveillanceController getController(IErrorSubscriber errorSubscriber) {
        ValidationSupport.validateRequired("Error subscriber", errorSubscriber);

        MessageBus systemMessageBus = new MessageBus();
        MessageQueueBuilder messageQueueBuilder = new MessageQueueBuilder();

        IPushMessageQueue<SurveillanceError> errorQueue = messageQueueBuilder.<SurveillanceError>createPush()
                .withDeduplication(5000)
                .build();
        errorQueue.subscribe(errorSubscriber::onError);
        systemMessageBus.addQueue(errorQueue);

        ThreadPoolManager threadPoolManager = new ThreadPoolManager(errorQueue);

        CameraSetConfiguration cameraSetConfiguration =
                new CameraSetConfiguration();

        DataObserver<CameraSet> cameraSetDataObserver =
                new DataObserver<>(cameraSetConfiguration, threadPoolManager.systemThreadPool(), createRetry());

        SnapshotCapturer<CameraSet, Camera> cameraSetSnapshotCapturer =
                new SnapshotCapturer<>(cameraSetConfiguration, CameraSet::cameras);

        CameraSetRuntime cameraSetRuntime = getCameraSetRuntime(
                threadPoolManager,
                systemMessageBus,
                messageQueueBuilder);

        CameraSurveillanceController cameraSurveillanceController = new CameraSurveillanceController(
                cameraSetDataObserver,
                cameraSetConfiguration,
                cameraSetSnapshotCapturer,
                cameraSetRuntime,
                threadPoolManager,
                LoggerFactory.systemLogger(CameraSurveillanceController.class));

        return cameraSurveillanceController;
    }

    private CameraSetRuntime getCameraSetRuntime(
            ThreadPoolManager threadPoolManager,
            MessageBus systemMessageBus,
            MessageQueueBuilder messageQueueBuilder) {
        CameraConnectivityFactory cameraConnectivityFactory =
                new CameraConnectivityFactory();
        MotionDetectionSettings motionDetectionSettings = getMotionDetectionSettings();

        IPushMessageQueue<VideoFile> videoFileQueue = messageQueueBuilder.<VideoFile>createPush().build();
        systemMessageBus.addQueue(videoFileQueue);
        new VideoFileProcessor(videoFileQueue, LoggerFactory.systemLogger(VideoFileProcessor.class));

        CameraRecorderFactory cameraRecorderFactory = new CameraRecorderFactory(
                videoFileQueue,
                new VideoWriterFactory(),
                new VideoSessionManager(
                        new VideoSessionSettings(1, TimeUnit.HOURS)
                ));

        CameraRuntimeFactory cameraRuntimeFactory = new CameraRuntimeFactory(
                cameraConnectivityFactory,
                cameraRecorderFactory,
                motionDetectionSettings,
                threadPoolManager,
                messageQueueBuilder);

        return new CameraSetRuntime(cameraRuntimeFactory, systemMessageBus);
    }

    private MotionDetectionSettings getMotionDetectionSettings() {
        try {
            return new MotionDetectionConfiguration().getConfiguration();
        }
        catch (IOException exception) {
            throw new IllegalStateException("Could not load motion detection configuration.", exception);
        }
    }

    private Retry createRetry() {
        return new Retry(new RetryPolicy(3, RetryWaitStrategy.Lineal, 1000, 30000));
    }
}
