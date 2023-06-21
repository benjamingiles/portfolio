package edu.byu.cs.tweeter.client.model.backgroundTask.observer;

public interface IsFollowerObserver extends ServiceObserver {
    void handleSuccess(boolean isFollower);
}
