package camera.messaging;

import camera.threading.IThreadPool;
import camera.threading.ThreadServiceType;
import camera.validation.ValidationSupport;

import java.util.ArrayList;
import java.util.List;

public class PushMessageQueue<TMessage> implements IPushMessageQueue<TMessage> {
    private final int id;
    private final List<IQueueSubscriber<TMessage>> subscribers;
    private final List<IMessageFilter<TMessage>> filters;
    private final IThreadPool threadPool;
    private boolean closed;

    public PushMessageQueue(PushMessageQueueSettings<TMessage> settings) {
        ValidationSupport.validateRequired("Push message queue settings", settings);

        this.id = settings.id();
        this.subscribers = new ArrayList<>();
        this.filters = List.copyOf(settings.filters());
        this.threadPool = settings.threadPool();
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
    public void enqueue(TMessage message) {
        ValidationSupport.validateRequired("Message", message);

        List<IQueueSubscriber<TMessage>> currentSubscribers;

        synchronized (this) {
            if (closed) {
                close(message);
                return;
            }

            if (subscribers.isEmpty()) {
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

            currentSubscribers = List.copyOf(subscribers);
        }

        for (IQueueSubscriber<TMessage> subscriber : currentSubscribers) {
            if (threadPool == null) {
                subscriber.push(message);
            }
            else {
                threadPool.submit(ThreadServiceType.SurveillanceError, () -> subscriber.push(message));
            }
        }
    }

    @Override
    public synchronized void close() {
        closed = true;
        subscribers.clear();
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
