package camera.messaging;

import camera.validation.ValidationSupport;

import java.util.Collection;
import java.util.List;
import java.util.function.Function;

public class SharedMessageFanout<TMessage> implements IQueueSubscriber<TMessage> {
    private final List<IPullMessageQueue<SharedMessage<TMessage>>> queues;
    private final Function<TMessage, TMessage> messageCloner;

    public SharedMessageFanout(Collection<IPullMessageQueue<SharedMessage<TMessage>>> queues) {
        this(queues, null);
    }

    public SharedMessageFanout(
            Collection<IPullMessageQueue<SharedMessage<TMessage>>> queues,
            Function<TMessage, TMessage> messageCloner) {
        ValidationSupport.validateRequired("Queues", queues);

        this.queues = List.copyOf(queues);
        this.messageCloner = messageCloner;
        for (IPullMessageQueue<SharedMessage<TMessage>> queue : this.queues) {
            ValidationSupport.validateRequired("Queue", queue);
        }
    }

    @Override
    public void push(TMessage message) {
        ValidationSupport.validateRequired("Message", message);

        if (queues.isEmpty()) {
            closeAutoCloseable(message);
            return;
        }

        if (messageCloner != null) {
            pushClonedMessages(message);
            return;
        }

        SharedMessage<TMessage> sharedMessage = new SharedMessage<>(message, queues.size());
        RuntimeException fanoutException = null;

        for (IPullMessageQueue<SharedMessage<TMessage>> queue : queues) {
            try {
                queue.enqueue(sharedMessage);
            }
            catch (RuntimeException exception) {
                if (fanoutException == null) {
                    fanoutException = new IllegalStateException("Could not fan out message to all queues.");
                }

                fanoutException.addSuppressed(exception);
            }
        }

        if (fanoutException != null) {
            throw fanoutException;
        }
    }

    private void pushClonedMessages(TMessage message) {
        RuntimeException fanoutException = null;

        try {
            for (IPullMessageQueue<SharedMessage<TMessage>> queue : queues) {
                try {
                    TMessage queueMessage = messageCloner.apply(message);
                    ValidationSupport.validateRequired("Cloned message", queueMessage);

                    queue.enqueue(new SharedMessage<>(queueMessage, 1));
                }
                catch (RuntimeException exception) {
                    if (fanoutException == null) {
                        fanoutException = new IllegalStateException("Could not fan out message to all queues.");
                    }

                    fanoutException.addSuppressed(exception);
                }
            }
        }
        finally {
            closeAutoCloseable(message);
        }

        if (fanoutException != null) {
            throw fanoutException;
        }
    }

    private void closeAutoCloseable(Object message) {
        if (message instanceof AutoCloseable closeable) {
            try {
                closeable.close();
            }
            catch (Exception exception) {
                throw new IllegalStateException("Could not close unconsumed message.", exception);
            }
        }
    }
}
