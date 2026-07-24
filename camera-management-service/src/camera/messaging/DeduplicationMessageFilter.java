package camera.messaging;

import camera.utils.TtlHashSet;
import camera.validation.ValidationSupport;
import jakarta.validation.constraints.Positive;

public class DeduplicationMessageFilter<TMessage> implements IMessageFilter<TMessage> {
    private final TtlHashSet<TMessage> messages;

    public DeduplicationMessageFilter(long messageTtlMilliseconds) {
        ValidationSupport.validateValue(DeduplicationMessageFilterSettings.class, "messageTtlMilliseconds", messageTtlMilliseconds);

        messages = new TtlHashSet<>(messageTtlMilliseconds);
    }

    @Override
    public boolean canSend(TMessage message) {
        ValidationSupport.validateRequired("Message", message);

        return messages.add(message);
    }

    private record DeduplicationMessageFilterSettings(@Positive long messageTtlMilliseconds) {
    }
}
