package camera.messaging;

public interface IPullMessageQueue<TMessage> extends IMessageQueue, IQueueSubscriber<TMessage> {
    void subscribe(IQueueSubscriber<TMessage> subscriber);

    void unsubscribe(IQueueSubscriber<TMessage> subscriber);

    void enqueue(TMessage message);

    TMessage dequeue();
}
