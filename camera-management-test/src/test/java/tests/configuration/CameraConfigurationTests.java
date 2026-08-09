package tests.configuration;

import helpers.CameraConfigurationHarness;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CameraConfigurationTests {
    @ParameterizedTest(name = "{0}")
    @MethodSource({
            "tests.configuration.ConfigurationChangeTestCases#additions",
            "tests.configuration.ConfigurationChangeTestCases#updates",
            "tests.configuration.ConfigurationChangeTestCases#deletions"
    })
    void testCameraConfigurationChanges(ConfigurationChangeTestCase testCase, @TempDir Path testDirectory) throws Exception {
        CameraConfigurationHarness harness = CameraConfigurationHarness.create(testDirectory);
        harness.useCamerasYaml(testCase.initialFixtureName());
        harness.connect();
        harness.clearEvents();

        harness.useCamerasYaml(testCase.changedFixtureName());
        harness.triggerConfigurationChange();

        assertEquals(testCase.expectedConnectedIds(), harness.connectedIds(),
                testCase.name() + ": connected camera ids");
        assertEquals(testCase.expectedDisconnectedIds(), harness.disconnectedIds(),
                testCase.name() + ": disconnected camera ids");
        assertEquals(testCase.expectedCreatedThreadPoolIds(), harness.createdThreadPoolIds(),
                testCase.name() + ": created thread pool ids");
        assertEquals(testCase.expectedShutdownThreadPoolIds(), harness.shutdownThreadPoolIds(),
                testCase.name() + ": shutdown thread pool ids");
    }
}
