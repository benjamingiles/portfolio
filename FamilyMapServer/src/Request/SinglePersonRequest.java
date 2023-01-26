package Request;

/**
 * An object that represents a SinglePersonRequest
 */
public class SinglePersonRequest {
  /**
   * id to be found
   */
  private String personID;

  /**
   * creates a SinglePersonRequest with the person's ID to be found
   * @param personID id to be found
   */
  public SinglePersonRequest(String personID) {
    this.personID = personID;
  }

  public String getPersonID() {
    return personID;
  }

  public void setPersonID(String personID) {
    this.personID = personID;
  }
}
