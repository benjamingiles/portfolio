package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * An object that represents a database
 */
public class Database {
  private Connection conn;

  /**
   * opens up a connection to the database
   *
   * @return the open connection
   */
  public Connection openConnection() throws DataAccessException {
    try {
      //The Structure for this Connection is driver:language:path
      //The path assumes you start in the root of your project unless given a non-relative path
      final String CONNECTION_URL = "jdbc:sqlite:familymap.sqlite";

      // Open a database connection to the file given in the path
      conn = DriverManager.getConnection(CONNECTION_URL);

      // Start a transaction
      conn.setAutoCommit(false);
    } catch (SQLException e) {
      e.printStackTrace();
      throw new DataAccessException("Unable to open connection to database");
    }

    return conn;
  }

  public Connection getConnection() throws DataAccessException {
    if(conn == null) {
      return openConnection();
    } else {
      return conn;
    }
  }

  /**
   * closes the connection we opened and either commits changes or will rollback changes
   *
   * @param commit if true it commits changes and if false then rollback changes
   */
  public void closeConnection(boolean commit) throws DataAccessException {
    try {
      if (commit) {
        //This will commit the changes to the database
        conn.commit();
      } else {
        //If we find out something went wrong, pass a false into closeConnection and this
        //will rollback any changes we made during this connection
        conn.rollback();
      }

      conn.close();
      conn = null;
    } catch (SQLException e) {
      e.printStackTrace();
      throw new DataAccessException("Unable to close database connection");
    }
  }

  public void clearTables() throws DataAccessException {
    try (Statement stmt = conn.createStatement()){
      String sql = "DELETE FROM authtoken; DELETE FROM user; DELETE FROM event; DELETE FROM person";
      stmt.executeUpdate(sql);
    } catch (SQLException e) {
      throw new DataAccessException("SQL Error encountered while clearing tables");
    }
  }

}
