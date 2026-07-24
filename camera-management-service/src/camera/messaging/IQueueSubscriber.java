package camera.messaging;

public interface IQueueSubscriber<TMessage> {
    void push(TMessage message);
}
