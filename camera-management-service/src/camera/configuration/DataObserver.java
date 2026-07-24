package camera.configuration;

import camera.logging.LoggerFactory;
import camera.logging.ILogger;
import camera.retry.Retry;
import camera.threading.IThreadPool;
import camera.threading.InLoopTaskExecutor;
import camera.threading.ThreadServiceType;
import camera.validation.ValidationSupport;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.FileTime;
import java.util.concurrent.TimeUnit;

public class DataObserver<TData> extends InLoopTaskExecutor {
    public final Configuration<TData> configuration;
    private final ILogger logger;
    private IDataController dataController;
    private FileTime lastModifiedTime;

    public DataObserver(Configuration<TData> configuration, IThreadPool threadPool, Retry retry) {
        super(ThreadServiceType.ConfigurationFileWatcher, threadPool, LoggerFactory.systemLogger(DataObserver.class), retry);

        ValidationSupport.validateRequired("Configuration", configuration);

        this.configuration = configuration;
        this.logger = LoggerFactory.systemLogger(DataObserver.class);
    }

    public synchronized void startObserving(IDataController dataController) throws IOException {
        ValidationSupport.validateRequired("Data controller", dataController);

        if (!executing) {
            if (!Files.isRegularFile(configuration.filePath)) {
                throw new IOException("Configuration file does not exist: " + configuration.filePath);
            }

            logger.info("Starting configuration data observer.");
            this.dataController = dataController;
            startTask();
        }
    }

    public synchronized void stopObserving() {
        stopTask();
    }

    @Override
    protected void beforeLoop() throws IOException {
        if (!Files.isRegularFile(configuration.filePath)) {
            throw new IOException("Configuration file does not exist: " + configuration.filePath);
        }

        lastModifiedTime = getLastModifiedTime();
    }

    @Override
    protected void execute() throws IOException, InterruptedException {
        TimeUnit.MILLISECONDS.sleep(500);

        FileTime currentModifiedTime = getLastModifiedTime();

        if (!currentModifiedTime.equals(lastModifiedTime)) {
            lastModifiedTime = currentModifiedTime;
            dataController.onDataChanged();
        }
    }

    private FileTime getLastModifiedTime() throws IOException {
        return Files.getLastModifiedTime(configuration.filePath);
    }
}
