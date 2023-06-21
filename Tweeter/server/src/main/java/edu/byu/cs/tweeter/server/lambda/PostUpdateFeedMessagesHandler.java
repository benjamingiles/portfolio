package edu.byu.cs.tweeter.server.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.SQSEvent;

import edu.byu.cs.tweeter.server.factory.DynamoDBFactory;
import edu.byu.cs.tweeter.server.factory.Factory;
import edu.byu.cs.tweeter.server.service.StatusService;

public class PostUpdateFeedMessagesHandler implements RequestHandler<SQSEvent, Void> {
    @Override
    public Void handleRequest(SQSEvent event, Context context) {
        Factory factory = new DynamoDBFactory();
        StatusService service = new StatusService(factory);
        for (SQSEvent.SQSMessage message : event.getRecords()) {
            service.postUpdateFeedMessages(message.getBody());
        }
        return null;
    }
}
