package model;

/**
 * An object that represents a User
 */
public class User {
  /**
   * Unique username for user
   */
  private String username;
  /**
   * User's password
   */
  private String password;
  /**
   * User's email address
   */
  private String email;
  /**
   * User's first name
   */
  private String firstName;
  /**
   * User's last name
   */
  private String lastName;
  /**
   * User's gender
   */
  private String gender;
  /**
   * Unique Person ID assigned to this user's generated Person
   */
  private String personID;

  /**
   * Creates a user object
   *
   * @param username Unique username for user
   * @param password User's password
   * @param email User's email address
   * @param firstName User's first name
   * @param lastName User's last name
   * @param gender User's gender
   * @param personID Unique Person ID assigned to this user's generated Person
   */
  public User(String username, String password, String email, String firstName,
              String lastName, String gender, String personID) {
    this.username=username;
    this.password=password;
    this.email=email;
    this.firstName=firstName;
    this.lastName=lastName;
    this.gender=gender;
    this.personID=personID;
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

  public String getPersonID() {
    return personID;
  }

  public void setPersonID(String personID) {
    this.personID=personID;
  }

  /**
   *
   * @param o object to compare to this one
   * @return true if it has the same data or false if not
   */
  @Override
  public boolean equals(Object o) {
    if (o == null)
      return false;
    if (o instanceof User) {
      User oUser = (User) o;
      return oUser.getUsername().equals(getUsername()) &&
              oUser.getPassword().equals(getPassword()) &&
              oUser.getPersonID().equals(getPersonID()) &&
              oUser.getEmail().equals(getEmail()) &&
              oUser.getFirstName().equals(getFirstName()) &&
              oUser.getLastName().equals(getLastName()) &&
              oUser.getGender().equals(getGender());
    } else {
      return false;
    }
  }
}
