package camera.configuration;

import camera.logging.ILogger;
import camera.snapshot.ICapturable;
import camera.snapshot.SnapshotCapturer;
import camera.snapshot.SnapshotDifference;
import camera.validation.ValidationSupport;

import java.io.IOException;

public abstract class DataController<TConfiguration, TData extends ICapturable> implements IDataController {
    private final DataObserver<TConfiguration> dataObserver;
    private final Configuration<TConfiguration> configuration;
    private final SnapshotCapturer<TConfiguration, TData> snapshotCapturer;
    private final ILogger logger;

    private SnapshotDifference<TData> dataChanges;

    protected DataController(
            DataObserver<TConfiguration> dataObserver,
            Configuration<TConfiguration> configuration,
            SnapshotCapturer<TConfiguration, TData> snapshotCapturer,
            ILogger logger) {
        ValidationSupport.validateRequired("Data observer", dataObserver);
        ValidationSupport.validateRequired("Configuration", configuration);
        ValidationSupport.validateRequired("Snapshot capturer", snapshotCapturer);
        ValidationSupport.validateRequired("Logger", logger);

        this.dataObserver = dataObserver;
        this.configuration = configuration;
        this.snapshotCapturer = snapshotCapturer;
        this.logger = logger;
        this.dataChanges = new SnapshotDifference<>();
    }

    public void connect() {
        try {
            onDataChanged();
            dataObserver.startObserving(this);
        }
        catch (IOException exception) {
            throw new IllegalStateException("Could not start data controller.", exception);
        }
    }

    public void disconnect() {
        dataObserver.stopObserving();
    }

    @Override
    public void onDataChanged() {
        try {
            logger.info("Configuration data changed.");
            configuration.reload();
            dataChanges = snapshotCapturer.takeSnapshot();
            updateData();
        }
        catch (IOException exception) {
            throw new IllegalStateException("Could not update data controller.", exception);
        }
    }

    protected SnapshotDifference<TData> dataChanges() {
        return dataChanges;
    }

    protected abstract void updateData();
}
