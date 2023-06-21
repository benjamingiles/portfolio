package edu.byu.cs.tweeter.client.model.service;


import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.backgroundTask.GetUserTask;
import edu.byu.cs.tweeter.client.model.backgroundTask.handler.GetUserHandler;
import edu.byu.cs.tweeter.client.model.backgroundTask.observer.GetUserObserver;

public class UserService {

    public void getUserTask(String user, GetUserObserver observer) {
        GetUserTask getUserTask = new GetUserTask(Cache.getInstance().getCurrUserAuthToken(),
                user, new GetUserHandler(observer));
        Executor.execute(getUserTask);
    }

}
