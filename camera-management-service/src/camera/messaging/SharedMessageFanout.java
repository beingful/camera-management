package camera.messaging;

import camera.validation.ValidationSupport;

import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

public class SharedMessageFanout<TMessage> implements IQueueSubscriber<TMessage> {
    private final List<IPullMessageQueue<SharedMessage<TMessage>>> queues;
    private final Function<TMessage, TMessage> messageCloner;
    private final Consumer<TMessage> messageReleaser;

    public SharedMessageFanout(Collection<IPullMessageQueue<SharedMessage<TMessage>>> queues) {
        this(queues, null, null);
    }

    public SharedMessageFanout(
            Collection<IPullMessageQueue<SharedMessage<TMessage>>> queues,
            Function<TMessage, TMessage> messageCloner) {
        this(queues, messageCloner, null);
    }

    public SharedMessageFanout(
            Collection<IPullMessageQueue<SharedMessage<TMessage>>> queues,
            Function<TMessage, TMessage> messageCloner,
            Consumer<TMessage> messageReleaser) {
        ValidationSupport.validateRequired("Queues", queues);

        this.queues = List.copyOf(queues);
        this.messageCloner = messageCloner;
        this.messageReleaser = messageReleaser;
        for (IPullMessageQueue<SharedMessage<TMessage>> queue : this.queues) {
            ValidationSupport.validateRequired("Queue", queue);
        }
    }

    @Override
    public void push(TMessage message) {
        ValidationSupport.validateRequired("Message", message);

        if (queues.isEmpty()) {
            closeMessage(message);
            return;
        }

        if (messageCloner != null) {
            pushClonedMessages(message);
            return;
        }

        SharedMessage<TMessage> sharedMessage = new SharedMessage<>(message, queues.size(), messageReleaser);
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
                TMessage queueMessage = null;
                boolean enqueued = false;

                try {
                    queueMessage = messageCloner.apply(message);
                    ValidationSupport.validateRequired("Cloned message", queueMessage);

                    queue.enqueue(new SharedMessage<>(queueMessage, 1, messageReleaser));
                    enqueued = true;
                }
                catch (RuntimeException exception) {
                    if (!enqueued) {
                        closeMessage(queueMessage);
                    }

                    if (fanoutException == null) {
                        fanoutException = new IllegalStateException("Could not fan out message to all queues.");
                    }

                    fanoutException.addSuppressed(exception);
                }
            }
        }
        finally {
            closeMessage(message);
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

    private void closeMessage(TMessage message) {
        if (message == null) {
            return;
        }

        if (messageReleaser != null) {
            messageReleaser.accept(message);
            return;
        }

        closeAutoCloseable(message);
    }
}
