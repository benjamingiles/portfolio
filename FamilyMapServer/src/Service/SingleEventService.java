package Service;

import Request.SingleEventRequest;
import Result.SingleEventResult;
import dao.*;
import model.AuthToken;
import model.Event;
import model.Person;


/**
 * An object that represents the SingleEventService
 */
public class SingleEventService {

  /**
   * authToken of current user
   */
  private String authToken;

  /**
   * creates a Service object with the authToken
   * @param authToken authToken of current user
   */
  public SingleEventService(String authToken) {
    this.authToken = authToken;
  }

  /**
   * returns a single event object with the specified ID if it is associated with user
   *
   * @param r Request with the eventID to be found
   * @return error if event can't be found or a Result with event's information
   */
  public SingleEventResult singleEvent(SingleEventRequest r) {
    Database db = new Database();
    try {
      db.openConnection();

      EventDAO eventDAO = new EventDAO(db.getConnection());
      AuthTokenDAO authTokenDAO = new AuthTokenDAO(db.getConnection());
      PersonDAO personDAO = new PersonDAO(db.getConnection());

      if (authTokenDAO.getAuthToken(authToken) == null) {
        db.closeConnection(false);
        return new SingleEventResult( "Error: AuthToken is invalid", false);
      }

      Event event = eventDAO.getEvent(r.getEventID());
      AuthToken token = authTokenDAO.getAuthToken(authToken);

      if (event == null) {
        db.closeConnection(false);
        return new SingleEventResult( "Error: EventID is invalid", false);
      }

      Person person = personDAO.getPerson(event.getPersonID());

      if (!person.getAssociatedUsername().equals(token.getUsername())) {
        db.closeConnection(false);
        return new SingleEventResult("Error: EventID is not connected to this authtoken", false);
      }

      db.closeConnection(true);
      return new SingleEventResult(event.getEventID(), event.getUsername(), event.getPersonID(), event.getLatitude(),
              event.getLongitude(), event.getCountry(), event.getCity(), event.getEventType(), event.getYear(), true);

    } catch (DataAccessException e) {
      e.printStackTrace();

      try {
        db.closeConnection(false);
      } catch (DataAccessException ex) {
        ex.printStackTrace();
      }
      return new SingleEventResult("Error: Error finding event", false);
    }
  }
}
