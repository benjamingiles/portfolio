package com.example.familymapclient;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.UUID;

import Request.LoginRequest;
import Request.RegisterRequest;
import Result.LoginResult;
import Result.MultipleEventResult;
import Result.MultiplePersonResult;
import Result.RegisterResult;

public class ServerProxyTest {

    ServerProxy serverProxy;
    LoginRequest loginRequest;
    RegisterRequest registerRequest;

    @BeforeEach
    public void setUp() {
        serverProxy = new ServerProxy("localhost", "8080");
        loginRequest = new LoginRequest("sheila", "parker");
        UUID uuid = UUID.randomUUID();
        String username = uuid.toString();
        registerRequest = new RegisterRequest(username, "fake", "email", "Ben",
                "Giles", "m");
    }

    @Test
    public void testLoginPass() {
        LoginResult loginResult = serverProxy.login(loginRequest);
        assertTrue(loginResult.isSuccess());
    }

    @Test
    public void testLoginFail() {
        LoginRequest failRequest = new LoginRequest("null", "null");
        LoginResult loginResult = serverProxy.login(failRequest);
        assertFalse(loginResult.isSuccess());
    }

    @Test
    public void testRegisterPass() {
        RegisterResult registerResult = serverProxy.register(registerRequest);
        assertTrue(registerResult.isSuccess());
    }

    @Test
    public void testRegisterFail() {
        RegisterRequest failRequest = new RegisterRequest("sheila", "fake",
                "email", "fake", "fake", "m");
        RegisterResult registerResult = serverProxy.register(failRequest);
        assertFalse(registerResult.isSuccess());
    }

    @Test
    public void testGetPeoplePass() {
        LoginResult loginResult = serverProxy.login(loginRequest);
        String token = loginResult.getAuthToken();
        MultiplePersonResult multiplePersonResult = serverProxy.getPeople(token);
        assertTrue(multiplePersonResult.isSuccess());
    }

    @Test
    public void testGetPeopleFail() {
        String token = "null";
        MultiplePersonResult multiplePersonResult = serverProxy.getPeople(token);
        assertFalse(multiplePersonResult.isSuccess());
    }

    @Test
    public void testGetEventsPass() {
        LoginResult loginResult = serverProxy.login(loginRequest);
        String token = loginResult.getAuthToken();
        MultipleEventResult multipleEventResult = serverProxy.getEvents(token);
        assertTrue(multipleEventResult.isSuccess());
    }

    @Test
    public void testGetEventsFail() {
        String token = "null";
        MultipleEventResult multipleEventResult = serverProxy.getEvents(token);
        assertFalse(multipleEventResult.isSuccess());
    }
}