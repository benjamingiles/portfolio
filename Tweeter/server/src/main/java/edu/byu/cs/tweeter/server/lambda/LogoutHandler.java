package edu.byu.cs.tweeter.server.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import edu.byu.cs.tweeter.model.net.request.LogoutRequest;
import edu.byu.cs.tweeter.model.net.response.LogoutReponse;
import edu.byu.cs.tweeter.server.factory.DynamoDBFactory;
import edu.byu.cs.tweeter.server.factory.Factory;
import edu.byu.cs.tweeter.server.service.UserService;

public class LogoutHandler implements RequestHandler<LogoutRequest, LogoutReponse> {

    @Override
    public LogoutReponse handleRequest(LogoutRequest request, Context context) {
        Factory factory = new DynamoDBFactory();
        UserService userService = new UserService(factory);
        return userService.logout(request);
    }
}
