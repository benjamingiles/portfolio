package edu.byu.cs.tweeter.server.service;

import java.util.ArrayList;
import java.util.List;

import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.FollowRequest;
import edu.byu.cs.tweeter.model.net.request.FollowersRequest;
import edu.byu.cs.tweeter.model.net.request.FollowingRequest;
import edu.byu.cs.tweeter.model.net.request.GetCountRequest;
import edu.byu.cs.tweeter.model.net.request.IsFollowerRequest;
import edu.byu.cs.tweeter.model.net.request.UnfollowRequest;
import edu.byu.cs.tweeter.model.net.response.FollowResponse;
import edu.byu.cs.tweeter.model.net.response.FollowersResponse;
import edu.byu.cs.tweeter.model.net.response.FollowingResponse;
import edu.byu.cs.tweeter.model.net.response.GetCountResponse;
import edu.byu.cs.tweeter.model.net.response.IsFollowerResponse;
import edu.byu.cs.tweeter.model.net.response.UnfollowResponse;
import edu.byu.cs.tweeter.server.dao.AuthtokenDAO;
import edu.byu.cs.tweeter.server.dao.FollowDAO;
import edu.byu.cs.tweeter.server.dao.UserDAO;
import edu.byu.cs.tweeter.server.dao.dynamodb.DataPage;
import edu.byu.cs.tweeter.server.dao.dynamodb.bean.AuthtokenBean;
import edu.byu.cs.tweeter.server.dao.dynamodb.bean.FollowsBean;
import edu.byu.cs.tweeter.server.dao.dynamodb.bean.UserBean;
import edu.byu.cs.tweeter.server.factory.Factory;

/**
 * Contains the business logic for getting the users a user is following.
 */
public class FollowService {

    private Factory factory;

    private FollowDAO followDAO;
    private UserDAO userDAO;
    private AuthtokenDAO authtokenDAO;

    public FollowService(Factory factory) {
        this.factory = factory;
        followDAO = factory.getFollowDAO();
        userDAO = factory.getUserDAO();
        authtokenDAO = factory.getAuthtokenDAO();
    }

    /**
     * Returns the users that the user specified in the request is following. Uses information in
     * the request object to limit the number of followees returned and to return the next set of
     * followees after any that were returned in a previous request. Uses the {@link FollowDAO} to
     * get the followees.
     *
     * @param request contains the data required to fulfill the request.
     * @return the followees.
     */
    public FollowingResponse getFollowees(FollowingRequest request) {
        if(request.getFollowerAlias() == null) {
            throw new RuntimeException("[Bad Request] Request needs to have a follower alias");
        } else if(request.getLimit() <= 0) {
            throw new RuntimeException("[Bad Request] Request needs to have a positive limit");
        }

        if (!checkAuthtoken(request.getAuthToken().getToken())) {
            return new FollowingResponse("authtoken timeout");
        }

        DataPage<FollowsBean> followeesPage = followDAO.getPageofFollowees(request.getFollowerAlias(),
                request.getLimit(), request.getLastFolloweeAlias());

        List<User> followees = new ArrayList<>();

        for(FollowsBean followsBean : followeesPage.getValues()) {
            UserBean userBean = userDAO.getUser(followsBean.getFollowee_handle());
            User user = new User(userBean.getFirst_name(), userBean.getLast_name(),
                    userBean.getAlias(), userBean.getImageURL());
            followees.add(user);
        }

        return new FollowingResponse(followees, followeesPage.isHasMorePages());
    }

    public FollowersResponse getFollowers(FollowersRequest request) {
        if(request.getFollowerAlias() == null) {
            throw new RuntimeException("[Bad Request] Request needs to have a follower alias");
        } else if(request.getLimit() <= 0) {
            throw new RuntimeException("[Bad Request] Request needs to have a positive limit");
        }

        if (!checkAuthtoken(request.getAuthToken().getToken())) {
            return new FollowersResponse("authtoken timeout");
        }

        DataPage<FollowsBean> followersPage = followDAO.getPageofFollowers(request.getFollowerAlias(),
                request.getLimit(), request.getLastFollowerAlias());

        List<User> followers = new ArrayList<>();

        for(FollowsBean followsBean : followersPage.getValues()) {
            UserBean userBean = userDAO.getUser(followsBean.getFollow_handle());
            User user = new User(userBean.getFirst_name(), userBean.getLast_name(),
                    userBean.getAlias(), userBean.getImageURL());
            followers.add(user);
        }

        return new FollowersResponse(followers, followersPage.isHasMorePages());
    }

