package com.example.familymapclient;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import model.Event;
import model.Person;

public class DataCache {

    private static DataCache instance = new DataCache();

    public static DataCache getInstance() {
        return instance;
    }

    private DataCache() {}

    private Map<String, Person> people = new HashMap<>();
    private List<Person> allPeople = new ArrayList<>();
    private Map<String, Event> events = new HashMap<>();
    private List<Event> allEvents = new ArrayList<>();
    private Map<String, List<Event>> personEvents = new HashMap<>();
    private Map<String, List<Person>> childrenMap = new HashMap<>();
    private List<Person> paternalAncestors = new ArrayList<>();
    private List<Person> maternalAncestors = new ArrayList<>();
    private String rootPersonID;

    public String getRootPersonID() {
        return rootPersonID;
    }

    public void setRootPersonID(String rootPersonID) {
        this.rootPersonID = rootPersonID;
    }

    private boolean showLifeStoryLines;
    private boolean showFamilyTreeLines;
    private boolean showSpouseLines;
    private boolean showFathersSide;
    private boolean showMothersSide;
    private boolean showMaleEvents;
    private boolean showFemaleEvents;

    public boolean isShowLifeStoryLines() {
        return showLifeStoryLines;
    }

    public void setShowLifeStoryLines(boolean showLifeStoryLines) {
        this.showLifeStoryLines = showLifeStoryLines;
    }

    public boolean isShowFamilyTreeLines() {
        return showFamilyTreeLines;
    }

    public void setShowFamilyTreeLines(boolean showFamilyTreeLines) {
        this.showFamilyTreeLines = showFamilyTreeLines;
    }

    public boolean isShowSpouseLines() {
        return showSpouseLines;
    }

    public void setShowSpouseLines(boolean showSpouseLines) {
        this.showSpouseLines = showSpouseLines;
    }

    public boolean isShowFathersSide() {
        return showFathersSide;
    }

    public void setShowFathersSide(boolean showFathersSide) {
        this.showFathersSide = showFathersSide;
    }

    public boolean isShowMothersSide() {
        return showMothersSide;
    }

    public void setShowMothersSide(boolean showMothersSide) {
        this.showMothersSide = showMothersSide;
    }

    public boolean isShowMaleEvents() {
        return showMaleEvents;
    }

    public void setShowMaleEvents(boolean showMaleEvents) {
        this.showMaleEvents = showMaleEvents;
    }

    public boolean isShowFemaleEvents() {
        return showFemaleEvents;
    }

    public void setShowFemaleEvents(boolean showFemaleEvents) {
        this.showFemaleEvents = showFemaleEvents;
    }

    public Person getPerson(String personID) {
        return people.get(personID);
    }

    public Event getEvent(String eventID) { return events.get(eventID); }

    public List<Event> getEvents() {
        return allEvents;
    }

    public List<Event> getPersonEvents(String personID) { return personEvents.get(personID); }

    public List<Person> getChildren(String personID) { return childrenMap.get(personID); }

    public void insertPerson(Person person) {

        people.put(person.getPersonID(), person);
        allPeople.add(person);
    }

    public void insertEvent(Event event) {

        events.put(event.getEventID(), event);
        allEvents.add(event);
    }

    public void insertPersonEvent(String personID, List<Event> eventsForPerson) {
        personEvents.put(personID, eventsForPerson);
    }

    public void insertChildrenMap(Map<String, List<Person>> children) {
        childrenMap = children;
    }

    public void insertPaternalAncestors(List<Person> ancestors) {
        paternalAncestors = ancestors;
    }

    public void insertMaternalAncestors(List<Person> ancestors) {
        maternalAncestors = ancestors;
    }

