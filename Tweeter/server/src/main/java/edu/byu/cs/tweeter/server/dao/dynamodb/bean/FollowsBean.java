package edu.byu.cs.tweeter.server.dao.dynamodb.bean;

import edu.byu.cs.tweeter.server.dao.dynamodb.DynamoDBFollowDAO;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSecondaryPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSecondarySortKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey;

@DynamoDbBean
public class FollowsBean {
    private String follow_handle;
    private String followerName;
    private String followee_handle;
    private String followeeName;

    @DynamoDbPartitionKey
    @DynamoDbSecondarySortKey(indexNames = DynamoDBFollowDAO.IndexName)
    public String getFollow_handle() {
        return follow_handle;
    }

    public void setFollow_handle(String follow_handle) {
        this.follow_handle = follow_handle;
    }

    public String getFollowerName() {
        return followerName;
    }

    public void setFollowerName(String followerName) {
        this.followerName = followerName;
    }

    @DynamoDbSortKey
    @DynamoDbSecondaryPartitionKey(indexNames = DynamoDBFollowDAO.IndexName)
    public String getFollowee_handle() {
        return followee_handle;
    }

    public void setFollowee_handle(String followee_handle) {
        this.followee_handle = followee_handle;
    }

    public String getFolloweeName() {
        return followeeName;
    }

    public void setFolloweeName(String followeeName) {
        this.followeeName = followeeName;
    }
}
