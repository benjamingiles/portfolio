package com.example.familymapclient.fragments.tasks;

import com.example.familymapclient.DataCache;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import model.Event;
import model.Person;

public class FillCacheHelper {
    public void fillDataCache(Person[] people, Event[] events) {
        DataCache dataCache = DataCache.getInstance();

        Map<String, List<Person>> childrenMap = new HashMap<>();

        for (Person person : people) {
            List<Event> personEvents = new ArrayList<>();
            dataCache.insertPerson(person);
            for (Event event : events) {
                if (event.getPersonID().equals(person.getPersonID())) {
                    personEvents.add(event);
                }
            }
            dataCache.insertPersonEvent(person.getPersonID(), personEvents);
            childrenMap.put(person.getPersonID(), new ArrayList<>());
        }

        for (Person person : people) {
            if (person.getFatherID() != null) {
                Objects.requireNonNull(childrenMap.get(person.getFatherID())).add(person);
            }
            if (person.getMotherID() != null) {
                Objects.requireNonNull(childrenMap.get(person.getMotherID())).add(person);
            }
            List<Event> personEvents = dataCache.getPersonEvents(person.getPersonID());
            personEvents = dataCache.sortEvents(personEvents);
            dataCache.insertPersonEvent(person.getPersonID(), personEvents);


        }

        dataCache.insertChildrenMap(childrenMap);

        for (Event event : events) {
            dataCache.insertEvent(event);
        }

        dataCache.setShowFamilyTreeLines(true);
        dataCache.setShowFathersSide(true);
        dataCache.setShowFemaleEvents(true);
        dataCache.setShowLifeStoryLines(true);
        dataCache.setShowMaleEvents(true);
        dataCache.setShowMothersSide(true);
        dataCache.setShowSpouseLines(true);
    }
}
