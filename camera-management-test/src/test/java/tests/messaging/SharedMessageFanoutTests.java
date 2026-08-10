package tests.messaging;

import camera.messaging.IPullMessageQueue;
import camera.messaging.MessageQueueBuilder;
import camera.messaging.QueueConsumer;
import camera.messaging.SharedMessage;
import camera.messaging.SharedMessageFanout;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SharedMessageFanoutTests {
    @Test
    public void closesOriginalAndDequeuedClonesWithExplicitReleaser() {
        MessageQueueBuilder queueBuilder = new MessageQueueBuilder();
        IPullMessageQueue<SharedMessage<TestMessage>> firstQueue = createQueue(queueBuilder);
        IPullMessageQueue<SharedMessage<TestMessage>> secondQueue = createQueue(queueBuilder);
        List<TestMessage> releasedMessages = new ArrayList<>();
        SharedMessageFanout<TestMessage> fanout = new SharedMessageFanout<>(
                List.of(firstQueue, secondQueue),
                TestMessage::cloneMessage,
                releasedMessages::add);

        TestMessage original = new TestMessage(1);

        fanout.push(original);
        assertEquals(List.of(original), releasedMessages);

        firstQueue.dequeue().close();
        secondQueue.dequeue().close();

        assertEquals(List.of(original, new TestMessage(2), new TestMessage(2)), releasedMessages);
    }

    @Test
    public void closesDroppedCloneWithExplicitReleaser() {
        MessageQueueBuilder queueBuilder = new MessageQueueBuilder();
        IPullMessageQueue<SharedMessage<TestMessage>> queue = createQueue(queueBuilder);
        List<TestMessage> releasedMessages = new ArrayList<>();
        SharedMessageFanout<TestMessage> fanout = new SharedMessageFanout<>(
                List.of(queue),
                TestMessage::cloneMessage,
                releasedMessages::add);

        TestMessage firstOriginal = new TestMessage(1);
        TestMessage secondOriginal = new TestMessage(2);

        fanout.push(firstOriginal);
        fanout.push(secondOriginal);

        assertEquals(List.of(firstOriginal, new TestMessage(2), secondOriginal), releasedMessages);

        queue.dequeue().close();

        assertEquals(List.of(firstOriginal, new TestMessage(2), secondOriginal, new TestMessage(3)), releasedMessages);
    }

    private IPullMessageQueue<SharedMessage<TestMessage>> createQueue(MessageQueueBuilder queueBuilder) {
        IPullMessageQueue<SharedMessage<TestMessage>> queue = queueBuilder.<SharedMessage<TestMessage>>createPull()
                .withQueueCapacity(1)
                .withDequeueTimeout(0)
                .build();

        queue.subscribe(new QueueConsumer<>());

        return queue;
    }

    private record TestMessage(int id) {
        private TestMessage cloneMessage() {
            return new TestMessage(id + 1);
        }
    }
}
