package edu.byu.cs.tweeter.client.presenter;

import java.util.List;

import edu.byu.cs.tweeter.client.model.backgroundTask.observer.GetUserObserver;
import edu.byu.cs.tweeter.client.model.backgroundTask.observer.PagedNotificationObserver;
import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.model.domain.User;

public abstract class PagedPresenter<T> extends Presenter {

    protected static final int PAGE_SIZE = 10;

    public interface PagedView<U> extends View {
        void showUser(User user);

        void setLoadingFooter(boolean value);

        void addMoreItems(List<U> items);
    }

    protected T lastItem;

    protected boolean hasMorePages;

    protected boolean isLoading = false;

    private UserService userService;


    public PagedPresenter(PagedView<T> view) {
        this.view = view;
        userService = new UserService();
    }

    public void getUser(String userAlias) {
        userService.getUserTask(userAlias, new GetUsersObserver());
        view.displayMessage("Getting user's profile...");
    }

    public void loadMoreItems(User user) {
        if (!isLoading) {
            isLoading = true;
            ((PagedView<T>) view).setLoadingFooter(true);
            load(user, new GetItemObserver());
        }
    }

    protected abstract void load(User user, GetItemObserver getItemObserver);

    public boolean hasMorePages() {
        return hasMorePages;
    }

    public void setHasMorePages(boolean hasMorePages) {
        this.hasMorePages = hasMorePages;
    }

    public boolean isLoading() {
        return isLoading;
    }

    private void setLoading() {
        isLoading = false;
        ((PagedView<T>) view).setLoadingFooter(false);
    }

    private class GetUsersObserver implements GetUserObserver {

        @Override
        public void handleFailure(String message) {
            view.displayMessage("Failed to get user's profile: " + message);
        }

        @Override
        public void handleException(Exception ex) {
            view.displayMessage("Failed to get user's profile because of exception: " + ex.getMessage());
        }

        @Override
        public void handleSuccess(User user) {
            ((PagedView<T>) view).showUser(user);
        }
    }

    protected class GetItemObserver implements PagedNotificationObserver<T> {

        @Override
        public void handleSuccess(List<T> items, boolean hasMorePages) {
            setLoading();
            lastItem = (items.size() > 0) ? items.get(items.size() - 1) : null;
            setHasMorePages(hasMorePages);
            ((PagedView<T>) view).addMoreItems(items);
        }

        @Override
        public void handleFailure(String message) {
            setLoading();
            view.displayMessage(getPrefix() + ": " + message);
        }

        @Override
        public void handleException(Exception exception) {
            setLoading();
            view.displayMessage(getPrefix() + " because of exception: " + exception.getMessage());
        }
    }

    protected abstract String getPrefix();

}
