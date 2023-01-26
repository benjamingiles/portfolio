package Result;

/**
 * An object that represents a RegisterResult
 */
public class RegisterResult {

  /**
   * Non-empty auth token string
   */
  private String authtoken;

  /**
   * Username passed in with request
   */
  private String username;

  /**
   * Non-empty string containing the person ID
   */
  private String personID;

  /**
   * error message if false
   */
  private String message;

  /**
   * boolean identifier whether it was successful or not
   */
  private boolean success;

  /**
   * creates a RegisterResult object if successful
   *
   * @param authToken Non-empty auth token string
   * @param username Username passed in with request
   * @param personID Non-empty string containing the person ID
   * @param success boolean identifier whether it was successful or not
   */
  public RegisterResult(String authToken, String username, String personID, boolean success) {
    this.authtoken=authToken;
    this.username=username;
    this.personID=personID;
    this.success=success;
    this.message=null;
  }

  /**
   * creates a RegisterResult object with just an error message
   * @param success boolean identifier whether it was successful or not
   * @param message error message if false
   */
  public RegisterResult(boolean success, String message) {
    this.message=message;
    this.success=success;
    this.authtoken=null;
    this.username=null;
    this.personID=null;
  }

  public String getAuthtoken() {
    return authtoken;
  }

  public void setAuthtoken(String authToken) {
    this.authtoken=authToken;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username=username;
  }

  public String getPersonID() {
    return personID;
  }

  public void setPersonID(String personID) {
    this.personID=personID;
  }

  public boolean isSuccess() {
    return success;
  }

  public void setSuccess(boolean success) {
    this.success=success;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message=message;
  }
}
