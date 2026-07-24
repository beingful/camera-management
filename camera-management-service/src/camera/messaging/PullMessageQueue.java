package camera.messaging;

import camera.logging.ILogger;
import camera.logging.LoggerFactory;
import camera.validation.ValidationSupport;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

public class PullMessageQueue<TMessage> implements IPullMessageQueue<TMessage> {
    private static final ILogger logger = LoggerFactory.systemLogger(PullMessageQueue.class);

    private final int id;
    private final BlockingQueue<TMessage> messages;
    private final List<IQueueSubscriber<TMessage>> subscribers;
    private final List<IMessageFilter<TMessage>> filters;
    private final int timeoutMilliseconds;
    private boolean closed;

    public PullMessageQueue(PullMessageQueueSettings<TMessage> settings) {
        ValidationSupport.validateRequired("Pull message queue settings", settings);

        this.id = settings.id();
        this.messages = new ArrayBlockingQueue<>(settings.capacity());
        this.subscribers = new ArrayList<>();
        this.filters = List.copyOf(settings.filters());
        this.timeoutMilliseconds = settings.timeoutMilliseconds();
        this.closed = false;
    }

    @Override
    public int id() {
        return id;
    }

    @Override
    public synchronized void subscribe(IQueueSubscriber<TMessage> subscriber) {
        ValidationSupport.validateRequired("Queue subscriber", subscriber);

        if (closed) {
            throw new IllegalStateException("Message queue is closed.");
        }

        subscribers.add(subscriber);
    }

    @Override
    public synchronized void unsubscribe(IQueueSubscriber<TMessage> subscriber) {
        ValidationSupport.validateRequired("Queue subscriber", subscriber);

        subscribers.remove(subscriber);
    }

    @Override
    public synchronized void enqueue(TMessage message) {
        ValidationSupport.validateRequired("Message", message);

        if (closed) {
            close(message);
            return;
        }

        if (!hasSubscribers()) {
            close(message);
            return;
        }

        try {
            if (!canSend(message)) {
                close(message);
                return;
            }
        }
        catch (RuntimeException exception) {
            close(message);
            throw exception;
        }

        TMessage droppedMessage = null;

        if (!messages.offer(message)) {
            droppedMessage = messages.poll();
            if (!messages.offer(message)) {
                close(message);
                return;
            }
        }

        if (droppedMessage != null) {
            close(droppedMessage);
        }
    }

    @Override
    public void push(TMessage message) {
        enqueue(message);
    }

    @Override
    public TMessage dequeue() {
        TMessage message = null;

        try {
            message = messages.poll(timeoutMilliseconds, TimeUnit.MILLISECONDS);
        }
        catch (InterruptedException exception) {
            logger.warning("Message queue dequeue was interrupted.");
            Thread.currentThread().interrupt();
        }

        return message;
    }

    @Override
    public synchronized void close() {
        closed = true;
        TMessage message;

        while ((message = messages.poll()) != null) {
            close(message);
        }

        subscribers.clear();
    }

    private boolean hasSubscribers() {
        return !subscribers.isEmpty();
    }

    private boolean canSend(TMessage message) {
        for (IMessageFilter<TMessage> filter : filters) {
            if (!filter.canSend(message)) {
                return false;
            }
        }

        return true;
    }

    private void close(TMessage message) {
        if (message instanceof AutoCloseable closeable) {
            try {
                closeable.close();
            }
            catch (Exception exception) {
                throw new IllegalStateException("Could not close dropped message.", exception);
            }
        }
    }
}
