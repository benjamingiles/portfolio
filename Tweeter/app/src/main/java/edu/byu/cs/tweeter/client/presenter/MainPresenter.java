package edu.byu.cs.tweeter.client.presenter;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.backgroundTask.observer.CountObserver;
import edu.byu.cs.tweeter.client.model.backgroundTask.observer.IsFollowerObserver;
import edu.byu.cs.tweeter.client.model.backgroundTask.observer.SimpleNotificationObserver;
import edu.byu.cs.tweeter.client.model.service.FollowService;
import edu.byu.cs.tweeter.client.model.service.LoginService;
import edu.byu.cs.tweeter.client.model.service.StatusService;
import edu.byu.cs.tweeter.model.domain.User;

public class MainPresenter extends Presenter {

    public interface MainView extends View {

        void followButtonVisibility(boolean value);

        void setFollowButton(boolean following);

        void enableFollowButton();

        void setFollowerCount(int count);

        void setFolloweeCount(int count);

        void setLogoutToast(String message);

        void logout();

        void setPostToast(String message);

        void cancelPostToast();
    }

    private FollowService followService;
    private LoginService loginService;
    private StatusService statusService;

    private User selectedUser;

    public MainPresenter(MainView view) {
        this.view = view;
        followService = new FollowService();
        loginService = new LoginService();
        selectedUser = null;
    }

    protected StatusService getStatusService() {
        if (statusService == null) {
            statusService = new StatusService();
        }

        return  statusService;
    }

    public void showFollowing(User selectedUser) {
        if (selectedUser.compareTo(Cache.getInstance().getCurrUser()) == 0) {
            ((MainView)view).followButtonVisibility(false);
        } else {
            ((MainView)view).followButtonVisibility(true);
            followService.setFollowButton(selectedUser, new GetIsFollowerObserver());
        }
    }

    public void unfollow(User selectedUser) {
        this.selectedUser = selectedUser;
        followService.unfollow(selectedUser, new GetUnfollowObserver());
        view.displayMessage("Removing " + selectedUser.getName() + "...");
        ((MainView)view).enableFollowButton();
    }

    public void updateSelectedUserFollowingAndFollowers(User selectedUser) {
        followService.updateSelectedUserFollowingAndFollowers(selectedUser, new GetFollowersCountObserver(),
                new GetFollowingCountObserver());
    }

    public void follow(User selectedUser) {
        this.selectedUser = selectedUser;
        followService.follow(selectedUser, new GetFollowObserver());
        view.displayMessage("Adding " + selectedUser.getName() + "...");
        ((MainView)view).enableFollowButton();
    }

    public void logout() {
        ((MainView)view).setLogoutToast("Logging Out...");
        loginService.logout(new GetLogoutObserver());
    }

    public void postStatus(String post) {
        ((MainView)view).setPostToast("Posting Status...");
        getStatusService().postStatus(post, new PostStatusObserver());
    }

    private class GetFollowingCountObserver implements CountObserver {

        @Override
        public void handleSuccess(int count) {
            ((MainView)view).setFolloweeCount(count);
        }

        @Override
        public void handleFailure(String message) {
            view.displayMessage("Failed to get following count: " + message);
        }

        @Override
        public void handleException(Exception exception) {
            view.displayMessage("Failed to get following count because of exception: " + exception.getMessage());
        }
    }

    private class GetFollowersCountObserver implements CountObserver {

        @Override
        public void handleSuccess(int count) {
            ((MainView)view).setFollowerCount(count);
        }

        @Override
        public void handleFailure(String message) {
            view.displayMessage("Failed to get followers count: " + message);
        }

        @Override
        public void handleException(Exception exception) {
            view.displayMessage("Failed to get followers count because of exception: " + exception.getMessage());
        }
    }

    private class GetIsFollowerObserver implements IsFollowerObserver {

        @Override
        public void handleSuccess(boolean isFollower) {
            // If logged in user if a follower of the selected user, display the follow button as "following"
            ((MainView)view).setFollowButton(isFollower);
        }

        @Override
        public void handleFailure(String message) {
            view.displayMessage("Failed to determine following relationship: " + message);
        }

        @Override
        public void handleException(Exception exception) {
            view.displayMessage("Failed to determine following relationship because of exception: " + exception.getMessage());
        }
    }

    private class GetFollowObserver implements SimpleNotificationObserver {

        @Override
        public void handleSuccess() {
            followService.updateSelectedUserFollowingAndFollowers(selectedUser, new GetFollowersCountObserver(),
                    new GetFollowingCountObserver());
            ((MainView)view).setFollowButton(true);
        }

        @Override
        public void handleFailure(String message) {
            view.displayMessage("Failed to follow: " + message);
        }

        @Override
        public void handleException(Exception exception) {
            view.displayMessage("Failed to follow because of exception: " + exception.getMessage());
        }
    }

    private class GetUnfollowObserver implements SimpleNotificationObserver {

        @Override
        public void handleSuccess() {
            followService.updateSelectedUserFollowingAndFollowers(selectedUser, new GetFollowersCountObserver(),
                    new GetFollowingCountObserver());
            ((MainView)view).setFollowButton(false);
        }

        @Override
        public void handleFailure(String message) {
            view.displayMessage("Failed to unfollow: " + message);
        }

        @Override
        public void handleException(Exception exception) {
            view.displayMessage("Failed to unfollow because of exception: " + exception.getMessage());
        }
    }

    private class GetLogoutObserver implements SimpleNotificationObserver {

        @Override
        public void handleSuccess() {
            ((MainView)view).logout();
        }

        @Override
        public void handleFailure(String message) {
            view.displayMessage("Failed to logout: " + message);
        }

        @Override
        public void handleException(Exception exception) {
            view.displayMessage("Failed to logout because of exception: " + exception.getMessage());
        }
    }

    protected class PostStatusObserver implements SimpleNotificationObserver {

        @Override
        public void handleSuccess() {
            ((MainView)view).cancelPostToast();
            view.displayMessage("Successfully Posted!");
        }

        @Override
        public void handleFailure(String message) {
            ((MainView)view).cancelPostToast();
            view.displayMessage("Failed to post status: " + message);
        }

        @Override
        public void handleException(Exception exception) {
            ((MainView)view).cancelPostToast();
            view.displayMessage("Failed to post status because of exception: " + exception.getMessage());
        }
    }
}
