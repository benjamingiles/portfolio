package Request;

/**
 * An object that represents a LoginRequest
 */
public class LoginRequest {

  /**
   * Unique username for user
   */
  private String username;

  /**
   * User's password
   */
  private String password;

  /**
   * Creates a LoginRequest object
   *
   * @param username Unique username for user
   * @param password Unique username for user
   */
  public LoginRequest(String username, String password) {
    this.username=username;
    this.password=password;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }
}
