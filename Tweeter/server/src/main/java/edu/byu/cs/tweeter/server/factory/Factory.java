package edu.byu.cs.tweeter.server.factory;

import edu.byu.cs.tweeter.server.dao.AuthtokenDAO;
import edu.byu.cs.tweeter.server.dao.FeedDAO;
import edu.byu.cs.tweeter.server.dao.FollowDAO;
import edu.byu.cs.tweeter.server.dao.StoryDAO;
import edu.byu.cs.tweeter.server.dao.UserDAO;

public interface Factory {
    AuthtokenDAO getAuthtokenDAO();
    FeedDAO getFeedDAO();
    FollowDAO getFollowDAO();
    StoryDAO getStoryDAO();
    UserDAO getUserDAO();
}
