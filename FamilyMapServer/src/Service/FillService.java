package Service;

import Fill.Fill;
import Request.FillRequest;
import Result.FillResult;
import dao.*;
import model.Person;
import model.User;

import java.io.FileNotFoundException;
import java.util.UUID;

/**
 * An object that represents the FillService
 */
public class FillService {

  /**
   * populates the server's database with generated data for the specified username
   *
   * @param r the request with the username and number of generations
   * @return an error message if unsuccessful or a success message otherwise
   */
  public FillResult fill(FillRequest r) {
    Database db = new Database();
    try {
      db.openConnection();

      UserDAO userDAO = new UserDAO(db.getConnection());
      PersonDAO personDAO = new PersonDAO(db.getConnection());
      EventDAO eventDAO = new EventDAO(db.getConnection());

      if (userDAO.getUser(r.getUsername()) == null) {
        db.closeConnection(false);
        return new FillResult("Error: username not in the database", false);
      }

      if (r.getGenerations() < 0) {
        db.closeConnection(false);
        return new FillResult("Error: generations cannot be less than zero", false);
      }

      personDAO.deleteAllPeople(r.getUsername());
      eventDAO.deleteAllEvents(r.getUsername());

      String id = UUID.randomUUID().toString();
      User user = userDAO.getUser(r.getUsername());

      Person person = new Person(id, r.getUsername(), user.getFirstName(), user.getLastName(), user.getGender(), null, null, null);
      Fill fill = new Fill(r.getUsername(), db.getConnection(), person);
      fill.generatePerson(user.getGender(), r.getGenerations(), true, 2000);

      db.closeConnection(true);

      return new FillResult("Successfully added " + fill.getPersonCount() + " persons and " + fill.getEventCount()
              + " events to the database", true);

    } catch (DataAccessException | FileNotFoundException e) {
      e.printStackTrace();

      try {
        db.closeConnection(false);
      } catch (DataAccessException ex) {
        ex.printStackTrace();
      }
      return new FillResult("Error: Error filling data", false);
    }
  }
}
