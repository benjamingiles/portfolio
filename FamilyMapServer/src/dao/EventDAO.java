package dao;

import model.Event;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * The Event Data Access Object
 */
public class EventDAO {

  /**
   * the connection object to the database
   */
  private Connection conn;

  /**
   * creates an EventDAO object with a connection to a database
   * @param conn the connection object to the database
   */
  public EventDAO(Connection conn) {
    this.conn=conn;
  }

  /**
   * put an event into the database
   *
   * @param event the event object to be put into the database
   */
  public void createEvent(Event event) throws DataAccessException {
    //We can structure our string to be similar to a sql command, but if we insert question
    //marks we can change them later with help from the statement
    String sql = "INSERT INTO event (eventID, associatedUsername, personID, latitude, longitude, " +
            "country, city, eventType, year) VALUES(?,?,?,?,?,?,?,?,?)";
    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
      //Using the statements built-in set(type) functions we can pick the question mark we want
      //to fill in and give it a proper value. The first argument corresponds to the first
      //question mark found in our sql String
      stmt.setString(1, event.getEventID());
      stmt.setString(2, event.getUsername());
      stmt.setString(3, event.getPersonID());
      stmt.setFloat(4, event.getLatitude());
      stmt.setFloat(5, event.getLongitude());
      stmt.setString(6, event.getCountry());
      stmt.setString(7, event.getCity());
      stmt.setString(8, event.getEventType());
      stmt.setInt(9, event.getYear());

      stmt.executeUpdate();
    } catch (SQLException e) {
      throw new DataAccessException("Error encountered while inserting into the database");
    }
  }

  /**
   * find and return an event in the database
   *
   * @param eventID unique ID for the event to be found
   * @return the event if it can be found or null if not
   */
  public Event getEvent(String eventID) throws DataAccessException {
    Event event;
    ResultSet rs = null;
    String sql = "SELECT * FROM event WHERE eventID = ?;";
    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
      stmt.setString(1, eventID);
      rs = stmt.executeQuery();
      if (rs.next()) {
        event = new Event(rs.getString("eventID"), rs.getString("associatedUsername"),
                rs.getString("personID"), rs.getFloat("latitude"), rs.getFloat("longitude"),
                rs.getString("country"), rs.getString("city"), rs.getString("eventType"),
                rs.getInt("year"));
        return event;
      }
    } catch (SQLException e) {
      e.printStackTrace();
      throw new DataAccessException("Error encountered while finding event");
    } finally {
      if(rs != null) {
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
   * Returns all the events for a particular user
   *
   * @param username the username of user we want the events for
   * @return a list of all the events for that user
   */
  public Event[] getAllEvents(String username) throws DataAccessException {
    List<Event> events = new ArrayList<>();
    Event event;
    ResultSet rs = null;
    String sql = "SELECT * FROM event WHERE associatedUsername = ?";
    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
      stmt.setString(1, username);
      rs = stmt.executeQuery();
      while (rs.next()) {
        event = new Event(rs.getString("eventID"), rs.getString("associatedUsername"),
                rs.getString("personID"), rs.getFloat("latitude"), rs.getFloat("longitude"),
                rs.getString("country"), rs.getString("city"), rs.getString("eventType"),
                rs.getInt("year"));
        events.add(event);
      }
      return events.toArray(new Event[0]);
    } catch (SQLException e) {
      e.printStackTrace();
      throw new DataAccessException("Error encountered while finding event");
    } finally {
      if(rs != null) {
        try {
          rs.close();
        } catch (SQLException e) {
          e.printStackTrace();
        }
      }
    }
  }

  /**
   * clears the event table
   */
  public void clearTable() throws DataAccessException {
    try (Statement stmt = conn.createStatement()){
      String sql = "DELETE FROM event";
      stmt.executeUpdate(sql);
    } catch (SQLException e) {
      throw new DataAccessException("SQL Error encountered while clearing tables");
    }
  }

  public void deleteAllEvents(String username) throws DataAccessException {
    String sql = "DELETE FROM event WHERE associatedUsername = ?";
    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
      stmt.setString(1, username);
      stmt.executeUpdate();
    } catch (SQLException e) {
      e.printStackTrace();
      throw new DataAccessException("Error encountered while finding event");
    }
  }
}