    public List<Event> sortEvents(List<Event> eventList) {
        List<Event> sortedEvents = new ArrayList<>();

        Event birthEvent = null;
        Event deathEvent = null;

        for (Event event : eventList) {
            if (event.getEventType().toLowerCase(Locale.ROOT).equals("birth")) {
                birthEvent = event;
            }

            if (event.getEventType().toLowerCase(Locale.ROOT).equals("death")) {
                deathEvent = event;
            }
        }

        if (birthEvent != null) {
            eventList.remove(birthEvent);
        }
        if (deathEvent != null) {
            eventList.remove(deathEvent);
        }

        int numEvents = eventList.size();
        for (int i = 0; i < numEvents; ++i) {
            int minValue = 0;
            int minIndex = 0;

            for (int j = 0; j < eventList.size(); ++j) {
                if (j == 0) {
                    minIndex = j;
                    minValue = eventList.get(j).getYear();
                }

                else {
                    if (eventList.get(j).getYear() < minValue) {
                        minIndex = j;
                        minValue = eventList.get(j).getYear();
                    }
                    else if (eventList.get(j).getYear() == minValue) {
                        String[] temp = {eventList.get(j).getEventType().toLowerCase(Locale.ROOT),
                                eventList.get(minIndex).getEventType().toLowerCase(Locale.ROOT)};
                        Arrays.sort(temp);

                        if (temp[0].equals(eventList.get(j).getEventType().toLowerCase(Locale.ROOT))) {
                            minIndex = j;
                            minValue = eventList.get(j).getYear();
                        }
                    }
                }
            }
            sortedEvents.add(eventList.get(minIndex));
            eventList.remove(minIndex);
        }

        if (deathEvent != null) {
            sortedEvents.add(deathEvent);
        }
        if (birthEvent != null) {
            sortedEvents.add(0, birthEvent);
        }

        return sortedEvents;
    }

    public List<Person> findAncestors(Person ancestor) {
        List<Person> ancestors = new ArrayList<>();
        ancestors = findAncestorsHelper(ancestor, ancestors);
        return ancestors;
    }

    public List<Person> findAncestorsHelper(Person ancestor, List<Person> ancestors) {
        if (ancestor != null) {
            ancestors.add(ancestor);
            ancestors = findAncestorsHelper(getPerson(ancestor.getFatherID()), ancestors);
            ancestors = findAncestorsHelper(getPerson(ancestor.getMotherID()), ancestors);
        }
        return ancestors;
    }

    public List<Person> searchPeople(String search) {
        search = search.toLowerCase(Locale.ROOT);
        List<Person> foundPeople = new ArrayList<>();

        for (Person person : allPeople) {
            if (person.getFirstName().toLowerCase(Locale.ROOT).contains(search) ||
                    person.getLastName().toLowerCase(Locale.ROOT).contains(search)) {
                foundPeople.add(person);
            }
        }

        return foundPeople;
    }

    public List<Event> searchEvents(String search) {
        search = search.toLowerCase(Locale.ROOT);
        List<Event> foundEvents = new ArrayList<>();

        for (Event event : allEvents) {
            Person person = getPerson(event.getPersonID());
            if (!isShowMaleEvents() && person.getGender().toLowerCase(Locale.ROOT).equals("m")){
                continue;
            }
            if (!isShowFemaleEvents() && person.getGender().toLowerCase(Locale.ROOT).equals("f")){
                continue;
            }
            if (!canMarkFromFamilyFilter(person)) {
                continue;
            }
            String year = Integer.toString(event.getYear());
            if (event.getEventType().toLowerCase(Locale.ROOT).contains(search) ||
                    event.getCountry().toLowerCase(Locale.ROOT).contains(search)
                    || event.getCity().toLowerCase(Locale.ROOT).contains(search) ||
                    year.toLowerCase(Locale.ROOT).contains(search)) {
                foundEvents.add(event);
            }
        }

        return foundEvents;
    }

    public boolean canMarkFromFamilyFilter(Person person) {
        DataCache dataCache = DataCache.getInstance();
        boolean markEvent = true;

        if (!dataCache.isShowFathersSide()) {
            for (Person ancestor : paternalAncestors) {
                if (ancestor.getPersonID().equals(person.getPersonID())) {
                    markEvent = false;
                    break;
                }
            }
        }

        if (!dataCache.isShowMothersSide()) {
            for (Person ancestor : maternalAncestors) {
                if (ancestor.getPersonID().equals(person.getPersonID())) {
                    markEvent = false;
                }
            }
        }

        return markEvent;
    }
}
