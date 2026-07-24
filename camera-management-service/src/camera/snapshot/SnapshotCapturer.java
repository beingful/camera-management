package camera.snapshot;

import camera.configuration.Configuration;
import camera.validation.ValidationSupport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class SnapshotCapturer<TConfiguration, TData extends ICapturable> {
    private final Configuration<TConfiguration> configuration;
    private final Function<TConfiguration, List<TData>> dataExtractor;

    private Snapshot<TData> currentSnapshot;

    public SnapshotCapturer(
            Configuration<TConfiguration> configuration,
            Function<TConfiguration, List<TData>> dataExtractor) {
        ValidationSupport.validateRequired("Configuration", configuration);
        ValidationSupport.validateRequired("Snapshot data extractor", dataExtractor);

        this.configuration = configuration;
        this.dataExtractor = dataExtractor;
        this.currentSnapshot = new Snapshot<>(List.of());
    }

    public SnapshotDifference<TData> takeSnapshot() throws IOException {
        TConfiguration configurationData = configuration.getConfiguration();
        List<TData> updatedData = dataExtractor.apply(configurationData);
        Snapshot<TData> latestSnapshot = new Snapshot<>(updatedData);
        SnapshotDifference<TData> latestChanges = snapshotsDifference(latestSnapshot);

        currentSnapshot = latestSnapshot;

        return latestChanges;
    }

    private SnapshotDifference<TData> snapshotsDifference(Snapshot<TData> latestSnapshot) {
        List<TData> created = new ArrayList<>();
        List<TData> updated = new ArrayList<>();
        List<TData> deleted = new ArrayList<>();

        for (TData data : latestSnapshot.dataSet.values()) {
            int dataId = data.id();

            if (!currentSnapshot.dataSet.containsKey(dataId)) {
                created.add(data);
            }
        }

        for (TData data : currentSnapshot.dataSet.values()) {
            int dataId = data.id();

            if (!latestSnapshot.dataSet.containsKey(dataId)) {
                deleted.add(data);
            }
            else if (!latestSnapshot.dataSet.get(dataId).equals(data)) {
                updated.add(data);
            }
        }

        return new SnapshotDifference<>(created, updated, deleted, latestSnapshot.dataSet.size());
    }
}
