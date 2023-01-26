package Service;

import Fill.Fill;
import Request.RegisterRequest;
import Result.RegisterResult;
import dao.AuthTokenDAO;
import dao.DataAccessException;
import dao.Database;
import dao.UserDAO;
import model.AuthToken;
import model.Person;
import model.User;

import java.io.FileNotFoundException;
import java.util.UUID;

/**
 * An object that represents the RegisterService
 */
public class RegisterService {

  /**
   * Creates a new user account and generates 4 generations of ancestral data for user
   *
   * @param r the request to turn into a user
   * @return if successful will return the response body with authToken or an error
   */
  public RegisterResult register(RegisterRequest r) {
    Database db = new Database();
    try {
      db.openConnection();

      UserDAO userDAO = new UserDAO(db.getConnection());
      AuthTokenDAO authTokenDAO = new AuthTokenDAO(db.getConnection());
      String id = UUID.randomUUID().toString();
      User user = new User(r.getUsername(), r.getPassword(), r.getEmail(), r.getFirstName(), r.getLastName(), r.getGender(), id);
      userDAO.createUser(user);
      String token = UUID.randomUUID().toString();
      AuthToken authToken = new AuthToken(token, r.getUsername());
      authTokenDAO.createAuthToken(authToken);

      Person person = new Person(id, r.getUsername(), r.getFirstName(), r.getLastName(), r.getGender(), null, null, null);
      Fill fill = new Fill(r.getUsername(), db.getConnection(), person);
      fill.generatePerson(r.getGender(), 4, true, 2000);

      db.closeConnection(true);

      return new RegisterResult(authToken.getAuthToken(), user.getUsername(), user.getPersonID(), true);

    } catch (DataAccessException | FileNotFoundException e) {
      e.printStackTrace();

      try {
        db.closeConnection(false);
      } catch (DataAccessException ex) {
        ex.printStackTrace();
      }
      return new RegisterResult(false, "Error: Error registering user");
    }
  }
}
