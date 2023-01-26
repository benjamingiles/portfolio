package Request;

/**
 * An object that represents a RegisterRequest
 */
public class RegisterRequest {

  /**
   * Unique username for User
   */
  private String username;
  /**
   * User's password
   */
  private String password;
  /**
   * User's email
   */
  private String email;
  /**
   * user's first name
   */
  private String firstName;
  /**
   * user's last name
   */
  private String lastName;
  /**
   * user's gender
   */
  private String gender;

  /**
   * Creates a RegisterRequest object
   *
   * @param username Unique username for user
   * @param password user's password
   * @param email user's email
   * @param firstName user's first name
   * @param lastName user's last name
   * @param gender user's gender
   */
  public RegisterRequest(String username, String password, String email, String firstName, String lastName, String gender) {
    this.username=username;
    this.password=password;
    this.email=email;
    this.firstName=firstName;
    this.lastName=lastName;
    this.gender=gender;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username=username;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password=password;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email=email;
  }

  public String getFirstName() {
    return firstName;
  }

  public void setFirstName(String firstName) {
    this.firstName=firstName;
  }

  public String getLastName() {
    return lastName;
  }

  public void setLastName(String lastName) {
    this.lastName=lastName;
  }

  public String getGender() {
    return gender;
  }

  public void setGender(String gender) {
    this.gender=gender;
  }
}
