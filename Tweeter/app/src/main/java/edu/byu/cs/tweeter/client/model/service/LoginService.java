package edu.byu.cs.tweeter.client.model.service;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.widget.EditText;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;
import java.util.Base64;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.backgroundTask.LoginTask;
import edu.byu.cs.tweeter.client.model.backgroundTask.LogoutTask;
import edu.byu.cs.tweeter.client.model.backgroundTask.RegisterTask;
import edu.byu.cs.tweeter.client.model.backgroundTask.handler.AuthenticateHandler;
import edu.byu.cs.tweeter.client.model.backgroundTask.handler.SimpleNotificationHandler;
import edu.byu.cs.tweeter.client.model.backgroundTask.observer.AuthenticateObserver;
import edu.byu.cs.tweeter.client.model.backgroundTask.observer.SimpleNotificationObserver;

public class LoginService {

    public void login(EditText alias, EditText password, AuthenticateObserver observer) {
        // Send the login request.
        LoginTask loginTask = new LoginTask(alias.getText().toString(),
                password.getText().toString(),
                new AuthenticateHandler(observer));
        Executor.execute(loginTask);
    }

    public void register(EditText firstName, EditText lastName, EditText alias,
                         EditText password, ImageView imageToUpload, AuthenticateObserver observer) {
        // Convert image to byte array.
        Bitmap image = ((BitmapDrawable) imageToUpload.getDrawable()).getBitmap();
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, bos);
        byte[] imageBytes = bos.toByteArray();

        // Intentionally, Use the java Base64 encoder so it is compatible with M4.
        String imageBytesBase64 = Base64.getEncoder().encodeToString(imageBytes);

        // Send register request.
        RegisterTask registerTask = new RegisterTask(firstName.getText().toString(), lastName.getText().toString(),
                alias.getText().toString(), password.getText().toString(), imageBytesBase64, new AuthenticateHandler(observer));

        Executor.execute(registerTask);
    }

    public void logout(SimpleNotificationObserver observer) {
        LogoutTask logoutTask = new LogoutTask(Cache.getInstance().getCurrUserAuthToken(), new SimpleNotificationHandler(observer));
        Executor.execute(logoutTask);
    }

}
