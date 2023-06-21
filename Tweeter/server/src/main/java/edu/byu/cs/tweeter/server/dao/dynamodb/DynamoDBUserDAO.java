package edu.byu.cs.tweeter.server.dao.dynamodb;

import edu.byu.cs.tweeter.server.dao.UserDAO;
import edu.byu.cs.tweeter.server.dao.dynamodb.bean.UserBean;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

public class DynamoDBUserDAO implements UserDAO {

    private static final String TableName = "user";

    private static DynamoDbClient dynamoDbClient = DynamoDbClient.builder()
            .region(Region.US_WEST_2)
            .build();

    private static DynamoDbEnhancedClient enhancedClient = DynamoDbEnhancedClient.builder()
            .dynamoDbClient(dynamoDbClient)
            .build();

    public UserBean getUser(String alias) {
        DynamoDbTable<UserBean> table = enhancedClient.table(TableName, TableSchema.fromBean(UserBean.class));
        Key key = Key.builder()
                .partitionValue(alias)
                .build();

        return table.getItem(key);
    }

    public void putUser(String alias, String first_name, String last_name, String imageURL,
                        int followers_count, int followees_count, String password) {
        DynamoDbTable<UserBean> table = enhancedClient.table(TableName, TableSchema.fromClass(UserBean.class));

        UserBean newUser = new UserBean();
        newUser.setAlias(alias);
        newUser.setFirst_name(first_name);
        newUser.setLast_name(last_name);
        newUser.setImageURL(imageURL);
        newUser.setFollowers_count(followers_count);
        newUser.setFollowing_count(followees_count);
        newUser.setPassword(password);
        table.putItem(newUser);
    }

    public void increaseFollowersCount(String alias) {
        DynamoDbTable<UserBean> table = enhancedClient.table(TableName, TableSchema.fromClass(UserBean.class));
        Key key = Key.builder().partitionValue(alias).build();

        UserBean user = table.getItem(key);
        if (user != null) {
            int followers_count = user.getFollowers_count();
            user.setFollowers_count(followers_count + 1);
            table.updateItem(user);
        }
    }

    public void decreaseFollowersCount(String alias) {
        DynamoDbTable<UserBean> table = enhancedClient.table(TableName, TableSchema.fromClass(UserBean.class));
        Key key = Key.builder().partitionValue(alias).build();

        UserBean user = table.getItem(key);
        if (user != null) {
            int followers_count = user.getFollowers_count();
            user.setFollowers_count(followers_count - 1);
            table.updateItem(user);
        }
    }

    public void increaseFollowingCount(String alias) {
        DynamoDbTable<UserBean> table = enhancedClient.table(TableName, TableSchema.fromClass(UserBean.class));
        Key key = Key.builder().partitionValue(alias).build();

        UserBean user = table.getItem(key);
        if (user != null) {
            int following_count = user.getFollowing_count();
            user.setFollowing_count(following_count + 1);
            table.updateItem(user);
        }
    }

    public void decreaseFollowingCount(String alias) {
        DynamoDbTable<UserBean> table = enhancedClient.table(TableName, TableSchema.fromClass(UserBean.class));
        Key key = Key.builder().partitionValue(alias).build();

        UserBean user = table.getItem(key);
        if (user != null) {
            int following_count = user.getFollowing_count();
            user.setFollowing_count(following_count - 1);
            table.updateItem(user);
        }
    }

}
