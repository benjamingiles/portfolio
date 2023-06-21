package edu.byu.cs.tweeter.server.dao;

import java.util.List;

import edu.byu.cs.tweeter.server.dao.dynamodb.DataPage;
import edu.byu.cs.tweeter.server.dao.dynamodb.bean.StoryBean;

public interface StoryDAO {
    DataPage<StoryBean> getPageofStory(String targetUserAlias, int pageSize, String lastTimestamp);
    void putStory(String sender_alias, long timestamp, String post, List<String> urls, List<String> mentions);
}
