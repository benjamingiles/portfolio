package edu.byu.cs.tweeter.client.model.net;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.presenter.MainPresenter;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.TweeterRemoteException;
import edu.byu.cs.tweeter.model.net.request.LoginRequest;
import edu.byu.cs.tweeter.model.net.request.StoryRequest;
import edu.byu.cs.tweeter.model.net.response.AuthenticateResponse;
import edu.byu.cs.tweeter.model.net.response.StoryResponse;

public class PostStatusTest {
    private User currUser;
    private AuthToken currAuthToken;
    private MainPresenter spyMainPresenter;
    private MainPresenter.MainView mockMain;
    private CountDownLatch countDownLatch;
    private ServerFacade serverFacade;
    private Status post;

    @BeforeEach
    public void setup() {
        serverFacade = new ServerFacade();
        try {
            LoginRequest loginRequest = new LoginRequest("@kyle", "pass");
            AuthenticateResponse loginResponse = serverFacade.login(loginRequest, "/login");
            Cache.getInstance().setCurrUser(loginResponse.getUser());
            Cache.getInstance().setCurrUserAuthToken(loginResponse.getAuthToken());
            currUser = loginResponse.getUser();
            currAuthToken = loginResponse.getAuthToken();
            List<String> emptyList = new ArrayList<>();
            long time = 0;
            post = new Status("My new Post", loginResponse.getUser(), time, emptyList, emptyList);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }

        mockMain = Mockito.mock(MainPresenter.MainView.class);
        spyMainPresenter = Mockito.spy(new MainPresenter(mockMain));

        resetCountDownLatch();
    }

    private void resetCountDownLatch() {
        countDownLatch = new CountDownLatch(1);
    }

    private void awaitCountDownLatch() throws InterruptedException {
        countDownLatch.await();
        resetCountDownLatch();
    }

    @Test
    public void testPostStatus_validRequest_correctResponse() throws InterruptedException {
        Answer<Void> answer = new Answer<>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                countDownLatch.countDown();
                return null;
            }
        };

        Mockito.doAnswer(answer).when(mockMain).displayMessage(Mockito.anyString());
        spyMainPresenter.postStatus("My new Post");
        awaitCountDownLatch();

        Mockito.verify(mockMain).displayMessage("Successfully Posted!");
        StoryRequest request = new StoryRequest(currAuthToken, currUser.getAlias(), 10, null);
        try {
            StoryResponse response = serverFacade.getStory(request, "/getstory");
            Assertions.assertEquals(response.getStory().get(0).getPost(), post.getPost());
            Assertions.assertEquals(response.getStory().get(0).getUser(), post.getUser());
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (TweeterRemoteException e) {
            throw new RuntimeException(e);
        }
    }
}
