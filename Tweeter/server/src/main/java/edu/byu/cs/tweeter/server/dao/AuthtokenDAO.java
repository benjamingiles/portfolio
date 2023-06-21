package edu.byu.cs.tweeter.server.dao;

import edu.byu.cs.tweeter.server.dao.dynamodb.bean.AuthtokenBean;

public interface AuthtokenDAO {
    void deleteAuthtoken(String authtoken);
    void putAuthtoken(String authtoken, String user_alias, long timeout);
    AuthtokenBean getAuthtoken(String authtoken);
}
