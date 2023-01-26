package Service;

import Request.SinglePersonRequest;
import Result.SinglePersonResult;
import dao.*;
import model.AuthToken;
import model.Person;

/**
 * An object that represents the SinglePersonService
 */
public class SinglePersonService {

  /**
   * authToken of current user
   */
  private String authToken;

  /**
   * creates a Service object with the authToken
   * @param authToken authToken of current user
   */
  public SinglePersonService(String authToken) {
    this.authToken = authToken;
  }

  /**
   * returns a single person object with the specified ID if they are associated with user
   *
   * @param r Request with the personID to be found
   * @return error if person can't be found or a Result with person's information
   */
  public SinglePersonResult singlePerson(SinglePersonRequest r) {
    Database db = new Database();
    try {
      db.openConnection();

      PersonDAO personDAO = new PersonDAO(db.getConnection());
      AuthTokenDAO authTokenDAO = new AuthTokenDAO(db.getConnection());

      if (authTokenDAO.getAuthToken(authToken) == null) {
        db.closeConnection(false);
        return new SinglePersonResult( "Error: AuthToken is invalid", false);
      }

      AuthToken token = authTokenDAO.getAuthToken(authToken);

      Person person = personDAO.getPerson(r.getPersonID());

      if (person == null) {
        db.closeConnection(false);
        return new SinglePersonResult( "Error: PersonID is invalid", false);
      }

      if (!person.getAssociatedUsername().equals(token.getUsername())) {
        db.closeConnection(false);
        return new SinglePersonResult("Error: Person ID is not associated with this authtoken", false);
      }

      db.closeConnection(true);
      return new SinglePersonResult(person.getAssociatedUsername(), person.getPersonID(), person.getFirstName(),
                                    person.getLastName(), person.getGender(), person.getFatherID(), person.getMotherID(),
                                    person.getSpouseID(), true);

    } catch (DataAccessException e) {
      e.printStackTrace();

      try {
        db.closeConnection(false);
      } catch (DataAccessException ex) {
        ex.printStackTrace();
      }
      return new SinglePersonResult("Error: Error finding person", false);
    }
  }
}
