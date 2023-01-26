package Request;

import model.Event;
import model.Person;
import model.User;

/**
 * An object that represents a LoadRequest
 */
public class LoadRequest {
  /**
   * a json array of User objects
   */
  private User[] users;
  /**
   * a json array of Person objects
   */
  private Person[] persons;
  /**
   * a json array of Event objects
   */
  private Event[] events;

  /**
   * Creates a LoadRequest with arrays of users, persons, and events
   *
   * @param users json array of User objects
   * @param persons json array of Person objects
   * @param events json array of Event objects
   */
  public LoadRequest(User[] users, Person[] persons, Event[] events) {
    this.users = users;
    this.persons = persons;
    this.events = events;
  }

  public User[] getUsers() {
    return users;
  }

  public void setUsers(User[] users) {
    this.users = users;
  }

  public Person[] getPersons() {
    return persons;
  }

  public void setPersons(Person[] persons) {
    this.persons = persons;
  }

  public Event[] getEvents() {
    return events;
  }

  public void setEvents(Event[] events) {
    this.events = events;
  }
}
