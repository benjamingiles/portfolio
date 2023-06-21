package edu.byu.cs.tweeter.server.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import edu.byu.cs.tweeter.model.net.request.GetCountRequest;
import edu.byu.cs.tweeter.model.net.response.GetCountResponse;
import edu.byu.cs.tweeter.server.factory.DynamoDBFactory;
import edu.byu.cs.tweeter.server.factory.Factory;
import edu.byu.cs.tweeter.server.service.FollowService;

public class GetFollowersCountHandler implements RequestHandler<GetCountRequest, GetCountResponse> {
    @Override
    public GetCountResponse handleRequest(GetCountRequest request, Context context) {
        Factory factory = new DynamoDBFactory();
        FollowService service = new FollowService(factory);
        return service.getFollowersCount(request);
    }
}
