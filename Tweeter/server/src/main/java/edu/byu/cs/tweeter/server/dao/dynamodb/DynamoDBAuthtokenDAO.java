package edu.byu.cs.tweeter.server.dao.dynamodb;

import edu.byu.cs.tweeter.server.dao.AuthtokenDAO;
import edu.byu.cs.tweeter.server.dao.dynamodb.bean.AuthtokenBean;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

public class DynamoDBAuthtokenDAO implements AuthtokenDAO {
    private static final String TableName = "authtoken";

    private static DynamoDbClient dynamoDbClient = DynamoDbClient.builder()
            .region(Region.US_WEST_2)
            .build();

    private static DynamoDbEnhancedClient enhancedClient = DynamoDbEnhancedClient.builder()
            .dynamoDbClient(dynamoDbClient)
            .build();

    public AuthtokenBean getAuthtoken(String authtoken) {
        DynamoDbTable<AuthtokenBean> table = enhancedClient.table(TableName, TableSchema.fromBean(AuthtokenBean.class));
        Key key = Key.builder()
                .partitionValue(authtoken)
                .build();

        return table.getItem(key);
    }

    public void putAuthtoken(String authtoken, String user_alias, long timeout) {
        DynamoDbTable<AuthtokenBean> table = enhancedClient.table(TableName, TableSchema.fromClass(AuthtokenBean.class));

        AuthtokenBean newAuthtoken = new AuthtokenBean();
        newAuthtoken.setAuthtoken(authtoken);
        newAuthtoken.setUser_alias(user_alias);
        newAuthtoken.setTimeout(timeout);
        table.putItem(newAuthtoken);
    }

    public void deleteAuthtoken(String authtoken) {
        DynamoDbTable<AuthtokenBean> table = enhancedClient.table(TableName, TableSchema.fromBean(AuthtokenBean.class));
        Key key = Key.builder()
                .partitionValue(authtoken)
                .build();

        table.deleteItem(key);
    }
}
