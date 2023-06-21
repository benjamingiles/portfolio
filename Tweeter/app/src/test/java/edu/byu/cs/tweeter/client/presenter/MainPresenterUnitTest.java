package edu.byu.cs.tweeter.client.presenter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import edu.byu.cs.tweeter.client.model.service.StatusService;

public class MainPresenterUnitTest {
    private MainPresenter.MainView mockView;
    private StatusService mockStatusService;
    private MainPresenter mainPresenterSpy;

    @BeforeEach
    public void setup() {
        mockView = Mockito.mock(MainPresenter.MainView.class);
        mockStatusService = Mockito.mock(StatusService.class);

        mainPresenterSpy = Mockito.spy(new MainPresenter(mockView));
        //Mockito.doReturn(mockStatusService).when(mainPresenterSpy).getStatusService();
        Mockito.when(mainPresenterSpy.getStatusService()).thenReturn(mockStatusService);
    }

    @Test
    public void testPost_postSuccessful() {
        Answer<Void> answer = new Answer<>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                MainPresenter.PostStatusObserver observer = invocation.getArgument(1, MainPresenter.PostStatusObserver.class);
                observer.handleSuccess();
                return null;
            }
        };

        verifyPost(answer, "Successfully Posted!");
    }

    @Test
    public void testPost_postFailedWithMessage() {
        Answer<Void> answer = new Answer<>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                MainPresenter.PostStatusObserver observer = invocation.getArgument(1, MainPresenter.PostStatusObserver.class);
                observer.handleFailure("error message");
                return null;
            }
        };

        verifyPost(answer, "Failed to post status: error message");
    }

    @Test
    public void testPost_postFailedWithException() {
        Answer<Void> answer = new Answer<>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                MainPresenter.PostStatusObserver observer = invocation.getArgument(1, MainPresenter.PostStatusObserver.class);
                observer.handleException(new Exception("the exception message"));
                return null;
            }
        };

        verifyPost(answer, "Failed to post status because of exception: the exception message");
    }

    private void verifyPost(Answer<Void> answer, String message) {
        Mockito.doAnswer(answer).when(mockStatusService).postStatus(Mockito.anyString(), Mockito.any());
        String post = "Test Post";
        mainPresenterSpy.postStatus(post);
        Mockito.verify(mockView).setPostToast("Posting Status...");
        Mockito.verify(mockView).cancelPostToast();
        Mockito.verify(mockView).displayMessage(message);
    }
}
