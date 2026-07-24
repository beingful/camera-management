package camera.messaging;

public interface IMessageQueue extends AutoCloseable {
    int id();

    @Override
    void close();
}
