package dummies;

import camera.messaging.MessageBus;
import camera.models.CameraSet;
import camera.runtime.CameraSetRuntime;
import helpers.CameraIds;

import java.util.ArrayList;
import java.util.List;

public class FakeCameraSetRuntime extends CameraSetRuntime {
    private final List<Integer> connectedIds = new ArrayList<>();
    private final List<Integer> disconnectedIds = new ArrayList<>();
    private boolean disconnectedAll;
    private boolean systemQueuesClosed;

    public FakeCameraSetRuntime() {
        super(DummyRuntimeFactory.create(), new MessageBus());
    }

    @Override
    public synchronized void connect(CameraSet cameraSet) {
        connectedIds.addAll(CameraIds.from(cameraSet));
    }

    @Override
    public synchronized void disconnect(CameraSet cameraSet) {
        disconnectedIds.addAll(CameraIds.from(cameraSet));
    }

    @Override
    public synchronized void disconnect(int cameraId) {
        disconnectedIds.add(cameraId);
    }

    @Override
    public synchronized void disconnectAll() {
        disconnectedAll = true;
    }

    @Override
    public synchronized void closeSystemQueues() {
        systemQueuesClosed = true;
    }

    public void clear() {
        connectedIds.clear();
        disconnectedIds.clear();
        disconnectedAll = false;
        systemQueuesClosed = false;
    }

    public List<Integer> connectedIds() {
        return List.copyOf(connectedIds);
    }

    public List<Integer> disconnectedIds() {
        return List.copyOf(disconnectedIds);
    }

    public boolean disconnectedAll() {
        return disconnectedAll;
    }

    public boolean systemQueuesClosed() {
        return systemQueuesClosed;
    }
}
