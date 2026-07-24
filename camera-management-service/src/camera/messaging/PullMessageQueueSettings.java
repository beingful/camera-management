package camera.messaging;

import camera.validation.ValidationSupport;

import java.util.Collection;
import java.util.List;

public record PullMessageQueueSettings<TMessage>(
        int id,
        int capacity,
        int timeoutMilliseconds,
        Collection<IMessageFilter<TMessage>> filters) {
    public PullMessageQueueSettings {
        if (capacity < 1) {
            throw new IllegalArgumentException("Queue capacity must be greater than 0.");
        }

        if (timeoutMilliseconds < 0) {
            throw new IllegalArgumentException("Dequeue timeout must be greater than or equal to 0.");
        }

        ValidationSupport.validateRequired("Message filters", filters);

        filters = List.copyOf(filters);
        for (IMessageFilter<TMessage> filter : filters) {
            ValidationSupport.validateRequired("Message filter", filter);
        }
    }
}
