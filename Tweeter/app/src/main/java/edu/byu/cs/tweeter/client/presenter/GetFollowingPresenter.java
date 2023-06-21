package edu.byu.cs.tweeter.client.presenter;

import edu.byu.cs.tweeter.client.model.service.FollowService;
import edu.byu.cs.tweeter.model.domain.User;

public class GetFollowingPresenter extends PagedPresenter<User> {
    private FollowService followService;

    public GetFollowingPresenter(PagedView<User> view) {
        super(view);
        followService = new FollowService();
    }

    @Override
    protected void load(User user, PagedPresenter<User>.GetItemObserver getItemObserver) {
        followService.loadMoreFollowees(user, PAGE_SIZE, lastItem, getItemObserver);
    }

    @Override
    protected String getPrefix() {
        return "Failed to get following";
    }
}
