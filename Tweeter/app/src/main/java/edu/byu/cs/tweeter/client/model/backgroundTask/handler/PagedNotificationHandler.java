package edu.byu.cs.tweeter.client.model.backgroundTask.handler;

import android.os.Bundle;

import java.util.List;

import edu.byu.cs.tweeter.client.model.backgroundTask.GetFeedTask;
import edu.byu.cs.tweeter.client.model.backgroundTask.PagedTask;
import edu.byu.cs.tweeter.client.model.backgroundTask.observer.PagedNotificationObserver;

public class PagedNotificationHandler<T> extends BackgroundTaskHandler<PagedNotificationObserver<T>> {

    public PagedNotificationHandler(PagedNotificationObserver<T> observer) {
        super(observer);
    }

    @Override
    protected void handleSuccess(Bundle data, PagedNotificationObserver<T> observer) {
        List<T> items = (List<T>) data.getSerializable(PagedTask.ITEMS_KEY);
        boolean hasMorePages = data.getBoolean(GetFeedTask.MORE_PAGES_KEY);
        observer.handleSuccess(items, hasMorePages);
    }
}
