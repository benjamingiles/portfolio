package edu.byu.cs.tweeter.client.model.net;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.TweeterRemoteException;
import edu.byu.cs.tweeter.model.net.request.FollowersRequest;
import edu.byu.cs.tweeter.model.net.request.GetCountRequest;
import edu.byu.cs.tweeter.model.net.request.RegisterRequest;
import edu.byu.cs.tweeter.model.net.response.AuthenticateResponse;
import edu.byu.cs.tweeter.model.net.response.FollowersResponse;
import edu.byu.cs.tweeter.model.net.response.GetCountResponse;
import edu.byu.cs.tweeter.util.FakeData;

public class ServerFacadeTest {
    private ServerFacade serverFacade;

    @BeforeEach
    public void setup() {
        serverFacade = new ServerFacade();
    }

    @Test
    public void registerSuccessTest() throws IOException, TweeterRemoteException {
        RegisterRequest request = new RegisterRequest("@fake", "pass", "firstName", "lastName", "image");
        AuthenticateResponse response = serverFacade.register(request, "/register");
        AuthenticateResponse expectedResponse = new AuthenticateResponse(FakeData.getInstance().getFirstUser(),
                FakeData.getInstance().getAuthToken());
        Assertions.assertTrue(response.isSuccess());
        Assertions.assertEquals(expectedResponse.getUser(), response.getUser());
    }

    @Test
    public void registerFailureTest() {
        RegisterRequest request = new RegisterRequest(null, "pass", "firstName", "lastName", "image");
        Assertions.assertThrows(TweeterRequestException.class, () -> serverFacade.register(request, "/register"));
    }

    @Test
    public void getFollowersSuccessTest() throws IOException, TweeterRemoteException {
        FollowersRequest request = new FollowersRequest(new AuthToken(), "alias", 1, "last");
        FollowersResponse response = serverFacade.getFollowers(request, "/getfollowers");
        FollowersResponse expectedResponse = new FollowersResponse(FakeData.getInstance().getFakeUsers(), false);
        Assertions.assertTrue(response.isSuccess());
        Assertions.assertEquals(expectedResponse.getFollowers().get(0), response.getFollowers().get(0));
    }

    @Test
    public void getFollowersFailureTest() {
        FollowersRequest request = new FollowersRequest(null, null, 1, null);
        Assertions.assertThrows(TweeterRequestException.class, () -> serverFacade.getFollowers(request, "/getfollowers"));
    }

    @Test
    public void getFollowersCountSuccessTest() throws IOException, TweeterRemoteException {
        GetCountRequest request = new GetCountRequest(new User("Firstname", "lastName", "photo"), null);
        GetCountResponse response = serverFacade.getCount(request, "/getfollowerscount");
        GetCountResponse expectedResponse = new GetCountResponse(20);
        Assertions.assertTrue(response.isSuccess());
        Assertions.assertEquals(expectedResponse.getCount(), response.getCount());
    }

    @Test
    public void getFollowersCountFailureTest() {
        GetCountRequest request = new GetCountRequest(null, null);
        Assertions.assertThrows(TweeterRequestException.class, () -> serverFacade.getCount(request, "/getfollowerscount"));
    }
}
