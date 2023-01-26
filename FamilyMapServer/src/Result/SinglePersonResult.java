package Result;

/**
 * An object that represents a SinglePersonResult
 */
public class SinglePersonResult {
  /**
   * Username of user to which this person belongs
   */
  private String associatedUsername;
  /**
   * Unique identifier for this person
   */
  private String personID;
  /**
   * person's first name
   */
  private String firstName;
  /**
   * person's last name
   */
  private String lastName;
  /**
   * person's gender
   */
  private String gender;
  /**
   * Person ID of person's father
   */
  private String fatherID;
  /**
   * PersonId of person's mother
   */
  private String motherID;
  /**
   * personID of person's spouse
   */
  private String spouseID;
  /**
   * error message
   */
  private String message;
  /**
   * whether it was successful
   */
  private boolean success;

  /**
   * creates a Result object if successful with all fields filled out
   *
   * @param associatedUsername Username of user to which this person belongs
   * @param personID Unique identifier for this person
   * @param firstName person's first name
   * @param lastName person's last name
   * @param gender person's gender
   * @param fatherID PersonID of person's father
   * @param motherID PersonID of person's mother
   * @param spouseID PersonID of person's spouse
   * @param success whether it was successful
   */
  public SinglePersonResult(String associatedUsername, String personID, String firstName, String lastName,
                            String gender, String fatherID, String motherID, String spouseID, boolean success) {
    this.associatedUsername = associatedUsername;
    this.personID = personID;
    this.firstName = firstName;
    this.lastName = lastName;
    this.gender = gender;
    this.fatherID = fatherID;
    this.motherID = motherID;
    this.spouseID = spouseID;
    this.success = success;
    this.message = null;
  }

  /**
   * if it was unsuccessful create a result object with an error message
   *
   * @param message the error message
   * @param success whether it was successful
   */
  public SinglePersonResult(String message, boolean success) {
    this.message = message;
    this.success = success;
    this.associatedUsername = null;
    this.personID = null;
    this.firstName = null;
    this.lastName = null;
    this.motherID = null;
    this.fatherID = null;
    this.spouseID = null;
  }

  public String getAssociatedUsername() {
    return associatedUsername;
  }

  public void setAssociatedUsername(String associatedUsername) {
    this.associatedUsername = associatedUsername;
  }

  public String getPersonID() {
    return personID;
  }

  public void setPersonID(String personID) {
    this.personID = personID;
  }

  public String getFirstName() {
    return firstName;
  }

  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  public String getLastName() {
    return lastName;
  }

  public void setLastName(String lastName) {
    this.lastName = lastName;
  }

  public String getGender() {
    return gender;
  }

  public void setGender(String gender) {
    this.gender = gender;
  }

  public String getFatherID() {
    return fatherID;
  }

  public void setFatherID(String fatherID) {
    this.fatherID = fatherID;
  }

  public String getMotherID() {
    return motherID;
  }

  public void setMotherID(String motherID) {
    this.motherID = motherID;
  }

  public String getSpouseID() {
    return spouseID;
  }

  public void setSpouseID(String spouseID) {
    this.spouseID = spouseID;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public boolean isSuccess() {
    return success;
  }

  public void setSuccess(boolean success) {
    this.success = success;
  }
}
