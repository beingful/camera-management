package dummies;

import camera.configuration.Configuration;
import camera.configuration.DataObserver;
import camera.configuration.IDataController;
import camera.models.CameraSet;
import camera.retry.Retry;
import camera.retry.RetryPolicy;
import camera.retry.RetryWaitStrategy;

public class FakeDataObserver extends DataObserver<CameraSet> {
    private boolean started;
    private boolean stopped;

    private IDataController controller;

    public FakeDataObserver(Configuration<CameraSet> configuration) {
        super(configuration, new NoOpThreadPool(), new Retry(new RetryPolicy(1, RetryWaitStrategy.Lineal, 1, 1)));
    }

    @Override
    public synchronized void startObserving(IDataController dataController) {
        this.controller = dataController;
        this.started = true;
    }

    @Override
    public synchronized void stopObserving() {
        this.stopped = true;
    }

    public void triggerChange() {
        controller.onDataChanged();
    }

    public boolean started() {
        return started;
    }

    public boolean stopped() {
        return stopped;
    }
}
