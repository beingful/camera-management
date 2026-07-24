package camera.messaging;

import camera.validation.ValidationSupport;
import jakarta.validation.constraints.Positive;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class SharedMessage<TMessage> implements AutoCloseable {
    private final TMessage message;
    private final AtomicInteger references;
    private final AtomicBoolean released;

    public SharedMessage(TMessage message, int references) {
        ValidationSupport.validateRequired("Message", message);
        ValidationSupport.validateValue(SharedMessageSettings.class, "references", references);

        this.message = message;
        this.references = new AtomicInteger(references);
        this.released = new AtomicBoolean(false);
    }

    public TMessage message() {
        if (released.get()) {
            throw new IllegalStateException("Message has already been released.");
        }

        return message;
    }

    @Override
    public void close() {
        int remainingReferences = references.decrementAndGet();

        if (remainingReferences == 0 && released.compareAndSet(false, true)) {
            closeMessage();
        }

        if (remainingReferences < 0) {
            throw new IllegalStateException("Message was released too many times.");
        }
    }

    private void closeMessage() {
        if (message instanceof AutoCloseable closeable) {
            try {
                closeable.close();
            }
            catch (Exception exception) {
                throw new IllegalStateException("Could not close shared message.", exception);
            }
        }
    }

    private record SharedMessageSettings(@Positive int references) {
    }
}
