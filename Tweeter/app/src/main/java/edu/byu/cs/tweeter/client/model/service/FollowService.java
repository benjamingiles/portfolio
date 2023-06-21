package edu.byu.cs.tweeter.client.model.service;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.backgroundTask.FollowTask;
import edu.byu.cs.tweeter.client.model.backgroundTask.GetFollowersCountTask;
import edu.byu.cs.tweeter.client.model.backgroundTask.GetFollowersTask;
import edu.byu.cs.tweeter.client.model.backgroundTask.GetFollowingCountTask;
import edu.byu.cs.tweeter.client.model.backgroundTask.GetFollowingTask;
import edu.byu.cs.tweeter.client.model.backgroundTask.IsFollowerTask;
import edu.byu.cs.tweeter.client.model.backgroundTask.UnfollowTask;
import edu.byu.cs.tweeter.client.model.backgroundTask.handler.CountNotificationHandler;
import edu.byu.cs.tweeter.client.model.backgroundTask.handler.IsFollowerHandler;
import edu.byu.cs.tweeter.client.model.backgroundTask.handler.PagedNotificationHandler;
import edu.byu.cs.tweeter.client.model.backgroundTask.handler.SimpleNotificationHandler;
import edu.byu.cs.tweeter.client.model.backgroundTask.observer.CountObserver;
import edu.byu.cs.tweeter.client.model.backgroundTask.observer.IsFollowerObserver;
import edu.byu.cs.tweeter.client.model.backgroundTask.observer.PagedNotificationObserver;
import edu.byu.cs.tweeter.client.model.backgroundTask.observer.SimpleNotificationObserver;
import edu.byu.cs.tweeter.model.domain.User;

public class FollowService {

    public void loadMoreFollowees(User user, int pageSize, User lastFollowee, PagedNotificationObserver<User> observer) {
        GetFollowingTask getFollowingTask = new GetFollowingTask(Cache.getInstance().getCurrUserAuthToken(),
                user, pageSize, lastFollowee, new PagedNotificationHandler<User>(observer));
        Executor.execute(getFollowingTask);
    }

    public void loadMoreFollowers(User user, int pageSize, User lastFollower, PagedNotificationObserver<User> observer) {
        GetFollowersTask getFollowersTask = new GetFollowersTask(Cache.getInstance().getCurrUserAuthToken(),
                user, pageSize, lastFollower, new PagedNotificationHandler<User>(observer));
        Executor.execute(getFollowersTask);
    }

    public void setFollowButton(User selectedUser, IsFollowerObserver observer) {
        IsFollowerTask isFollowerTask = new IsFollowerTask(Cache.getInstance().getCurrUserAuthToken(),
                Cache.getInstance().getCurrUser(), selectedUser, new IsFollowerHandler(observer));
        Executor.execute(isFollowerTask);
    }

    public void unfollow(User selectedUser, SimpleNotificationObserver observer) {
        UnfollowTask unfollowTask = new UnfollowTask(Cache.getInstance().getCurrUserAuthToken(),
                Cache.getInstance().getCurrUser(), selectedUser, new SimpleNotificationHandler(observer));
        Executor.execute(unfollowTask);
    }

    public void follow(User selectedUser, SimpleNotificationObserver observer) {
        FollowTask followTask = new FollowTask(Cache.getInstance().getCurrUserAuthToken(),
                Cache.getInstance().getCurrUser(), selectedUser, new SimpleNotificationHandler(observer));
        Executor.execute(followTask);
    }

    public void updateSelectedUserFollowingAndFollowers(User selectedUser, CountObserver
            followersCountObserver, CountObserver followingCountObserver) {
        ExecutorService executor = Executors.newFixedThreadPool(2);

        // Get count of most recently selected user's followers.
        GetFollowersCountTask followersCountTask = new GetFollowersCountTask(Cache.getInstance().getCurrUserAuthToken(),
                selectedUser, new CountNotificationHandler(followersCountObserver));
        executor.execute(followersCountTask);

        // Get count of most recently selected user's followees (who they are following)
        GetFollowingCountTask followingCountTask = new GetFollowingCountTask(Cache.getInstance().getCurrUserAuthToken(),
                selectedUser, new CountNotificationHandler(followingCountObserver));
        executor.execute(followingCountTask);
    }

}

