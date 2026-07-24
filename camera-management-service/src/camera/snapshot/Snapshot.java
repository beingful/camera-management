package camera.snapshot;

import camera.validation.ValidationSupport;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Snapshot<TData extends ICapturable> {
    public final Map<Integer, TData> dataSet;

    public Snapshot(List<TData> dataSet) {
        ValidationSupport.validateRequired("Capturable data set", dataSet);

        this.dataSet = dataSet.stream().collect(
                Collectors.toMap(TData::id, data -> data));
    }
}
