package edu.byu.cs.tweeter.model.net.request;

import edu.byu.cs.tweeter.model.domain.AuthToken;

public class UnfollowRequest {

    private String currUser;
    private String followee;
    private AuthToken authToken;

    private UnfollowRequest() {}

    public UnfollowRequest(String currUser, String followee, AuthToken authToken) {
        this.currUser = currUser;
        this.followee = followee;
        this.authToken = authToken;
    }

    public String getCurrUser() {
        return currUser;
    }

    public void setCurrUser(String currUser) {
        this.currUser = currUser;
    }

    public String getFollowee() {
        return followee;
    }

    public void setFollowee(String followee) {
        this.followee = followee;
    }

    public AuthToken getAuthToken() {
        return authToken;
    }

    public void setAuthToken(AuthToken authToken) {
        this.authToken = authToken;
    }
}
