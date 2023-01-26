package Service;

import Result.MultiplePersonResult;
import dao.*;
import model.AuthToken;
import model.Person;

import java.sql.Connection;

/**
 * An object that represents the MultiplePersonService
 */
public class MultiplePersonService {
  /**
   * AuthToken for current user
   */
  private String authToken;

  /**
   * Creates a MultiplePersonService object with the current user's authToken
   * @param authToken AuthToken for current user
   */
  public MultiplePersonService(String authToken) {
    this.authToken = authToken;
  }

  /**
   * Returns all family members of the current user
   * @return an error if unsuccessful or an array of Persons if successful
   */
  public MultiplePersonResult personService() {
    Database db = new Database();
    try {
      Connection conn = db.getConnection();

      PersonDAO personDAO = new PersonDAO(conn);
      AuthTokenDAO authTokenDAO = new AuthTokenDAO(conn);

      if (authTokenDAO.getAuthToken(authToken) == null) {
        db.closeConnection(false);
        return new MultiplePersonResult( "Error: AuthToken is invalid", false);
      }

      AuthToken userToken = authTokenDAO.getAuthToken(authToken);

      Person[] people = personDAO.getAllPeople(userToken.getUsername());

      db.closeConnection(true);
      return new MultiplePersonResult(people, true);

    } catch (DataAccessException e) {
      e.printStackTrace();

      try {
        db.closeConnection(false);
      } catch (DataAccessException ex) {
        ex.printStackTrace();
        return new MultiplePersonResult("Error: Error finding person", false);
      }
      return new MultiplePersonResult("Error: Error finding person", false);
    }
  }
}
