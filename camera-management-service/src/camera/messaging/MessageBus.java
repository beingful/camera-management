package camera.messaging;

import camera.validation.ValidationSupport;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MessageBus implements AutoCloseable {
    private final Map<Integer, IMessageQueue> queues;

    public MessageBus() {
        queues = new HashMap<>();
    }

    public synchronized void addQueue(IMessageQueue queue) {
        ValidationSupport.validateRequired("Message queue", queue);

        if (queues.containsKey(queue.id())) {
            throw new IllegalStateException("Message queue already exists: " + queue.id());
        }

        queues.put(queue.id(), queue);
    }

    @Override
    public synchronized void close() {
        RuntimeException closeException = null;

        for (IMessageQueue queue : List.copyOf(queues.values())) {
            try {
                queue.close();
            }
            catch (RuntimeException exception) {
                if (closeException == null) {
                    closeException = new IllegalStateException("Could not close all message queues.");
                }

                closeException.addSuppressed(exception);
            }
        }

        queues.clear();

        if (closeException != null) {
            throw closeException;
        }
    }
}
