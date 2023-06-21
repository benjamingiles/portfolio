package edu.byu.cs.tweeter.server.service;

import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.amazonaws.services.sqs.model.SendMessageResult;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.FeedRequest;
import edu.byu.cs.tweeter.model.net.request.PostStatusRequest;
import edu.byu.cs.tweeter.model.net.request.StoryRequest;
import edu.byu.cs.tweeter.model.net.response.FeedResponse;
import edu.byu.cs.tweeter.model.net.response.PostStatusResponse;
import edu.byu.cs.tweeter.model.net.response.StoryResponse;
import edu.byu.cs.tweeter.server.dao.AuthtokenDAO;
import edu.byu.cs.tweeter.server.dao.FeedDAO;
import edu.byu.cs.tweeter.server.dao.FollowDAO;
import edu.byu.cs.tweeter.server.dao.StoryDAO;
import edu.byu.cs.tweeter.server.dao.UserDAO;
import edu.byu.cs.tweeter.server.dao.dynamodb.DataPage;
import edu.byu.cs.tweeter.server.dao.dynamodb.bean.AuthtokenBean;
import edu.byu.cs.tweeter.server.dao.dynamodb.bean.FeedBean;
import edu.byu.cs.tweeter.server.dao.dynamodb.bean.FollowsBean;
import edu.byu.cs.tweeter.server.dao.dynamodb.bean.StoryBean;
import edu.byu.cs.tweeter.server.dao.dynamodb.bean.UserBean;
import edu.byu.cs.tweeter.server.factory.Factory;

public class StatusService {

    private Factory factory;
    private FeedDAO feedDAO;
    private StoryDAO storyDAO;
    private AuthtokenDAO authtokenDAO;
    private UserDAO userDAO;
    private FollowDAO followDAO;

    public StatusService(Factory factory) {
        this.factory = factory;
        feedDAO = factory.getFeedDAO();
        storyDAO = factory.getStoryDAO();
        authtokenDAO = factory.getAuthtokenDAO();
        userDAO = factory.getUserDAO();
        followDAO = factory.getFollowDAO();
    }

    public void postUpdateFeedMessages(String message) {
        System.out.println("deserializing");
        System.out.println(message);
        Status status = (new Gson()).fromJson(message, Status.class);
        System.out.println("deserialized: " + status.getPost());

        DataPage<FollowsBean> followsPage = new DataPage<>();
        followsPage.setHasMorePages(true);
        String lastUserAlias = null;

        AmazonSQS sqs = AmazonSQSClientBuilder.defaultClient();
        String queueUrl = "https://sqs.us-west-2.amazonaws.com/765357718698/UpdateFeedQueue";

        while(followsPage.isHasMorePages()) {
            followsPage = followDAO.getPageofFollowers(status.getUser().getAlias(), 25, lastUserAlias);
            List<FollowsBean> followsBeans = followsPage.getValues();
            if (followsBeans.size() > 0) {
                lastUserAlias = followsBeans.get(followsBeans.size() - 1).getFollow_handle();
            }
            List<String> followers = new ArrayList<>();
            for (FollowsBean followsBean : followsBeans) {
                followers.add(followsBean.getFollow_handle());
            }

            UpdateFeedModel updateFeed = new UpdateFeedModel(followers, status);
            String post = (new Gson()).toJson(updateFeed);

            SendMessageRequest send_msg_request = new SendMessageRequest()
                    .withQueueUrl(queueUrl)
                    .withMessageBody(post);

            SendMessageResult send_msg_result = sqs.sendMessage(send_msg_request);
            System.out.println("message sent");

            String msgId = send_msg_result.getMessageId();
            System.out.println("Message ID: " + msgId);
        }
    }

    public void updateFeeds(String message) {
        UpdateFeedModel updateFeed = (new Gson()).fromJson(message, UpdateFeedModel.class);
        System.out.println("updating feeds");
        feedDAO.addFeedBatch(updateFeed.getFollowers(), updateFeed.getStatus());
        System.out.println("feeds updated");
    }

    public PostStatusResponse postStatus(PostStatusRequest request) {
        if (request.getStatus() == null) {
            throw new RuntimeException("[Bad Request] Request needs to have a status");
        }

        if (!checkAuthtoken(request.getAuthToken().getToken())) {
            return new PostStatusResponse("authtoken timeout");
        }

        Status status = request.getStatus();
        String senderAlias = status.getUser().getAlias();

        storyDAO.putStory(senderAlias, status.getTimestamp(), status.getPost(),
                status.getUrls(), status.getMentions());

        String post = (new Gson()).toJson(status);
        String queueUrl = "https://sqs.us-west-2.amazonaws.com/765357718698/PostStatusQueue";

        SendMessageRequest send_msg_request = new SendMessageRequest()
                .withQueueUrl(queueUrl)
                .withMessageBody(post);

        AmazonSQS sqs = AmazonSQSClientBuilder.defaultClient();
        sqs.sendMessage(send_msg_request);

        return new PostStatusResponse();
    }

    public StoryResponse getStory(StoryRequest request) {
        if(request.getTargetAlias() == null) {
            throw new RuntimeException("[Bad Request] Request needs to have a target alias");
        } else if(request.getLimit() <= 0) {
            throw new RuntimeException("[Bad Request] Request needs to have a positive limit");
        }

        if (!checkAuthtoken(request.getAuthToken().getToken())) {
            return new StoryResponse("authtoken timeout");
        }


        String timestamp;
        if (request.getLastStatus() == null) {
            timestamp = null;
        } else {
            timestamp = String.valueOf(request.getLastStatus().timestamp);
        }

        DataPage<StoryBean> storyPage = storyDAO.getPageofStory(request.getTargetAlias(),
                request.getLimit(), timestamp);

        List<Status> story = new ArrayList<>();

        for(StoryBean storyBean : storyPage.getValues()) {
            UserBean userBean = userDAO.getUser(storyBean.getSender_alias());
            User user = new User(userBean.getFirst_name(), userBean.getLast_name(),
                    userBean.getAlias(), userBean.getImageURL());
            Status status = new Status(storyBean.getPost(), user, storyBean.getTimestamp(),
                    storyBean.getUrls(), storyBean.getMentions());
            story.add(status);
        }

        return new StoryResponse(story, storyPage.isHasMorePages());
    }

    public FeedResponse getFeed(FeedRequest request) {
        if(request.getTargetAlias() == null) {
            throw new RuntimeException("[Bad Request] Request needs to have a target alias");
        } else if(request.getLimit() <= 0) {
            throw new RuntimeException("[Bad Request] Request needs to have a positive limit");
        }

        if (!checkAuthtoken(request.getAuthToken().getToken())) {
            return new FeedResponse("authtoken timeout");
        }

        String timestamp;
        if (request.getLastStatus() == null) {
            timestamp = null;
        } else {
            timestamp = String.valueOf(request.getLastStatus().timestamp);
        }

        DataPage<FeedBean> feedPage = feedDAO.getPageofFeed(request.getTargetAlias(),
                request.getLimit(), timestamp);

        List<Status> feed = new ArrayList<>();

        for(FeedBean feedBean : feedPage.getValues()) {
            UserBean userBean = userDAO.getUser(feedBean.getSender_alias());
            User user = new User(userBean.getFirst_name(), userBean.getLast_name(),
                    userBean.getAlias(), userBean.getImageURL());
            Status status = new Status(feedBean.getPost(), user, feedBean.getTimestamp(),
                    feedBean.getUrls(), feedBean.getMentions());
            feed.add(status);
        }

        return new FeedResponse(feed, feedPage.isHasMorePages());
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


}
