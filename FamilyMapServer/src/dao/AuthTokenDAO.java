package dao;

import model.AuthToken;

import java.sql.*;

/**
 * The AuthToken Data Access Object
 */
public class AuthTokenDAO {

  /**
   * the connection object that we will use for our database
   */
  private Connection conn;

  /**
   * Creates an AuthTokenDAO object with a connection to a database
   * @param conn the connection object to the database it will use
   */
  public AuthTokenDAO(Connection conn) {
    this.conn=conn;
  }

  /**
   * add an AuthToken to the database
   * @param authToken the AuthToken object to be added to the database
   */
  public void createAuthToken(AuthToken authToken) throws DataAccessException {
    String sql = "INSERT INTO authtoken (authtoken, username) VALUES(?,?)";
    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
      stmt.setString(1, authToken.getAuthToken());
      stmt.setString(2, authToken.getUsername());

      stmt.executeUpdate();
    } catch (SQLException e) {
      throw new DataAccessException("Error encountered while inserting into the database");
    }
  }

  /**
   * find and return an AuthToken in the database
   * @param searchAuthToken the authToken that is to be found
   * @return the AuthToken if it can be found or null if it can't
   */
  public AuthToken getAuthToken(String searchAuthToken) throws DataAccessException {
    AuthToken authToken;
    ResultSet rs = null;
    String sql = "SELECT * FROM authtoken WHERE authtoken = ?;";
    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
      stmt.setString(1, searchAuthToken);
      rs = stmt.executeQuery();
      if (rs.next()) {
        authToken = new AuthToken(rs.getString("authtoken"), rs.getString("username"));
        return authToken;
      }
    } catch (SQLException e) {
      e.printStackTrace();
      throw new DataAccessException("Error encountered while finding authToken");
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
   * finds and returns the authToken for a specific user
   * @param username the username that will be used to search for an authToken
   * @return the authToken if it can be found for that user or null if it can't
   */
  public AuthToken findAuthTokenForUser(String username) throws DataAccessException {
    AuthToken authToken;
    ResultSet rs = null;
    String sql = "SELECT * FROM authtoken WHERE username = ?;";
    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
      stmt.setString(1, username);
      rs = stmt.executeQuery();
      if (rs.next()) {
        authToken = new AuthToken(rs.getString("authtoken"), rs.getString("username"));
        return authToken;
      }
    } catch (SQLException e) {
      throw new DataAccessException("Error encountered while finding authToken");
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
   * Clears the authToken Table
   * @throws DataAccessException
   */
  public void clearTable() throws DataAccessException {
    try (Statement stmt = conn.createStatement()){
      String sql = "DELETE FROM authtoken";
      stmt.executeUpdate(sql);
    } catch (SQLException e) {
      throw new DataAccessException("SQL Error encountered while clearing tables");
    }
  }
}
