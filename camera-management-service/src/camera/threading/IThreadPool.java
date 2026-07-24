package camera.threading;

public interface IThreadPool {
    ManagedThread submit(ThreadServiceType serviceType, Runnable task);

    void shutdown();
}
