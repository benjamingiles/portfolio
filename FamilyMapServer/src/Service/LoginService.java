package Service;

import Request.LoginRequest;
import Result.LoginResult;
import dao.AuthTokenDAO;
import dao.DataAccessException;
import dao.Database;
import dao.UserDAO;
import model.AuthToken;
import model.User;

import java.util.UUID;

/**
 * An object that represents the LoginService
 */
public class LoginService {

  /**
   * logs in the user and returns an authToken
   *
   * @param r the request to login
   * @return if successful it will return a response with an authToken or an error if unsuccessful
   */
  public LoginResult login(LoginRequest r) {
    Database db = new Database();
    try {
      db.openConnection();

      UserDAO userDAO = new UserDAO(db.getConnection());
      AuthTokenDAO authTokenDAO = new AuthTokenDAO(db.getConnection());
      if (userDAO.validate(r.getUsername(), r.getPassword())) {
        User user = userDAO.getUser(r.getUsername());
        UUID uuid = UUID.randomUUID();
        String token = uuid.toString();
        AuthToken authToken = new AuthToken(token, r.getUsername());
        authTokenDAO.createAuthToken(authToken);

        db.closeConnection(true);

        return new LoginResult(authToken.getAuthToken(), user.getUsername(), user.getPersonID(), true);
      } else {
        db.closeConnection(false);
        return new LoginResult(false, "Error: could not validate user");
      }

    } catch (DataAccessException e) {
      e.printStackTrace();

      try {
        db.closeConnection(false);
      } catch (DataAccessException ex) {
        ex.printStackTrace();
      }
      return new LoginResult(false, "Error: Error logging in user");
    }
  }
}
