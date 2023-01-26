package dao;

import model.Person;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * The Person Data Access Object
 */
public class PersonDAO {

  /**
   * the connection to the database it will use
   */
  private Connection conn;

  /**
   * Creates a PersonDAO object with a connection to the database it will use
   * @param conn the connection to the database it will use
   */
  public PersonDAO(Connection conn) {
    this.conn=conn;
  }

  /**
   * create a person in the database
   *
   * @param person the person object to be put into the database
   */
  public void createPerson(Person person) throws DataAccessException {
    String sql = "INSERT INTO person (personID, associatedUsername, firstName, lastName, gender, fatherID," +
            "motherID, spouseID) VALUES(?,?,?,?,?,?,?,?)";
    try(PreparedStatement stmt = conn.prepareStatement(sql)) {
      stmt.setString(1, person.getPersonID());
      stmt.setString(2, person.getAssociatedUsername());
      stmt.setString(3, person.getFirstName());
      stmt.setString(4, person.getLastName());
      stmt.setString(5, person.getGender());
      stmt.setString(6, person.getFatherID());
      stmt.setString(7, person.getMotherID());
      stmt.setString(8, person.getSpouseID());

      stmt.executeUpdate();

    } catch (SQLException e) {
      throw new DataAccessException("Error encountered while inserting into the database");
    }
  }

  /**
   * find a person in the database based off their ID
   *
   * @param personID unique ID for the person to be found
   * @return the person if they can be found or null if not
   */
  public Person getPerson(String personID) throws DataAccessException {
    Person person;
    ResultSet rs = null;
    String sql = "SELECT * FROM person WHERE personID = ?;";
    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
      stmt.setString(1, personID);
      rs = stmt.executeQuery();
      if (rs.next()) {
        person = new Person(rs.getString("personID"), rs.getString("associatedUsername"),
                rs.getString("firstName"), rs.getString("lastName"), rs.getString("gender"),
                rs.getString("fatherID"), rs.getString("motherID"), rs.getString("spouseID"));
        return person;
      }
    } catch (SQLException e) {
      e.printStackTrace();
      throw new DataAccessException("Error encountered while finding person");
    } finally {
      if (rs != null) {
        try {
          rs.close();
        } catch (SQLException e) {
          e.printStackTrace();
        }
      }
    }
    return null;
  }

  /**
   * returns all the family members of the current user
   *
   * @param username the unique username that family members are associated with
   * @return list of family members
   * @throws DataAccessException
   */
  public Person[] getAllPeople(String username) throws DataAccessException {
    Person person;
    List<Person> people = new ArrayList<>();
    ResultSet rs = null;
    String sql = "SELECT * FROM person WHERE associatedUsername = ?;";
    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
      stmt.setString(1, username);
      rs = stmt.executeQuery();
      while (rs.next()) {
        person = new Person(rs.getString("personID"), rs.getString("associatedUsername"),
                rs.getString("firstName"), rs.getString("lastName"), rs.getString("gender"),
                rs.getString("fatherID"), rs.getString("motherID"), rs.getString("spouseID"));
        people.add(person);
      }
      return people.toArray(new Person[0]);
    } catch (SQLException e) {
      e.printStackTrace();
      throw new DataAccessException("Error encountered while finding person");
    } finally {
      if (rs != null) {
        try {
          rs.close();
        } catch (SQLException e) {
          e.printStackTrace();
          throw new DataAccessException("Error encountered while finding person");
        }
      }
    }
  }

  public void updateSpouseID(String personID, String spouseID) throws DataAccessException {
    String sql = "UPDATE person SET spouseID = ? WHERE personID = ?";
    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
      stmt.setString(1, spouseID);
      stmt.setString(2, personID);

      stmt.executeUpdate();
    } catch (SQLException e) {
      throw new DataAccessException("Error encountered while editing spouseID");
    }
  }

  /**
   * clears the person table
   */
  public void clearTable() throws DataAccessException {
    try (Statement stmt = conn.createStatement()){
      String sql = "DELETE FROM person";
      stmt.executeUpdate(sql);
    } catch (SQLException e) {
      throw new DataAccessException("SQL Error encountered while clearing tables");
    }
  }

  public void deleteAllPeople(String username) throws DataAccessException {
    String sql = "DELETE FROM person WHERE associatedUsername = ?;";
    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
      stmt.setString(1, username);
      stmt.executeUpdate();
    } catch (SQLException e) {
      e.printStackTrace();
      throw new DataAccessException("Error encountered while finding person");
    }
  }
}
