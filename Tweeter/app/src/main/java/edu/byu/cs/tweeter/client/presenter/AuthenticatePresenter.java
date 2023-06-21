package edu.byu.cs.tweeter.client.presenter;

import edu.byu.cs.tweeter.client.model.backgroundTask.observer.AuthenticateObserver;
import edu.byu.cs.tweeter.client.model.service.LoginService;
import edu.byu.cs.tweeter.model.domain.User;

public abstract class AuthenticatePresenter extends Presenter {

    public interface AuthenticateView extends View {
        void setErrorText(String message);

        void changeToMain(User loggedInUser, String message);

        void setToast(String message);
    }

    protected LoginService loginService;

    public AuthenticatePresenter(AuthenticateView view) {
        this.view = view;
        loginService = new LoginService();
    }

    protected class GetAuthenticateObserver implements AuthenticateObserver {

        @Override
        public void handleSuccess(User loggedInUser, String message) {
            ((AuthenticateView)view).changeToMain(loggedInUser, message);
        }

        @Override
        public void handleFailure(String message) {
            view.displayMessage(getPrefix() + ": " + message);
        }

        @Override
        public void handleException(Exception exception) {
            view.displayMessage(getPrefix() + " because of exception: " + exception.getMessage());
        }
    }

    protected abstract String getPrefix();

}
