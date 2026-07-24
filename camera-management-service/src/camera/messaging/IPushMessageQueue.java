package camera.messaging;

public interface IPushMessageQueue<TMessage> extends IMessageQueue {
    void subscribe(IQueueSubscriber<TMessage> subscriber);

    void unsubscribe(IQueueSubscriber<TMessage> subscriber);

    void enqueue(TMessage message);
}
