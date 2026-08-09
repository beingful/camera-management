package tests.configuration;

import java.util.List;

public record ConfigurationChangeTestCase(
        String name,
        String initialFixtureName,
        String changedFixtureName,
        List<Integer> expectedConnectedIds,
        List<Integer> expectedDisconnectedIds,
        List<Integer> expectedCreatedThreadPoolIds,
        List<Integer> expectedShutdownThreadPoolIds) {
    @Override
    public String toString() {
        return name;
    }
}
