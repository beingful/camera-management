package camera.snapshot;

import java.util.ArrayList;
import java.util.List;

public class SnapshotDifference<TData extends ICapturable> {
    public final List<TData> created;
    public final List<TData> updated;
    public final List<TData> deleted;
    public final int currentCount;

    public SnapshotDifference() {
        this.created = new ArrayList<>();
        this.updated = new ArrayList<>();
        this.deleted = new ArrayList<>();
        this.currentCount = 0;
    }

    public SnapshotDifference(List<TData> created, List<TData> updated, List<TData> deleted) {
        this(created, updated, deleted, 0);
    }

    public SnapshotDifference(List<TData> created, List<TData> updated, List<TData> deleted, int currentCount) {
        this.created = created;
        this.updated = updated;
        this.deleted = deleted;
        this.currentCount = currentCount;
    }
}
