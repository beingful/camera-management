# Camera Management Test

Separate test harness for `../camera-management-service`.

The harness is a Maven/JUnit 5 project that depends on the `camera-management-service` artifact and runs tests with fakes. It does not modify the service source and does not use the real service `config/cameras.yaml`.
Camera configuration scenarios are stored as YAML fixture resources under `src/test/resources/fixtures/cameras/`.
Sources use the standard Maven test layout under `src/test/java`. Target-specific tests live under `src/test/java/tests/connectivity`, `src/test/java/tests/configuration`, and `src/test/java/tests/motiondetection`.
JUnit executes parameterized test cases in parallel, reports all pass/fail results through Surefire, and exits non-zero when any case fails.

## Run

```bash
./run-tests.sh
```

The script uses the Maven Wrapper, installs `../camera-management-service` into the local Maven repository with tests skipped, then runs this project's JUnit suite.

## Covered

- Initial camera configuration load on controller connect.
- Camera add, update, no-op rewrite, and delete reactions after a test `cameras.yaml` change.
- Controller disconnect behavior for empty, single-camera, and multi-camera configurations.
- Motion state transitions for detected, undetected, threshold, and resumed-motion sequences.

## Current Limits

- Full pixel-level motion detection is not covered. The concrete `CameraMotionSignalization` constructor requires a concrete `CameraMotionDetection`, whose constructor requires `MoG2MotionDetection` and OpenCV native state. Testing that cleanly would require a service change, such as depending on an interface for detection events.
- End-to-end RTSP connect/disconnect against real cameras is not covered. The tests verify that the service controller asks its runtime to connect/disconnect the expected cameras without opening network streams.
