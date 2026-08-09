package tests.configuration;

import java.util.List;

public class ConfigurationChangeTestCases {
    public static List<ConfigurationChangeTestCase> additions() {
        return List.of(
                new ConfigurationChangeTestCase(
                        "add first camera to empty file",
                        "empty.yaml",
                        "front-only.yaml",
                        List.of(1),
                        List.of(),
                        List.of(1),
                        List.of()),
                new ConfigurationChangeTestCase(
                        "add second camera to existing file",
                        "front-only.yaml",
                        "front-and-garage.yaml",
                        List.of(2),
                        List.of(),
                        List.of(2),
                        List.of()));
    }

    public static List<ConfigurationChangeTestCase> updates() {
        return List.of(
                new ConfigurationChangeTestCase(
                        "update single camera connection and frame rate",
                        "front-only.yaml",
                        "front-updated.yaml",
                        List.of(1),
                        List.of(1),
                        List.of(1),
                        List.of(1)),
                new ConfigurationChangeTestCase(
                        "update one camera while another remains unchanged",
                        "front-and-garage.yaml",
                        "front-and-garage-updated.yaml",
                        List.of(2),
                        List.of(2),
                        List.of(2),
                        List.of(2)),
                new ConfigurationChangeTestCase(
                        "rewrite identical camera file without runtime changes",
                        "front-only.yaml",
                        "front-only.yaml",
                        List.of(),
                        List.of(),
                        List.of(),
                        List.of()));
    }

    public static List<ConfigurationChangeTestCase> deletions() {
        return List.of(
                new ConfigurationChangeTestCase(
                        "delete one camera from multiple camera file",
                        "front-and-garage.yaml",
                        "front-only.yaml",
                        List.of(),
                        List.of(2),
                        List.of(),
                        List.of(2)),
                new ConfigurationChangeTestCase(
                        "delete last camera from file",
                        "front-only.yaml",
                        "empty.yaml",
                        List.of(),
                        List.of(1),
                        List.of(),
                        List.of(1)));
    }
}
