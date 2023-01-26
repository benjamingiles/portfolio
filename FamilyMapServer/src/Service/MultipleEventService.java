package Service;

import Result.MultipleEventResult;
import Result.MultiplePersonResult;
import dao.*;
import model.AuthToken;
import model.Event;
import model.Person;

/**
 * An object that represents the MultipleEventService
 */
public class MultipleEventService {
  /**
   * AuthToken for current user
   */
  private String authToken;

  /**
   * Creates a MultipleEventService object with the current user's authToken
   * @param authToken AuthToken for current user
   */
  public MultipleEventService(String authToken) {
    this.authToken = authToken;
  }

  /**
   * Returns all events for all family members of the current user
   * @return an error if unsuccessful or an array of Events if successful
   */
  public MultipleEventResult eventService() {
    Database db = new Database();
    try {
      db.openConnection();

      EventDAO eventDAO = new EventDAO(db.getConnection());
      AuthTokenDAO authTokenDAO = new AuthTokenDAO(db.getConnection());

      if (authTokenDAO.getAuthToken(authToken) == null) {
        db.closeConnection(false);
        return new MultipleEventResult( "Error: AuthToken is invalid", false);
      }

      AuthToken userToken = authTokenDAO.getAuthToken(authToken);

      Event[] events = eventDAO.getAllEvents(userToken.getUsername());

      db.closeConnection(true);
      return new MultipleEventResult(events, true);

    } catch (DataAccessException e) {
      e.printStackTrace();

      try {
        db.closeConnection(false);
      } catch (DataAccessException ex) {
        ex.printStackTrace();
        return new MultipleEventResult("Error: Error finding events", false);
      }
      return new MultipleEventResult("Error: Error finding events", false);
    }
  }
}
