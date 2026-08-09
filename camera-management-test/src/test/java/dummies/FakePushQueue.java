package dummies;

import camera.messaging.IPushMessageQueue;
import camera.messaging.IQueueSubscriber;

public class FakePushQueue<T> implements IPushMessageQueue<T> {
    @Override
    public int id() {
        return 1;
    }

    @Override
    public void close() {
    }

    @Override
    public void subscribe(IQueueSubscriber<T> subscriber) {
    }

    @Override
    public void unsubscribe(IQueueSubscriber<T> subscriber) {
    }

    @Override
    public void enqueue(T message) {
    }
}
