package edu.byu.cs.tweeter.client.model.backgroundTask;

import android.os.Handler;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.FollowRequest;
import edu.byu.cs.tweeter.model.net.response.FollowResponse;

/**
 * Background task that establishes a following relationship between two users.
 */
public class FollowTask extends AuthenticatedTask {

    public static final String URL_PATH = "/follow";

    /**
     * The user that is being followed.
     */
    private final User followee;

    private final User currUser;

    public FollowTask(AuthToken authToken, User currUser, User followee, Handler messageHandler) {
        super(authToken, messageHandler);
        this.currUser = currUser;
        this.followee = followee;
    }

    @Override
    protected void runTask() {
        try {

            FollowRequest request = new FollowRequest(currUser, followee, authToken);
            FollowResponse response = getServerFacade().follow(request, URL_PATH);

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
