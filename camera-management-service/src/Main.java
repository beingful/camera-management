import camera.CameraSurveillanceWorker;
import java.nio.file.Files;
import java.nio.file.Path;

void main() {
    loadOpenCv();

    CameraSurveillanceWorker cameraSurveillanceWorker =
            new CameraSurveillanceWorker();

    cameraSurveillanceWorker.start();
}

private void loadOpenCv() {
    String configuredLibraryPath = System.getenv("OPENCV_JAVA_LIBRARY");
    Path libraryPath = configuredLibraryPath == null || configuredLibraryPath.isBlank()
            ? Path.of("target/opencv-ffmpeg/share/java/opencv4/libopencv_java490.dylib")
            : Path.of(configuredLibraryPath);

    if (!Files.isRegularFile(libraryPath)) {
        throw new IllegalStateException(
                "OpenCV Java native library was not found: " + libraryPath +
                        ". Run ./opencv-build or set OPENCV_JAVA_LIBRARY.");
    }

    System.load(libraryPath.toAbsolutePath().toString());
}
