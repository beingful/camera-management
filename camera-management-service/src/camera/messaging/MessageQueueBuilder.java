package camera.messaging;

import camera.threading.IThreadPool;
import camera.validation.ValidationSupport;
import jakarta.validation.constraints.Positive;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class MessageQueueBuilder {
    private final AtomicInteger queueId;

    public MessageQueueBuilder() {
        queueId = new AtomicInteger();
    }

    public <TMessage> IPushMessageQueueBuilder<TMessage> createPush() {
        return new PushBuilder<>(queueId.incrementAndGet());
    }

    public <TMessage> IPullMessageQueueBuilder<TMessage> createPull() {
        return new PullBuilder<>(queueId.incrementAndGet());
    }

    private static final class PushBuilder<TMessage> implements IPushMessageQueueBuilder<TMessage> {
        private final int id;
        private final List<IMessageFilter<TMessage>> filters;
        private IThreadPool threadPool;

        private PushBuilder(int id) {
            this.id = id;
            this.filters = new ArrayList<>();
        }

        @Override
        public IPushMessageQueueBuilder<TMessage> withDeduplication(long messageTtlMilliseconds) {
            filters.add(new DeduplicationMessageFilter<>(messageTtlMilliseconds));
            return this;
        }

        @Override
        public IPushMessageQueueBuilder<TMessage> withThreadPool(IThreadPool threadPool) {
            ValidationSupport.validateRequired("Thread pool", threadPool);

            this.threadPool = threadPool;
            return this;
        }

        @Override
        public IPushMessageQueue<TMessage> build() {
            return new PushMessageQueue<>(new PushMessageQueueSettings<>(id, filters, threadPool));
        }

        @Override
        public IPushMessageQueueBuilder<TMessage> withFilter(IMessageFilter<TMessage> filter) {
            ValidationSupport.validateRequired("Message filter", filter);

            filters.add(filter);
            return this;
        }

        @Override
        public IPushMessageQueueBuilder<TMessage> withFilters(Collection<IMessageFilter<TMessage>> filters) {
            ValidationSupport.validateRequired("Message filters", filters);

            for (IMessageFilter<TMessage> filter : filters) {
                withFilter(filter);
            }
            return this;
        }
    }

    private static final class PullBuilder<TMessage> implements IPullMessageQueueBuilder<TMessage> {
        private final int id;
        private final List<IMessageFilter<TMessage>> filters;
        private int capacity;
        private int timeoutMilliseconds;

        private PullBuilder(int id) {
            this.id = id;
            this.filters = new ArrayList<>();
            this.capacity = 1;
            this.timeoutMilliseconds = 0;
        }

        @Override
        public IPullMessageQueueBuilder<TMessage> withQueueCapacity(int capacity) {
            ValidationSupport.validateValue(MessageQueueCapacitySettings.class, "capacity", capacity);

            this.capacity = capacity;
            return this;
        }

        @Override
        public IPullMessageQueueBuilder<TMessage> withDequeueTimeout(int timeoutMilliseconds) {
            ValidationSupport.validateValue(MessageQueueTimeoutSettings.class, "timeoutMilliseconds", timeoutMilliseconds);

            this.timeoutMilliseconds = timeoutMilliseconds;
            return this;
        }

        @Override
        public IPullMessageQueueBuilder<TMessage> withDeduplication(long messageTtlMilliseconds) {
            filters.add(new DeduplicationMessageFilter<>(messageTtlMilliseconds));
            return this;
        }

        @Override
        public IPullMessageQueue<TMessage> build() {
            return new PullMessageQueue<>(new PullMessageQueueSettings<>(id, capacity, timeoutMilliseconds, filters));
        }

        @Override
        public IPullMessageQueueBuilder<TMessage> withFilter(IMessageFilter<TMessage> filter) {
            ValidationSupport.validateRequired("Message filter", filter);

            filters.add(filter);
            return this;
        }

        @Override
        public IPullMessageQueueBuilder<TMessage> withFilters(Collection<IMessageFilter<TMessage>> filters) {
            ValidationSupport.validateRequired("Message filters", filters);

            for (IMessageFilter<TMessage> filter : filters) {
                withFilter(filter);
            }
            return this;
        }
    }

    private record MessageQueueCapacitySettings(@Positive int capacity) {
    }

    private record MessageQueueTimeoutSettings(@jakarta.validation.constraints.PositiveOrZero int timeoutMilliseconds) {
    }
}
