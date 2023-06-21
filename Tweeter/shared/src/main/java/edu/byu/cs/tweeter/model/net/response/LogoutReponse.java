package edu.byu.cs.tweeter.model.net.response;

public class LogoutReponse extends Response {
    public LogoutReponse(String message) {
        super(false, message);
    }

    public LogoutReponse() {
        super(true, null);
    }
}
