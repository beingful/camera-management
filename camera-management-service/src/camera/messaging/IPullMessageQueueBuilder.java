package camera.messaging;

import java.util.Collection;

public interface IPullMessageQueueBuilder<TMessage> {
    IPullMessageQueueBuilder<TMessage> withQueueCapacity(int capacity);

    IPullMessageQueueBuilder<TMessage> withDequeueTimeout(int timeoutMilliseconds);

    IPullMessageQueueBuilder<TMessage> withDeduplication(long messageTtlMilliseconds);

    IPullMessageQueueBuilder<TMessage> withFilter(IMessageFilter<TMessage> filter);

    IPullMessageQueueBuilder<TMessage> withFilters(Collection<IMessageFilter<TMessage>> filters);

    IPullMessageQueue<TMessage> build();
}
