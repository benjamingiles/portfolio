package com.example.familymapclient.fragments.tasks;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.example.familymapclient.DataCache;
import com.example.familymapclient.ServerProxy;

import java.util.List;

import Request.RegisterRequest;
import Result.RegisterResult;
import model.Event;
import model.Person;

public class RegisterTask implements Runnable {

    private final Handler messageHandler;
    private final RegisterRequest registerRequest;
    private final String serverHost;
    private final String serverPort;

    public RegisterTask(Handler messageHandler, RegisterRequest registerRequest, String serverHost,
                        String serverPort) {
        this.messageHandler = messageHandler;
        this.registerRequest = registerRequest;
        this.serverPort = serverPort;
        this.serverHost = serverHost;
    }

    @Override
    public void run() {
        ServerProxy serverProxy = new ServerProxy(serverHost, serverPort);
        RegisterResult registerResult = serverProxy.register(registerRequest);

        if (registerResult.isSuccess()) {
            DataCache dataCache = DataCache.getInstance();

            Person[] people = serverProxy.getPeople(registerResult.getAuthtoken()).getData();
            Event[] events = serverProxy.getEvents(registerResult.getAuthtoken()).getData();
            FillCacheHelper fillCacheHelper = new FillCacheHelper();
            fillCacheHelper.fillDataCache(people, events);

            Person rootPerson = dataCache.getPerson(registerResult.getPersonID());

            dataCache.setRootPersonID(registerResult.getPersonID());
            List<Person> paternalAncestors = dataCache.findAncestors(dataCache.getPerson(rootPerson.getFatherID()));
            List<Person> maternalAncestors = dataCache.findAncestors(dataCache.getPerson(rootPerson.getMotherID()));
            dataCache.insertPaternalAncestors(paternalAncestors);
            dataCache.insertMaternalAncestors(maternalAncestors);

            sendMessage(registerResult.getAuthtoken(), registerResult.isSuccess(),
                    registerResult.getPersonID());
        } else {
            sendMessage("", registerResult.isSuccess(), "");
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