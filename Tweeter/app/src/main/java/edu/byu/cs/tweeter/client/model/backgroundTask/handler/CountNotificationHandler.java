package edu.byu.cs.tweeter.client.model.backgroundTask.handler;

import android.os.Bundle;

import edu.byu.cs.tweeter.client.model.backgroundTask.GetCountTask;
import edu.byu.cs.tweeter.client.model.backgroundTask.observer.CountObserver;

public class CountNotificationHandler extends BackgroundTaskHandler<CountObserver>{

    public CountNotificationHandler(CountObserver observer) {
        super(observer);
    }

    @Override
    protected void handleSuccess(Bundle data, CountObserver observer) {
        int count = data.getInt(GetCountTask.COUNT_KEY);
        observer.handleSuccess(count);
    }
}
