package edu.byu.cs.tweeter.client.model.backgroundTask.observer;

import edu.byu.cs.tweeter.model.domain.User;

public interface GetUserObserver extends ServiceObserver {
    void handleSuccess(User user);
}
