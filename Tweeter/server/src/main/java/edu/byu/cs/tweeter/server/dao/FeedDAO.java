package edu.byu.cs.tweeter.server.dao;

import java.util.List;

import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.server.dao.dynamodb.DataPage;
import edu.byu.cs.tweeter.server.dao.dynamodb.bean.FeedBean;
import edu.byu.cs.tweeter.server.dao.dynamodb.bean.FollowsBean;

public interface FeedDAO {
    void putFeed(String receiver_alias, long timestamp, String sender_alias, String post, List<String> urls, List<String> mentions);
    DataPage<FeedBean> getPageofFeed(String targetUserAlias, int pageSize, String lastTimestamp);
    void addFeedBatch(List<String> followers, Status status);
}
