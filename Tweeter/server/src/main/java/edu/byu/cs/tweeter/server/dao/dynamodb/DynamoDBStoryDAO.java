package edu.byu.cs.tweeter.server.dao.dynamodb;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.byu.cs.tweeter.server.dao.StoryDAO;
import edu.byu.cs.tweeter.server.dao.dynamodb.bean.StoryBean;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
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

public class DynamoDBStoryDAO implements StoryDAO {

    private static final String TableName = "story";

    private static final String aliasAttr = "sender_alias";
    private static final String timeAttr = "timestamp";

    private static DynamoDbClient dynamoDbClient = DynamoDbClient.builder()
            .region(Region.US_WEST_2)
            .build();

    private static DynamoDbEnhancedClient enhancedClient = DynamoDbEnhancedClient.builder()
            .dynamoDbClient(dynamoDbClient)
            .build();

    private static boolean isNonEmptyString(String value) {
        return (value != null && value.length() > 0);
    }

    public void putStory(String sender_alias, long timestamp, String post, List<String> urls, List<String> mentions) {
        DynamoDbTable<StoryBean> table = enhancedClient.table(TableName, TableSchema.fromClass(StoryBean.class));

        StoryBean newStory = new StoryBean();
        newStory.setSender_alias(sender_alias);
        newStory.setTimestamp(timestamp);
        newStory.setPost(post);
        newStory.setUrls(urls);
        newStory.setMentions(mentions);
        table.putItem(newStory);
    }

    public DataPage<StoryBean> getPageofStory(String targetUserAlias, int pageSize, String lastTimestamp) {
        DynamoDbTable<StoryBean> table = enhancedClient.table(TableName, TableSchema.fromBean(StoryBean.class));
        Key key = Key.builder()
                .partitionValue(targetUserAlias)
                .build();

        QueryEnhancedRequest.Builder requestBuilder = QueryEnhancedRequest.builder()
                .queryConditional(QueryConditional.keyEqualTo(key))
                .scanIndexForward(false)
                .limit(pageSize);

        if (isNonEmptyString(lastTimestamp)) {
            // Build up the Exclusive Start Key (telling DynamoDB where you left off reading items)
            Map<String, AttributeValue> startKey = new HashMap<>();
            startKey.put(aliasAttr, AttributeValue.builder().s(targetUserAlias).build());
            startKey.put(timeAttr, AttributeValue.builder().n(lastTimestamp).build());

            requestBuilder.exclusiveStartKey(startKey);
        }

        QueryEnhancedRequest request = requestBuilder.build();

        DataPage<StoryBean> result = new DataPage<StoryBean>();

        PageIterable<StoryBean> pages = table.query(request);
        pages.stream()
                .limit(1)
                .forEach((Page<StoryBean> page) -> {
                    result.setHasMorePages(page.lastEvaluatedKey() != null);
                    page.items().forEach(visit -> result.getValues().add(visit));
                });

        return result;
    }
}
