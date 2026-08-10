package camera.runtime;

import camera.logging.LoggerFactory;
import camera.logging.ILogger;
import camera.messaging.MessageBus;
import camera.models.Camera;
import camera.models.CameraSet;
import camera.validation.ValidationSupport;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

public class CameraSetRuntime {
    private final CameraRuntimeFactory cameraRuntimeFactory;
    private final MessageBus systemMessageBus;
    private final Map<Integer, CameraRuntime> cameraRuntimes;
    private final ILogger logger;

    public CameraSetRuntime(CameraRuntimeFactory cameraRuntimeFactory, MessageBus systemMessageBus) {
        ValidationSupport.validateRequired("Camera runtime factory", cameraRuntimeFactory);
        ValidationSupport.validateRequired("System message bus", systemMessageBus);

        this.cameraRuntimeFactory = cameraRuntimeFactory;
        this.systemMessageBus = systemMessageBus;
        this.cameraRuntimes = new HashMap<>();
        this.logger = LoggerFactory.systemLogger(CameraSetRuntime.class);
    }

    public synchronized void connect(CameraSet cameraSet) {
        ValidationSupport.validateRequired("Camera set", cameraSet);

        List<Callable<Void>> startTasks = new ArrayList<>();

        for (Camera camera : cameraSet.cameras()) {
            int cameraId = camera.identity.id;

            if (!cameraRuntimes.containsKey(cameraId)) {
                logger.info("Starting camera runtime: " + camera.identity.name);
                CameraRuntime cameraRuntime = cameraRuntimeFactory.create(camera);
                cameraRuntimes.put(cameraId, cameraRuntime);
                startTasks.add(() -> {
                    cameraRuntime.start();
                    return null;
                });
            }
        }

        executeInParallel("Could not start all camera runtimes.", startTasks);
    }

    public synchronized void disconnect(CameraSet cameraSet) {
        ValidationSupport.validateRequired("Camera set", cameraSet);

        List<Callable<Void>> stopTasks = new ArrayList<>();

        for (Camera camera : cameraSet.cameras()) {
            int cameraId = camera.identity.id;

            CameraRuntime cameraRuntime = cameraRuntimes.getOrDefault(cameraId, null);

            if (cameraRuntime != null) {
                logger.info("Stopping camera runtime: " + camera.identity.name);
                cameraRuntimes.remove(cameraId);
                stopTasks.add(() -> {
                    cameraRuntime.stop();
                    return null;
                });
            }
        }

        executeInParallel("Could not stop all camera runtimes.", stopTasks);
    }

    public synchronized void disconnect(int cameraId) {
        CameraRuntime cameraRuntime = cameraRuntimes.getOrDefault(cameraId, null);

        if (cameraRuntime != null) {
            logger.info("Stopping camera runtime: " + cameraId);
            cameraRuntime.stop();
            cameraRuntimes.remove(cameraId);
        }
    }

    public synchronized void disconnectAll() {
        List<Callable<Void>> stopTasks = new ArrayList<>();

        for (CameraRuntime cameraRuntime : List.copyOf(cameraRuntimes.values())) {
            logger.info("Stopping camera runtime.");
            stopTasks.add(() -> {
                cameraRuntime.stop();
                return null;
            });
        }

        cameraRuntimes.clear();
        executeInParallel("Could not stop all camera runtimes.", stopTasks);
    }

    public synchronized void closeSystemQueues() {
        systemMessageBus.close();
    }

    private void executeInParallel(String errorMessage, List<Callable<Void>> tasks) {
        if (tasks.isEmpty()) {
            return;
        }

        AtomicInteger threadNumber = new AtomicInteger();
        ExecutorService executorService = Executors.newFixedThreadPool(
                tasks.size(),
                runnable -> new Thread(runnable, "camera-runtime-lifecycle-" + threadNumber.incrementAndGet()));

        RuntimeException taskException = null;

        try {
            List<Future<Void>> futures = executorService.invokeAll(tasks);

            for (Future<Void> future : futures) {
                try {
                    future.get();
                }
                catch (ExecutionException exception) {
                    if (taskException == null) {
                        taskException = new IllegalStateException(errorMessage);
                    }

                    taskException.addSuppressed(exception.getCause());
                }
            }
        }
        catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException(errorMessage, exception);
        }
        finally {
            executorService.shutdownNow();
        }

        if (taskException != null) {
            throw taskException;
        }
    }
}
