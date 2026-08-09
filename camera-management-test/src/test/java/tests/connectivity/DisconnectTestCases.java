package tests.connectivity;

import java.util.List;

public class DisconnectTestCases {
    public static List<DisconnectTestCase> emptyCameraFile() {
        return List.of(new DisconnectTestCase(
                "disconnect after empty camera file",
                "empty.yaml"));
    }

    public static List<DisconnectTestCase> oneCameraConnected() {
        return List.of(new DisconnectTestCase(
                "disconnect after one camera connected",
                "front-only.yaml"));
    }

    public static List<DisconnectTestCase> multipleCamerasConnected() {
        return List.of(new DisconnectTestCase(
                "disconnect after multiple cameras connected",
                "front-and-garage.yaml"));
    }
}
