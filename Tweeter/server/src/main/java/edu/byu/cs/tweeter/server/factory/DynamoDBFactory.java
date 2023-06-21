package edu.byu.cs.tweeter.server.factory;

import edu.byu.cs.tweeter.server.dao.AuthtokenDAO;
import edu.byu.cs.tweeter.server.dao.FeedDAO;
import edu.byu.cs.tweeter.server.dao.FollowDAO;
import edu.byu.cs.tweeter.server.dao.StoryDAO;
import edu.byu.cs.tweeter.server.dao.UserDAO;
import edu.byu.cs.tweeter.server.dao.dynamodb.DynamoDBAuthtokenDAO;
import edu.byu.cs.tweeter.server.dao.dynamodb.DynamoDBFeedDAO;
import edu.byu.cs.tweeter.server.dao.dynamodb.DynamoDBFollowDAO;
import edu.byu.cs.tweeter.server.dao.dynamodb.DynamoDBStoryDAO;
import edu.byu.cs.tweeter.server.dao.dynamodb.DynamoDBUserDAO;

public class DynamoDBFactory implements Factory {
    @Override
    public AuthtokenDAO getAuthtokenDAO() {
        return new DynamoDBAuthtokenDAO();
    }

    @Override
    public FeedDAO getFeedDAO() {
        return new DynamoDBFeedDAO();
    }

    @Override
    public FollowDAO getFollowDAO() {
        return new DynamoDBFollowDAO();
    }

    @Override
    public StoryDAO getStoryDAO() {
        return new DynamoDBStoryDAO();
    }

    @Override
    public UserDAO getUserDAO() {
        return new DynamoDBUserDAO();
    }
}
