package camera.utils;

import java.time.Clock;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;

public final class TtlHashSet<TValue> {
    private final Map<TValue, Long> values;
    private final long ttlMilliseconds;
    private final Clock clock;

    public TtlHashSet(long ttlMilliseconds) {
        this(ttlMilliseconds, Clock.systemUTC());
    }

    TtlHashSet(long ttlMilliseconds, Clock clock) {
        if (ttlMilliseconds < 1) {
            throw new IllegalArgumentException("TTL must be greater than 0.");
        }

        this.values = new HashMap<>();
        this.ttlMilliseconds = ttlMilliseconds;
        this.clock = Objects.requireNonNull(clock, "Clock is required.");
    }

    public synchronized boolean contains(TValue value) {
        removeExpiredValues();

        return values.containsKey(value);
    }

    public synchronized boolean add(TValue value) {
        removeExpiredValues();

        if (values.containsKey(value)) {
            return false;
        }

        values.put(value, expiresAt());

        return true;
    }

    public synchronized void remove(TValue value) {
        values.remove(value);
    }

    public synchronized int size() {
        removeExpiredValues();

        return values.size();
    }

    private long expiresAt() {
        return clock.millis() + ttlMilliseconds;
    }

    private void removeExpiredValues() {
        long now = clock.millis();
        Iterator<Map.Entry<TValue, Long>> iterator = values.entrySet().iterator();

        while (iterator.hasNext()) {
            Map.Entry<TValue, Long> entry = iterator.next();

            if (entry.getValue() <= now) {
                iterator.remove();
            }
        }
    }
}
