package Service;

import Result.ClearResult;
import Result.RegisterResult;
import dao.AuthTokenDAO;
import dao.DataAccessException;
import dao.Database;
import dao.UserDAO;
import model.AuthToken;
import model.User;

/**
 * An object that represents the ClearService
 */
public class ClearService {

  /**
   * deletes all data from the database
   * @return if unsuccessful will return a clearResult object with an error message or a success message otherwise
   */
  public ClearResult clear() {
    Database db = new Database();
    try {
      db.openConnection();

      db.clearTables();

      db.closeConnection(true);

      return new ClearResult("Clear succeeded", true);

    } catch (DataAccessException e) {
      e.printStackTrace();

      try {
        db.closeConnection(false);
      } catch (DataAccessException ex) {
        ex.printStackTrace();
      }
      return new ClearResult("Error: Error clearing data", false);
    }
  }
}
