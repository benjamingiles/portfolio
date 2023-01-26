package dao;

/**
 * The Exception class that the DAO's throw
 */
public class DataAccessException extends Exception {
  DataAccessException(String message)
  {
    super(message);
  }

  DataAccessException()
  {
    super();
  }
}