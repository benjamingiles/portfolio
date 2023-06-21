package edu.byu.cs.tweeter.server.dao;

import edu.byu.cs.tweeter.server.dao.dynamodb.DataPage;
import edu.byu.cs.tweeter.server.dao.dynamodb.bean.FollowsBean;

public interface FollowDAO {
    FollowsBean getFollows(String follow_handle, String followee_handle);
    void putFollows(String follow_handle, String follower_name,
                    String followee_handle, String followee_name);
    void deleteFollows(String follow_handle, String followee_handle);
    DataPage<FollowsBean> getPageofFollowers(String targetUserAlias, int pageSize, String lastUserAlias);
    DataPage<FollowsBean> getPageofFollowees(String targetUserAlias, int pageSize, String lastUserAlias);
    DataPage<FollowsBean> getAllFollowers(String targetUserAlias, String lastUserAlias);
}
