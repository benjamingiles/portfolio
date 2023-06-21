package edu.byu.cs.tweeter.client.presenter;

import edu.byu.cs.tweeter.client.model.service.StatusService;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;

public class FeedPresenter extends PagedPresenter<Status> {

    StatusService statusService;

    public FeedPresenter(PagedView<Status> view) {
        super(view);
        statusService = new StatusService();
    }

    @Override
    protected void load(User user, PagedPresenter<Status>.GetItemObserver getItemObserver) {
        statusService.loadFeed(user, PAGE_SIZE, lastItem, getItemObserver);
    }

    @Override
    protected String getPrefix() {
        return "Failed to get feed";
    }

}
