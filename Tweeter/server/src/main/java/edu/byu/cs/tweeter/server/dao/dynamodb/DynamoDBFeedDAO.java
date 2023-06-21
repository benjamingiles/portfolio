package edu.byu.cs.tweeter.server.dao.dynamodb;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.server.dao.FeedDAO;
import edu.byu.cs.tweeter.server.dao.dynamodb.bean.FeedBean;
import edu.byu.cs.tweeter.server.dao.dynamodb.bean.FollowsBean;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.BatchWriteItemEnhancedRequest;
import software.amazon.awssdk.enhanced.dynamodb.model.BatchWriteResult;
import software.amazon.awssdk.enhanced.dynamodb.model.Page;
import software.amazon.awssdk.enhanced.dynamodb.model.PageIterable;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest;
import software.amazon.awssdk.enhanced.dynamodb.model.WriteBatch;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException;

public class DynamoDBFeedDAO implements FeedDAO {

    private static final String TableName = "feed";

    private static final String aliasAttr = "receiver_alias";
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

    public void putFeed(String receiver_alias, long timestamp, String sender_alias, String post, List<String> urls, List<String> mentions) {
        DynamoDbTable<FeedBean> table = enhancedClient.table(TableName, TableSchema.fromClass(FeedBean.class));

        FeedBean newFeed = new FeedBean();
        newFeed.setReceiver_alias(receiver_alias);
        newFeed.setTimestamp(timestamp);
        newFeed.setSender_alias(sender_alias);
        newFeed.setPost(post);
        newFeed.setUrls(urls);
        newFeed.setMentions(mentions);
        table.putItem(newFeed);
    }

    public DataPage<FeedBean> getPageofFeed(String targetUserAlias, int pageSize, String lastTimestamp) {
        DynamoDbTable<FeedBean> table = enhancedClient.table(TableName, TableSchema.fromBean(FeedBean.class));
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

        DataPage<FeedBean> result = new DataPage<FeedBean>();

        PageIterable<FeedBean> pages = table.query(request);
        pages.stream()
                .limit(1)
                .forEach((Page<FeedBean> page) -> {
                    result.setHasMorePages(page.lastEvaluatedKey() != null);
                    page.items().forEach(visit -> result.getValues().add(visit));
                });

        return result;
    }

    public void addFeedBatch(List<String> followers, Status status) {
        List<FeedBean> batchToWrite = new ArrayList<>();
        for (String follower : followers) {

            FeedBean feedBean = new FeedBean();
            feedBean.setSender_alias(status.getUser().getAlias());
            feedBean.setTimestamp(status.getTimestamp());
            feedBean.setReceiver_alias(follower);
            feedBean.setPost(status.getPost());
            feedBean.setMentions(status.getMentions());
            feedBean.setUrls(status.getUrls());

            batchToWrite.add(feedBean);

            if (batchToWrite.size() == 25) {
                // package this batch up and send to DynamoDB.
                writeChunkOfUserDTOs(batchToWrite);
                batchToWrite = new ArrayList<>();
            }
        }

        // write any remaining
        if (batchToWrite.size() > 0) {
            // package this batch up and send to DynamoDB.
            writeChunkOfUserDTOs(batchToWrite);
        }
    }
    private void writeChunkOfUserDTOs(List<FeedBean> feedBeans) {
        if(feedBeans.size() > 25)
            throw new RuntimeException("Too many users to write");

        DynamoDbTable<FeedBean> table = enhancedClient.table(TableName, TableSchema.fromBean(FeedBean.class));
        WriteBatch.Builder<FeedBean> writeBuilder = WriteBatch.builder(FeedBean.class).mappedTableResource(table);
        for (FeedBean item : feedBeans) {
            writeBuilder.addPutItem(builder -> builder.item(item));
        }
        BatchWriteItemEnhancedRequest batchWriteItemEnhancedRequest = BatchWriteItemEnhancedRequest.builder()
                .writeBatches(writeBuilder.build()).build();

        try {
            BatchWriteResult result = enhancedClient.batchWriteItem(batchWriteItemEnhancedRequest);

            // just hammer dynamodb again with anything that didn't get written this time
            if (result.unprocessedPutItemsForTable(table).size() > 0) {
                writeChunkOfUserDTOs(result.unprocessedPutItemsForTable(table));
            }

        } catch (DynamoDbException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }

}
