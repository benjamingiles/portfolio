package edu.byu.cs.tweeter.server.dao;

import edu.byu.cs.tweeter.server.dao.dynamodb.bean.UserBean;

public interface UserDAO {
    void putUser(String alias, String first_name, String last_name, String imageURL,
                 int followers_count, int followees_count, String password);
    UserBean getUser(String alias);
    void increaseFollowingCount(String alias);
    void decreaseFollowingCount(String alias);
    void increaseFollowersCount(String alias);
    void decreaseFollowersCount(String alias);
}
