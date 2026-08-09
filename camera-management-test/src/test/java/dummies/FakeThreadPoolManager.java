package dummies;

import camera.models.Camera;
import camera.threading.ThreadPoolManager;
import helpers.CameraIds;

import java.util.ArrayList;
import java.util.List;

public class FakeThreadPoolManager extends ThreadPoolManager {
    private final List<Integer> createdIds = new ArrayList<>();
    private final List<Integer> shutdownIds = new ArrayList<>();
    private boolean shutdownAll;

    public FakeThreadPoolManager() {
        super(new FakePushQueue<>());
    }

    @Override
    public synchronized void createCameraThreadPools(List<Camera> cameras) {
        createdIds.addAll(CameraIds.from(cameras));
    }

    @Override
    public synchronized void shutdownCameraThreadPools(List<Camera> cameras) {
        shutdownIds.addAll(CameraIds.from(cameras));
    }

    @Override
    public synchronized void shutdownCameraThreadPool(int cameraId) {
        shutdownIds.add(cameraId);
    }

    @Override
    public synchronized void shutdown() {
        shutdownAll = true;
    }

    public void clear() {
        createdIds.clear();
        shutdownIds.clear();
        shutdownAll = false;
    }

    public List<Integer> createdIds() {
        return List.copyOf(createdIds);
    }

    public List<Integer> shutdownIds() {
        return List.copyOf(shutdownIds);
    }

    public boolean shutdownAll() {
        return shutdownAll;
    }
}
