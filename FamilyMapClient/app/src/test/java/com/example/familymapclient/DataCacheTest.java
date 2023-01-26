package com.example.familymapclient;

import static org.junit.jupiter.api.Assertions.*;

import com.example.familymapclient.fragments.tasks.FillCacheHelper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import Request.LoginRequest;
import Result.LoginResult;
import model.Event;
import model.Person;

public class DataCacheTest {

    ServerProxy serverProxy;
    LoginRequest loginRequest;
    LoginResult loginResult;
    FillCacheHelper fillCacheHelper;
    DataCache dataCache;

    @BeforeEach
    public void setUp() {
        serverProxy = new ServerProxy("localhost", "8080");
        loginRequest = new LoginRequest("sheila", "parker");
        fillCacheHelper = new FillCacheHelper();
        loginResult = serverProxy.login(loginRequest);
        fillCacheHelper.fillDataCache(serverProxy.getPeople(loginResult.getAuthToken()).getData(),
                serverProxy.getEvents(loginResult.getAuthToken()).getData());
        dataCache = DataCache.getInstance();
        dataCache.setRootPersonID(loginResult.getPersonID());
    }

    @Test
    public void testCanMarkFromFamilyFilterPass() {
        Person person = dataCache.getPerson(loginResult.getPersonID());
        assertTrue(dataCache.canMarkFromFamilyFilter(person));
        dataCache.setShowMothersSide(false);
        assertTrue(dataCache.canMarkFromFamilyFilter(person));
        dataCache.setShowFathersSide(false);
        assertTrue(dataCache.canMarkFromFamilyFilter(person));
    }

    @Test
    public void testCanMarkFromFamilyFilterFail() {
        Person person = dataCache.getPerson("Betty_White");
        assertTrue(dataCache.canMarkFromFamilyFilter(person));
        dataCache.setShowFathersSide(false);
        assertTrue(dataCache.canMarkFromFamilyFilter(person));
        dataCache.setShowMothersSide(false);
        assertFalse(dataCache.canMarkFromFamilyFilter(person));
    }

    @Test
    public void testSearchEventsPass() {
        String searchString = "completed asteroids";
        List<Event> events = dataCache.searchEvents(searchString);
        assertTrue(events.size() > 0);
        dataCache.setShowMothersSide(false);
        events = dataCache.searchEvents(searchString);
        assertTrue(events.size() > 0);
        dataCache.setShowFathersSide(false);
        events = dataCache.searchEvents(searchString);
        assertTrue(events.size() > 0);
    }

    @Test
    public void testSearchEventsFail() {
        String searchString = "completed asteroids";
        dataCache.setShowFemaleEvents(false);
        List<Event> events = dataCache.searchEvents(searchString);
        assertTrue(events.size() == 0);
    }

    @Test
    public void testSearchPeoplePass() {
        String searchString = "Sheila";
        List<Person> people = dataCache.searchPeople(searchString);
        assertTrue(people.size() > 0);
    }

    @Test
    public void testSearchPeopleFail() {
        String searchString = "Shiela";
        List<Person> people = dataCache.searchPeople(searchString);
        assertTrue(people.size() == 0);
    }

    @Test
    public void testSortEventsPass() {
        List<Event> events = dataCache.getPersonEvents("Sheila_Parker");
        events = dataCache.sortEvents(events);
        String expectedFirstEvent = "birth";
        assertEquals(expectedFirstEvent, events.get(0).getEventType());
    }

    @Test
    public void testSortEventsFail() {
        List<Event> events = new ArrayList<>();
        events = dataCache.sortEvents(events);
        assertTrue(events.size() == 0);
    }

}
