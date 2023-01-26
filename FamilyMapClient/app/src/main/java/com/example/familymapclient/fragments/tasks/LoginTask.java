package com.example.familymapclient.fragments.tasks;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.example.familymapclient.DataCache;
import com.example.familymapclient.ServerProxy;

import java.util.List;

import Request.LoginRequest;
import Result.LoginResult;
import model.Event;
import model.Person;

public class LoginTask implements Runnable {

    private final Handler messageHandler;
    private final LoginRequest loginRequest;
    private final String serverHost;
    private final String serverPort;

    public LoginTask(Handler messageHandler, LoginRequest loginRequest, String serverHost,
                     String serverPort) {
        this.messageHandler = messageHandler;
        this.loginRequest = loginRequest;
        this.serverPort = serverPort;
        this.serverHost = serverHost;
    }

    @Override
    public void run() {
        ServerProxy serverProxy = new ServerProxy(serverHost, serverPort);
        LoginResult loginResult = serverProxy.login(loginRequest);

        if (loginResult.isSuccess()) {
            DataCache dataCache = DataCache.getInstance();

            Person[] people = serverProxy.getPeople(loginResult.getAuthToken()).getData();
            Event[] events = serverProxy.getEvents(loginResult.getAuthToken()).getData();
            FillCacheHelper fillCacheHelper = new FillCacheHelper();
            fillCacheHelper.fillDataCache(people, events);


            Person rootPerson = dataCache.getPerson(loginResult.getPersonID());

            dataCache.setRootPersonID(loginResult.getPersonID());
            List<Person> paternalAncestors = dataCache.findAncestors(dataCache.getPerson(rootPerson.getFatherID()));
            List<Person> maternalAncestors = dataCache.findAncestors(dataCache.getPerson(rootPerson.getMotherID()));
            dataCache.insertPaternalAncestors(paternalAncestors);
            dataCache.insertMaternalAncestors(maternalAncestors);
            sendMessage(loginResult.getAuthToken(), loginResult.isSuccess(),
                    loginResult.getPersonID());
        } else {
            sendMessage("", loginResult.isSuccess(), "");
        }
    }

    private void sendMessage(String authToken, boolean success, String personID) {
        Message message = Message.obtain();

        Bundle messageBundle = new Bundle();
        messageBundle.putBoolean("success", success);
        messageBundle.putString("authToken", authToken);
        messageBundle.putString("personID", personID);
        message.setData(messageBundle);

        messageHandler.sendMessage(message);
    }

}
