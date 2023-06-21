package edu.byu.cs.tweeter.server.dao.dynamodb;

import java.util.HashMap;
import java.util.Map;

import edu.byu.cs.tweeter.server.dao.FollowDAO;
import edu.byu.cs.tweeter.server.dao.dynamodb.bean.FollowsBean;
import software.amazon.awssdk.core.pagination.sync.SdkIterable;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbIndex;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.Page;
import software.amazon.awssdk.enhanced.dynamodb.model.PageIterable;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

/**
 * A DAO for accessing 'following' data from the database.
 */
public class DynamoDBFollowDAO implements FollowDAO {

    private static final String TableName = "follows";
    public static final String IndexName = "follows_index";

    private static final String followAttr = "follow_handle";
    private static final String followeeAttr = "followee_handle";

    private static DynamoDbClient dynamoDbClient = DynamoDbClient.builder()
            .region(Region.US_WEST_2)
            .build();

    private static DynamoDbEnhancedClient enhancedClient = DynamoDbEnhancedClient.builder()
            .dynamoDbClient(dynamoDbClient)
            .build();

    private static boolean isNonEmptyString(String value) {
        return (value != null && value.length() > 0);
    }

    public FollowsBean getFollows(String follow_handle, String followee_handle) {
        DynamoDbTable<FollowsBean> table = enhancedClient.table(TableName, TableSchema.fromBean(FollowsBean.class));
        Key key = Key.builder()
                .partitionValue(follow_handle).sortValue(followee_handle)
                .build();

        return table.getItem(key);
    }

    public void putFollows(String follow_handle, String followerName,
                                String followee_handle, String followeeName) {
        DynamoDbTable<FollowsBean> table = enhancedClient.table(TableName, TableSchema.fromClass(FollowsBean.class));

        FollowsBean newFollows = new FollowsBean();
        newFollows.setFollow_handle(follow_handle);
        newFollows.setFollowerName(followerName);
        newFollows.setFollowee_handle(followee_handle);
        newFollows.setFolloweeName(followeeName);
        table.putItem(newFollows);
    }

    public void deleteFollows(String follow_handle, String followee_handle) {
        DynamoDbTable<FollowsBean> table = enhancedClient.table(TableName, TableSchema.fromBean(FollowsBean.class));
        Key key = Key.builder()
                .partitionValue(follow_handle).sortValue(followee_handle)
                .build();

        table.deleteItem(key);
    }

    public DataPage<FollowsBean> getPageofFollowees(String targetUserAlias, int pageSize, String lastUserAlias) {
        DynamoDbTable<FollowsBean> table = enhancedClient.table(TableName, TableSchema.fromBean(FollowsBean.class));
        Key key = Key.builder()
                .partitionValue(targetUserAlias)
                .build();

        QueryEnhancedRequest.Builder requestBuilder = QueryEnhancedRequest.builder()
                .queryConditional(QueryConditional.keyEqualTo(key))
                .limit(pageSize);

        if(isNonEmptyString(lastUserAlias)) {
            // Build up the Exclusive Start Key (telling DynamoDB where you left off reading items)
            Map<String, AttributeValue> startKey = new HashMap<>();
            startKey.put(followAttr, AttributeValue.builder().s(targetUserAlias).build());
            startKey.put(followeeAttr, AttributeValue.builder().s(lastUserAlias).build());

            requestBuilder.exclusiveStartKey(startKey);
        }

        QueryEnhancedRequest request = requestBuilder.build();

        DataPage<FollowsBean> result = new DataPage<FollowsBean>();

        PageIterable<FollowsBean> pages = table.query(request);
        pages.stream()
                .limit(1)
                .forEach((Page<FollowsBean> page) -> {
                    result.setHasMorePages(page.lastEvaluatedKey() != null);
                    page.items().forEach(visit -> result.getValues().add(visit));
                });

        return result;
    }

    public DataPage<FollowsBean> getPageofFollowers(String targetUserAlias, int pageSize, String lastUserAlias) {
        DynamoDbIndex<FollowsBean> index = enhancedClient.table(TableName, TableSchema.fromBean(FollowsBean.class)).index(IndexName);
        Key key = Key.builder()
                .partitionValue(targetUserAlias)
                .build();

        QueryEnhancedRequest.Builder requestBuilder = QueryEnhancedRequest.builder()
                .queryConditional(QueryConditional.keyEqualTo(key))
                .limit(pageSize);

        if(isNonEmptyString(lastUserAlias)) {
            Map<String, AttributeValue> startKey = new HashMap<>();
            startKey.put(followeeAttr, AttributeValue.builder().s(targetUserAlias).build());
            startKey.put(followAttr, AttributeValue.builder().s(lastUserAlias).build());

            requestBuilder.exclusiveStartKey(startKey);
        }

        QueryEnhancedRequest request = requestBuilder.build();

        DataPage<FollowsBean> result = new DataPage<FollowsBean>();

        SdkIterable<Page<FollowsBean>> sdkIterable = index.query(request);
        PageIterable<FollowsBean> pages = PageIterable.create(sdkIterable);
        pages.stream()
                .limit(1)
                .forEach((Page<FollowsBean> page) -> {
                    result.setHasMorePages(page.lastEvaluatedKey() != null);
                    page.items().forEach(visit -> result.getValues().add(visit));
                });

        return result;
    }

    public DataPage<FollowsBean> getAllFollowers(String targetUserAlias, String lastUserAlias) {
        DynamoDbIndex<FollowsBean> index = enhancedClient.table(TableName, TableSchema.fromBean(FollowsBean.class)).index(IndexName);
        Key key = Key.builder()
                .partitionValue(targetUserAlias)
                .build();

        QueryEnhancedRequest.Builder requestBuilder = QueryEnhancedRequest.builder()
                .queryConditional(QueryConditional.keyEqualTo(key));

        if(isNonEmptyString(lastUserAlias)) {
            Map<String, AttributeValue> startKey = new HashMap<>();
            startKey.put(followeeAttr, AttributeValue.builder().s(targetUserAlias).build());
            startKey.put(followAttr, AttributeValue.builder().s(lastUserAlias).build());

            requestBuilder.exclusiveStartKey(startKey);
        }

        QueryEnhancedRequest request = requestBuilder.build();

        DataPage<FollowsBean> result = new DataPage<FollowsBean>();

        SdkIterable<Page<FollowsBean>> sdkIterable = index.query(request);
        PageIterable<FollowsBean> pages = PageIterable.create(sdkIterable);
        pages.stream()
                .limit(1)
                .forEach((Page<FollowsBean> page) -> {
                    result.setHasMorePages(page.lastEvaluatedKey() != null);
                    page.items().forEach(visit -> result.getValues().add(visit));
                });

        return result;
    }
}
