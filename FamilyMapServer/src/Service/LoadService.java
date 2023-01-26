package Service;

import Request.LoadRequest;
import Result.ClearResult;
import Result.LoadResult;
import dao.*;
import model.Event;
import model.Person;
import model.User;

/**
 * An object that represents the LoadService
 */
public class LoadService {
  /**
   * clears all data from the database and loads in the data given
   *
   * @param r LoadRequest object holding arrays of users, persons, and events to be loaded
   * @return will return an error if unsuccessful or a success message
   */
  public LoadResult load(LoadRequest r) {
    Database db = new Database();
    try {
      db.openConnection();
      db.clearTables();

      User[] users = r.getUsers();
      Person[] people = r.getPersons();
      Event[] events = r.getEvents();

      UserDAO userDAO = new UserDAO(db.getConnection());
      PersonDAO personDAO = new PersonDAO(db.getConnection());
      EventDAO eventDAO = new EventDAO(db.getConnection());

      for (int i = 0; i < users.length; ++i) {
        userDAO.createUser(users[i]);
      }
      for (int i = 0; i < people.length; ++i) {
        personDAO.createPerson(people[i]);
      }
      for (int i = 0; i < events.length; ++i) {
        eventDAO.createEvent(events[i]);
      }

      db.closeConnection(true);

      return new LoadResult("Successfully added " + r.getUsers().length + " users, " +
              r.getPersons().length + " persons, and " + r.getEvents().length + " events to the database.", true);

    } catch (DataAccessException e) {
      e.printStackTrace();

      try {
        db.closeConnection(false);
      } catch (DataAccessException ex) {
        ex.printStackTrace();
      }
      return new LoadResult("Error: Error loading data", false);
    }
  }
}
