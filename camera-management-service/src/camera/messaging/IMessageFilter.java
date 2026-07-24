package camera.messaging;

public interface IMessageFilter<TMessage> {
    boolean canSend(TMessage message);
}
