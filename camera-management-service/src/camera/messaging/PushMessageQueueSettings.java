package camera.messaging;

import camera.threading.IThreadPool;
import camera.validation.ValidationSupport;

import java.util.Collection;
import java.util.List;

public record PushMessageQueueSettings<TMessage>(
        int id,
        Collection<IMessageFilter<TMessage>> filters,
        IThreadPool threadPool) {
    public PushMessageQueueSettings {
        ValidationSupport.validateRequired("Message filters", filters);

        filters = List.copyOf(filters);
        for (IMessageFilter<TMessage> filter : filters) {
            ValidationSupport.validateRequired("Message filter", filter);
        }
    }
}
