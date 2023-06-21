package edu.byu.cs.tweeter.client.presenter;

import android.widget.EditText;
import android.widget.ImageView;

public class RegisterPresenter extends AuthenticatePresenter {


    public RegisterPresenter(AuthenticateView view) {
        super(view);
    }

    @Override
    protected String getPrefix() {
        return "Failed to register";
    }

    public void register(EditText firstName, EditText lastName, EditText alias,
                         EditText password, ImageView imageToUpload) {
        // Register and move to MainActivity.
        try {
            validateRegistration(firstName, lastName, alias, password, imageToUpload);
            ((AuthenticateView)view).setErrorText(null);
            ((AuthenticateView)view).setToast("Registering...");
            loginService.register(firstName, lastName, alias, password, imageToUpload, new GetAuthenticateObserver());
        } catch (Exception e) {
            ((AuthenticateView)view).setErrorText(e.getMessage());
        }
    }

    public void validateRegistration(EditText firstName, EditText lastName, EditText alias,
                                     EditText password, ImageView imageToUpload) {
        if (firstName.getText().length() == 0) {
            throw new IllegalArgumentException("First Name cannot be empty.");
        }
        if (lastName.getText().length() == 0) {
            throw new IllegalArgumentException("Last Name cannot be empty.");
        }
        if (alias.getText().length() == 0) {
            throw new IllegalArgumentException("Alias cannot be empty.");
        }
        if (alias.getText().charAt(0) != '@') {
            throw new IllegalArgumentException("Alias must begin with @.");
        }
        if (alias.getText().length() < 2) {
            throw new IllegalArgumentException("Alias must contain 1 or more characters after the @.");
        }
        if (password.getText().length() == 0) {
            throw new IllegalArgumentException("Password cannot be empty.");
        }

        if (imageToUpload.getDrawable() == null) {
            throw new IllegalArgumentException("Profile image must be uploaded.");
        }
    }
}
