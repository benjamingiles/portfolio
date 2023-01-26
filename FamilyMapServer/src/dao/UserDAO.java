package dao;

import model.User;

import java.sql.*;

/**
 * The Data Access Object for User
 */
public class UserDAO {

  /**
   * the connection to the database it will use
   */
  private Connection conn;

  /**
   * Creates a UserDAO object with a connection to the database it will use
   * @param conn the connection to the database it will use
   */
  public UserDAO(Connection conn) {
    this.conn=conn;
  }

  /**
   * puts a user in the database
   *
   * @param user the user that is to be put into the database
   */
  public void createUser(User user) throws DataAccessException {
    String sql = "INSERT INTO user (username, password, email, firstName, lastName, gender, personID) " +
            "VALUES(?,?,?,?,?,?,?)";
    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
      stmt.setString(1, user.getUsername());
      stmt.setString(2, user.getPassword());
      stmt.setString(3, user.getEmail());
      stmt.setString(4, user.getFirstName());
      stmt.setString(5, user.getLastName());
      stmt.setString(6, user.getGender());
      stmt.setString(7, user.getPersonID());

      stmt.executeUpdate();
    } catch (SQLException e) {
      throw new DataAccessException("Error encountered while inserting into the database");
    }
  }

  /**
   * checks if the username and password match a user in the database
   *
   * @param username unique username for the user to be found
   * @param password user's password to be checked
   * @return true if the username and password match a user in the database and false if not
   */
  public boolean validate(String username, String password) throws DataAccessException {
    ResultSet rs = null;
    String sql = "SELECT * FROM user WHERE username = ? AND password = ?;";
    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
      stmt.setString(1, username);
      stmt.setString(2, password);
      rs = stmt.executeQuery();
      if (rs.next()) {
        return true;
      }
    } catch (SQLException e) {
      e.printStackTrace();
      throw new DataAccessException("Error encountered while finding user");
    } finally {
      if (rs != null) {
        try {
          rs.close();
        } catch (SQLException e) {
          e.printStackTrace();
        }
      }
    }
    return false;
  }

  /**
   * find a user in the database based off their username
   *
   * @param username Unique username to be found in database
   * @return the user if it can be found or null if it can't be found
   */
  public User getUser(String username) throws DataAccessException {
    User user;
    ResultSet rs = null;
    String sql = "SELECT * FROM user WHERE username = ?;";
    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
      stmt.setString(1, username);
      rs = stmt.executeQuery();
      if (rs.next()) {
        user = new User(rs.getString("username"), rs.getString("password"),
                rs.getString("email"), rs.getString("firstName"),
                rs.getString("lastName"), rs.getString("gender"),
                rs.getString("personID"));
        return user;
      }
    } catch (SQLException e) {
      e.printStackTrace();
      throw new DataAccessException("Error encountered while finding user");
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
   * Clears the user table
   *
   * @throws DataAccessException
   */
  public void clearTable() throws DataAccessException {
    try (Statement stmt = conn.createStatement()) {
      String sql = "DELETE FROM user";
      stmt.executeUpdate(sql);
    } catch (SQLException e) {
      throw new DataAccessException("SQL Error encountered while clearing table");
    }
  }

}