    public FollowResponse follow(FollowRequest request) {
        if(request.getFollowee().getAlias() == null) {
            throw new RuntimeException("[Bad Request] Request needs to have a followee");
        } else if(request.getCurrUser().getAlias() == null) {
            throw new RuntimeException("[Bad Request] Request needs to have a follower");
        }

        if (!checkAuthtoken(request.getAuthToken().getToken())) {
            return new FollowResponse("authtoken timeout");
        }

        followDAO.putFollows(request.getCurrUser().getAlias(), request.getCurrUser().getName(),
                request.getFollowee().getAlias(), request.getFollowee().getName());

        userDAO.increaseFollowingCount(request.getCurrUser().getAlias());
        userDAO.increaseFollowersCount(request.getFollowee().getAlias());

        return new FollowResponse();
    }

    public UnfollowResponse unfollow(UnfollowRequest request) {
        if(request.getFollowee() == null) {
            throw new RuntimeException("[Bad Request] Request needs to have a followee");
        } else if(request.getCurrUser() == null) {
            throw new RuntimeException("[Bad Request] Request needs to have a follower");
        }

        if (!checkAuthtoken(request.getAuthToken().getToken())) {
            return new UnfollowResponse("authtoken timeout");
        }

        followDAO.deleteFollows(request.getCurrUser(), request.getFollowee());

        userDAO.decreaseFollowingCount(request.getCurrUser());
        userDAO.decreaseFollowersCount(request.getFollowee());

        return new UnfollowResponse();
    }

    public IsFollowerResponse isFollower(IsFollowerRequest request) {
        if(request.getFollower() == null) {
            throw new RuntimeException("[Bad Request] Request need to have a follower");
        } else if(request.getFollowee() == null) {
            throw new RuntimeException("[Bad Request] Request needs to have a followee");
        }

        if (!checkAuthtoken(request.getAuthToken().getToken())) {
            return new IsFollowerResponse("authtoken timeout");
        }

        FollowsBean followsBean = followDAO.getFollows(request.getFollower().getAlias(),
                request.getFollowee().getAlias());

        boolean isFollower;

        if (followsBean != null) {
            System.out.println("follows bean is not null");
            isFollower = true;
        } else {
            isFollower = false;
        }
        System.out.println(isFollower);
        return new IsFollowerResponse(isFollower);
    }

    public GetCountResponse getFollowingCount(GetCountRequest request) {
        if(request.getTargetUser() == null) {
            throw new RuntimeException("[Bad Request] Request need to have a target user");
        }

        if (!checkAuthtoken(request.getAuthToken().getToken())) {
            return new GetCountResponse("authtoken timeout");
        }

        UserBean userBean = userDAO.getUser(request.getTargetUser().getAlias());
        return new GetCountResponse(userBean.getFollowing_count());
    }

    public GetCountResponse getFollowersCount(GetCountRequest request) {
        if(request.getTargetUser() == null) {
            throw new RuntimeException("[Bad Request] Request need to have a target user");
        }

        if (!checkAuthtoken(request.getAuthToken().getToken())) {
            return new GetCountResponse("authtoken timeout");
        }

        UserBean userBean = userDAO.getUser(request.getTargetUser().getAlias());
        return new GetCountResponse(userBean.getFollowers_count());
    }

    private boolean checkAuthtoken(String authtoken) {
        AuthtokenBean authtokenBean = authtokenDAO.getAuthtoken(authtoken);
        if (authtokenBean.getTimeout() + 3600000 > System.currentTimeMillis()) {
            return true;
        } else {
            authtokenDAO.deleteAuthtoken(authtoken);
            return false;
        }
    }

    /**
     * Returns an instance of {@link FollowDAO}. Allows mocking of the FollowDAO class
     * for testing purposes. All usages of FollowDAO should get their FollowDAO
     * instance from this method to allow for mocking of the instance.
     *
     * @return the instance.
     */
    FollowDAO getFollowDAO() {
        return factory.getFollowDAO();
    }

}
