package edu.byu.cs.tweeter.client.model.backgroundTask.handler;

import android.os.Bundle;

import edu.byu.cs.tweeter.client.model.backgroundTask.IsFollowerTask;
import edu.byu.cs.tweeter.client.model.backgroundTask.observer.IsFollowerObserver;

public class IsFollowerHandler extends BackgroundTaskHandler<IsFollowerObserver> {

    public IsFollowerHandler(IsFollowerObserver observer) {
        super(observer);
    }

    @Override
    protected void handleSuccess(Bundle data, IsFollowerObserver observer) {
        boolean isFollower = data.getBoolean(IsFollowerTask.IS_FOLLOWER_KEY);
        observer.handleSuccess(isFollower);
    }
}
