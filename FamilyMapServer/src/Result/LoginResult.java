package Result;

/**
 * An object that represents a LoginResult
 */
public class LoginResult {
  /**
   * authToken for user
   */
  private String authtoken;
  /**
   * username passed in with request
   */
  private String username;
  /**
   * unique person id
   */
  private String personID;
  /**
   * error message
   */
  private String message;
  /**
   * whether it was successful or not
   */
  private boolean success;

  /**
   * creates LoginResult object if successful
   *
   * @param authToken non-empty auth token
   * @param username username passed in with request
   * @param personID unique personID
   * @param success whether it was successful or not
   */
  public LoginResult(String authToken, String username, String personID, boolean success) {
    this.authtoken = authToken;
    this.username = username;
    this.personID = personID;
    this.success = success;
    this.message = null;
  }

  /**
   * creates LoginResult object with an error message if false
   * @param success boolean response whether it was successful or not
   * @param message error message
   */
  public LoginResult(boolean success, String message) {
    this.message = message;
    this.success = success;
    this.authtoken = null;
    this.username = null;
    this.personID = null;
  }

  public String getAuthToken() {
    return authtoken;
  }

  public void setAuthToken(String authToken) {
    this.authtoken = authToken;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getPersonID() {
    return personID;
  }

  public void setPersonID(String personID) {
    this.personID = personID;
  }

  public boolean isSuccess() {
    return success;
  }

  public void setSuccess(boolean success) {
    this.success = success;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }
}
