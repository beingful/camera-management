package tests.connectivity;

import helpers.CameraConfigurationHarness;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CameraConnectivityTests {
    @ParameterizedTest(name = "{0}")
    @MethodSource({
            "tests.connectivity.InitialConnectTestCases#emptyCameraFile",
            "tests.connectivity.InitialConnectTestCases#oneCamera",
            "tests.connectivity.InitialConnectTestCases#multipleCameras"
    })
    void testInitialConnectLoadsConfiguredCameras(InitialConnectTestCase testCase, @TempDir Path testDirectory) throws Exception {
        CameraConfigurationHarness harness = CameraConfigurationHarness.create(testDirectory);
        harness.useCamerasYaml(testCase.fixtureName());

        harness.connect();

        assertEquals(testCase.expectedConnectedIds(), harness.connectedIds(),
                testCase.name() + ": connected camera ids");
        assertEquals(testCase.expectedThreadPoolIds(), harness.createdThreadPoolIds(),
                testCase.name() + ": created thread pool ids");
        assertTrue(harness.observerStarted(),
                testCase.name() + ": observer should start");
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource({
            "tests.connectivity.DisconnectTestCases#emptyCameraFile",
            "tests.connectivity.DisconnectTestCases#oneCameraConnected",
            "tests.connectivity.DisconnectTestCases#multipleCamerasConnected"
    })
    void testControllerDisconnectStopsEverything(DisconnectTestCase testCase, @TempDir Path testDirectory) throws Exception {
        CameraConfigurationHarness harness = CameraConfigurationHarness.create(testDirectory);
        harness.useCamerasYaml(testCase.fixtureName());
        harness.connect();
        harness.clearEvents();

        harness.disconnect();

        assertTrue(harness.observerStopped(),
                testCase.name() + ": observer should stop");
        assertTrue(harness.disconnectedAll(),
                testCase.name() + ": runtime should disconnect all");
        assertTrue(harness.shutdownAllThreadPools(),
                testCase.name() + ": thread pools should shut down");
        assertTrue(harness.systemQueuesClosed(),
                testCase.name() + ": system queues should close");
    }
}
