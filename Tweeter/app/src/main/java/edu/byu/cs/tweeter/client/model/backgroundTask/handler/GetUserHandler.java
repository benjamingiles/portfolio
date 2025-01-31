package edu.byu.cs.tweeter.client.model.backgroundTask.handler;

import android.os.Bundle;

import edu.byu.cs.tweeter.client.model.backgroundTask.GetUserTask;
import edu.byu.cs.tweeter.client.model.backgroundTask.observer.GetUserObserver;
import edu.byu.cs.tweeter.model.domain.User;

/**
 * Message handler (i.e., observer) for GetUserTask.
 */
public class GetUserHandler extends BackgroundTaskHandler<GetUserObserver> {


    public GetUserHandler(GetUserObserver observer) {
        super(observer);
    }

    @Override
    protected void handleSuccess(Bundle data, GetUserObserver observer) {
        User user = (User) data.getSerializable(GetUserTask.USER_KEY);
        observer.handleSuccess(user);
    }
}
