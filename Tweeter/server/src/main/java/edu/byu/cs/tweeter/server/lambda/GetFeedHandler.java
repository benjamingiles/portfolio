package edu.byu.cs.tweeter.server.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import edu.byu.cs.tweeter.model.net.request.FeedRequest;
import edu.byu.cs.tweeter.model.net.response.FeedResponse;
import edu.byu.cs.tweeter.server.factory.DynamoDBFactory;
import edu.byu.cs.tweeter.server.factory.Factory;
import edu.byu.cs.tweeter.server.service.StatusService;

public class GetFeedHandler implements RequestHandler<FeedRequest, FeedResponse> {
    @Override
    public FeedResponse handleRequest(FeedRequest request, Context context) {
        Factory factory = new DynamoDBFactory();
        StatusService service = new StatusService(factory);
        return service.getFeed(request);
    }
}
