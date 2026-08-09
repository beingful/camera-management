package tests.connectivity;

import java.util.List;

public record InitialConnectTestCase(
        String name,
        String fixtureName,
        List<Integer> expectedConnectedIds,
        List<Integer> expectedThreadPoolIds) {
    @Override
    public String toString() {
        return name;
    }
}
