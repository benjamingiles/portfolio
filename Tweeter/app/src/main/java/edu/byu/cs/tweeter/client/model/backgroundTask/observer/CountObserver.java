package edu.byu.cs.tweeter.client.model.backgroundTask.observer;

public interface CountObserver extends ServiceObserver {
    void handleSuccess(int count);
}
