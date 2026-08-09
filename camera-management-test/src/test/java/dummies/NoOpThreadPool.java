package dummies;

import camera.threading.IThreadPool;
import camera.threading.ManagedThread;
import camera.threading.ThreadServiceType;

public class NoOpThreadPool implements IThreadPool {
    @Override
    public ManagedThread submit(ThreadServiceType serviceType, Runnable task) {
        return null;
    }

    @Override
    public void shutdown() {
    }
}
