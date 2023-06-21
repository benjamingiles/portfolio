package edu.byu.cs.tweeter.client.model.backgroundTask;

import android.os.Handler;

import java.io.IOException;

import edu.byu.cs.tweeter.model.net.request.LoginRequest;
import edu.byu.cs.tweeter.model.net.response.AuthenticateResponse;

/**
 * Background task that logs in a user (i.e., starts a session).
 */
public class LoginTask extends AuthenticateTask {

    public static final String URL_PATH = "/login";

    public LoginTask(String username, String password, Handler messageHandler) {
        super(messageHandler, username, password);
    }

    @Override
    protected final void runTask() throws IOException {

        try {
            LoginRequest request = new LoginRequest(username, password);
            AuthenticateResponse response = getServerFacade().login(request, URL_PATH);

            if (response.isSuccess()) {
                this.authenticatedUser = response.getUser();
                this.authToken = response.getAuthToken();
                sendSuccessMessage();
            } else {
                sendFailedMessage(response.getMessage());
            }

        } catch (Exception ex) {
            sendExceptionMessage(ex);
        }
    }
}
