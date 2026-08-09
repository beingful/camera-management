package tests.connectivity;

import java.util.List;

public class InitialConnectTestCases {
    public static List<InitialConnectTestCase> emptyCameraFile() {
        return List.of(new InitialConnectTestCase(
                "initial connect with empty camera file",
                "empty.yaml",
                List.of(),
                List.of()));
    }

    public static List<InitialConnectTestCase> oneCamera() {
        return List.of(new InitialConnectTestCase(
                "initial connect with one camera",
                "front-only.yaml",
                List.of(1),
                List.of(1)));
    }

    public static List<InitialConnectTestCase> multipleCameras() {
        return List.of(new InitialConnectTestCase(
                "initial connect with multiple cameras",
                "front-and-garage.yaml",
                List.of(1, 2),
                List.of(1, 2)));
    }
}
