package helpers;

import camera.CameraSurveillanceController;
import camera.configuration.Configuration;
import camera.models.Camera;
import camera.models.CameraSet;
import camera.snapshot.SnapshotCapturer;
import com.fasterxml.jackson.core.type.TypeReference;
import dummies.FakeCameraSetRuntime;
import dummies.FakeDataObserver;
import dummies.FakeThreadPoolManager;
import dummies.TestLogger;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;

public class CameraConfigurationHarness {
    private static final String CAMERA_FIXTURES = "fixtures/cameras/";

    private final FakeDataObserver observer;
    private final FakeCameraSetRuntime runtime;
    private final FakeThreadPoolManager threadPools;
    private final CameraSurveillanceController controller;

    private final Path yamlPath;

    private CameraConfigurationHarness(
            Path yamlPath,
            FakeDataObserver observer,
            FakeCameraSetRuntime runtime,
            FakeThreadPoolManager threadPools,
            CameraSurveillanceController controller) {
        this.yamlPath = yamlPath;
        this.observer = observer;
        this.runtime = runtime;
        this.threadPools = threadPools;
        this.controller = controller;
    }

    public static CameraConfigurationHarness create(Path testDirectory) throws IOException {
        Path directory = testDirectory.toAbsolutePath().normalize();
        Files.createDirectories(directory);
        Path yamlPath = directory.resolve("cameras.yaml");

        Configuration<CameraSet> configuration = new Configuration<>(
                yamlPath,
                new TypeReference<CameraSet>() {},
                CameraSetMapper.create());
        FakeDataObserver observer = new FakeDataObserver(configuration);
        SnapshotCapturer<CameraSet, Camera> snapshots = new SnapshotCapturer<>(configuration, CameraSet::cameras);
        FakeCameraSetRuntime runtime = new FakeCameraSetRuntime();
        FakeThreadPoolManager threadPools = new FakeThreadPoolManager();
        CameraSurveillanceController controller = new CameraSurveillanceController(
                observer,
                configuration,
                snapshots,
                runtime,
                threadPools,
                new TestLogger());

        return new CameraConfigurationHarness(yamlPath, observer, runtime, threadPools, controller);
    }

    public void useCamerasYaml(String fixtureName) throws IOException {
        String resourceName = CAMERA_FIXTURES + fixtureName;
        try (InputStream inputStream = CameraConfigurationHarness.class
                .getClassLoader()
                .getResourceAsStream(resourceName)) {
            if (inputStream == null) {
                throw new IOException("Missing test fixture: " + resourceName);
            }

            Files.copy(inputStream, yamlPath, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    public void connect() {
        controller.connect();
    }

    public void disconnect() {
        controller.disconnect();
    }

    public void triggerConfigurationChange() {
        observer.triggerChange();
    }

    public void clearEvents() {
        runtime.clear();
        threadPools.clear();
    }

    public List<Integer> connectedIds() {
        return runtime.connectedIds();
    }

    public List<Integer> disconnectedIds() {
        return runtime.disconnectedIds();
    }

    public List<Integer> createdThreadPoolIds() {
        return threadPools.createdIds();
    }

    public List<Integer> shutdownThreadPoolIds() {
        return threadPools.shutdownIds();
    }

    public boolean observerStarted() {
        return observer.started();
    }

    public boolean observerStopped() {
        return observer.stopped();
    }

    public boolean disconnectedAll() {
        return runtime.disconnectedAll();
    }

    public boolean shutdownAllThreadPools() {
        return threadPools.shutdownAll();
    }

    public boolean systemQueuesClosed() {
        return runtime.systemQueuesClosed();
    }
}
