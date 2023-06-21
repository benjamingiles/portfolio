package edu.byu.cs.tweeter.server.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import edu.byu.cs.tweeter.model.net.request.RegisterRequest;
import edu.byu.cs.tweeter.model.net.response.AuthenticateResponse;
import edu.byu.cs.tweeter.server.factory.DynamoDBFactory;
import edu.byu.cs.tweeter.server.factory.Factory;
import edu.byu.cs.tweeter.server.service.UserService;

public class RegisterHandler implements RequestHandler<RegisterRequest, AuthenticateResponse> {

    @Override
    public AuthenticateResponse handleRequest(RegisterRequest registerRequest, Context context) {
        Factory factory = new DynamoDBFactory();
        UserService userService = new UserService(factory);
        return userService.register(registerRequest);
    }
}
