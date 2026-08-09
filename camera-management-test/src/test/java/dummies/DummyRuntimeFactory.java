package dummies;

import camera.connection.CameraConnectivityFactory;
import camera.messaging.MessageQueueBuilder;
import camera.motion.detection.MotionDetectionSettings;
import camera.recording.CameraRecorderFactory;
import camera.recording.video.VideoWriterFactory;
import camera.recording.video.session.VideoFile;
import camera.recording.video.session.VideoSessionManager;
import camera.recording.video.session.VideoSessionSettings;
import camera.runtime.CameraRuntimeFactory;

import java.util.concurrent.TimeUnit;

public class DummyRuntimeFactory {
    static CameraRuntimeFactory create() {
        FakeThreadPoolManager threadPools = new FakeThreadPoolManager();
        CameraRecorderFactory recorderFactory = new CameraRecorderFactory(
                new FakePushQueue<VideoFile>(),
                new VideoWriterFactory(),
                new VideoSessionManager(new VideoSessionSettings(1, TimeUnit.SECONDS)));

        return new CameraRuntimeFactory(
                new CameraConnectivityFactory(),
                recorderFactory,
                new MotionDetectionSettings(1, 16.0, false, -1.0, 1.0, 1),
                threadPools,
                new MessageQueueBuilder());
    }
}
