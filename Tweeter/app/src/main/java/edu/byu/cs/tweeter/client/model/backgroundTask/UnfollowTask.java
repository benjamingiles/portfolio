package edu.byu.cs.tweeter.client.model.backgroundTask;

import android.os.Handler;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.UnfollowRequest;
import edu.byu.cs.tweeter.model.net.response.UnfollowResponse;

/**
 * Background task that removes a following relationship between two users.
 */
public class UnfollowTask extends AuthenticatedTask {

    public static final String URL_PATH = "/unfollow";

    /**
     * The user that is being followed.
     */
    private final User followee;

    private final User currUser;

    public UnfollowTask(AuthToken authToken, User currUser, User followee, Handler messageHandler) {
        super(authToken, messageHandler);
        this.currUser = currUser;
        this.followee = followee;
    }

    @Override
    protected void runTask() {
        try {

            String currAlias = currUser == null ? null : currUser.getAlias();
            String followeeAlias = followee == null ? null : followee.getAlias();

            UnfollowRequest request = new UnfollowRequest(currAlias, followeeAlias, authToken);
            UnfollowResponse response = getServerFacade().unfollow(request, URL_PATH);

            if (response.isSuccess()) {
                sendSuccessMessage();
            } else {
                sendFailedMessage(response.getMessage());
            }

        } catch (Exception ex) {
            sendExceptionMessage(ex);
        }
    }


}
