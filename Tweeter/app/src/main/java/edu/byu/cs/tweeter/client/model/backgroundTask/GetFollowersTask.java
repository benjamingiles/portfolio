package edu.byu.cs.tweeter.client.model.backgroundTask;

import android.os.Handler;

import java.io.IOException;
import java.util.List;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.TweeterRemoteException;
import edu.byu.cs.tweeter.model.net.request.FollowersRequest;
import edu.byu.cs.tweeter.model.net.response.FollowersResponse;
import edu.byu.cs.tweeter.util.Pair;

/**
 * Background task that retrieves a page of followers.
 */
public class GetFollowersTask extends PagedUserTask {

    public static final String URL_PATH = "/getfollowers";

    public GetFollowersTask(AuthToken authToken, User targetUser, int limit, User lastFollower,
                            Handler messageHandler) {
        super(authToken, targetUser, limit, lastFollower, messageHandler);
    }

    @Override
    protected void runTask() throws IOException {
        try {
            String targetUserAlias = targetUser == null ? null : targetUser.getAlias();
            String lastFollowerAlias = lastItem == null ? null : lastItem.getAlias();

            FollowersRequest request = new FollowersRequest(authToken, targetUserAlias, limit, lastFollowerAlias);
            FollowersResponse response = getServerFacade().getFollowers(request, URL_PATH);

            if (response.isSuccess()) {
                this.items = response.getFollowers();
                this.hasMorePages = response.getHasMorePages();
                sendSuccessMessage();
            } else {
                sendFailedMessage(response.getMessage());
            }
        } catch (IOException | TweeterRemoteException ex) {
            sendExceptionMessage(ex);
        }
    }

    @Override
    protected Pair<List<User>, Boolean> getItems() {
        return getFakeData().getPageOfUsers(getLastItem(), getLimit(), getTargetUser());
    }
}
