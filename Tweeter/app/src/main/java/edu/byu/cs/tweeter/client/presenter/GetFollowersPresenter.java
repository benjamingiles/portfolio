package edu.byu.cs.tweeter.client.presenter;

import edu.byu.cs.tweeter.client.model.service.FollowService;
import edu.byu.cs.tweeter.model.domain.User;

public class GetFollowersPresenter extends PagedPresenter<User> {

    private FollowService followService;

    public GetFollowersPresenter(PagedView<User> view) {
        super(view);
        this.followService = new FollowService();
    }

    @Override
    protected String getPrefix() {
        return "Failed to get followers";
    }

    @Override
    protected void load(User user, PagedPresenter<User>.GetItemObserver getItemObserver) {
        followService.loadMoreFollowers(user, PAGE_SIZE, lastItem, getItemObserver);
    }
}
