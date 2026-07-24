package camera.messaging;

import camera.threading.IThreadPool;

import java.util.Collection;

public interface IPushMessageQueueBuilder<TMessage> {
    IPushMessageQueueBuilder<TMessage> withDeduplication(long messageTtlMilliseconds);

    IPushMessageQueueBuilder<TMessage> withFilter(IMessageFilter<TMessage> filter);

    IPushMessageQueueBuilder<TMessage> withFilters(Collection<IMessageFilter<TMessage>> filters);

    IPushMessageQueueBuilder<TMessage> withThreadPool(IThreadPool threadPool);

    IPushMessageQueue<TMessage> build();
}
