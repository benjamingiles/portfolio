package edu.byu.cs.tweeter.client.presenter;

import edu.byu.cs.tweeter.client.model.service.StatusService;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;

public class StoryPresenter extends PagedPresenter<Status> {

    StatusService statusService;

    public StoryPresenter(PagedView<Status> view) {
        super(view);
        statusService = new StatusService();
    }

    @Override
    protected void load(User user, PagedPresenter<Status>.GetItemObserver getItemObserver) {
        statusService.loadStory(user, PAGE_SIZE, lastItem, getItemObserver);
    }

    @Override
    protected String getPrefix() {
        return "Failed to get story";
    }

}
