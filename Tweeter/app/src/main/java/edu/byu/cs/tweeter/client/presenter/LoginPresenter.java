package edu.byu.cs.tweeter.client.presenter;

import android.widget.EditText;

public class LoginPresenter extends AuthenticatePresenter {

    public LoginPresenter(AuthenticateView view) {
        super(view);
    }

    @Override
    protected String getPrefix() {
        return "Failed to login";
    }

    public void login(EditText alias, EditText password) {
        // Login and move to MainActivity.
        try {
            validateLogin(alias, password);
            ((AuthenticateView)view).setErrorText(null);
            ((AuthenticateView)view).setToast("Logging In...");
            loginService.login(alias, password, new GetAuthenticateObserver());
        } catch (Exception e) {
            ((AuthenticateView)view).setErrorText(e.getMessage());
        }
    }

    public void validateLogin(EditText alias, EditText password) {
        if (alias.getText().length() > 0 && alias.getText().charAt(0) != '@') {
            throw new IllegalArgumentException("Alias must begin with @.");
        }
        if (alias.getText().length() < 2) {
            throw new IllegalArgumentException("Alias must contain 1 or more characters after the @.");
        }
        if (password.getText().length() == 0) {
            throw new IllegalArgumentException("Password cannot be empty.");
        }
    }
}
