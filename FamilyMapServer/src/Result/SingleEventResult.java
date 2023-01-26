package Result;

/**
 * An object that represents a SingleEventResult
 */
public class SingleEventResult {
  /**
   * Unique identifier for this event
   */
  private String eventID;
  /**
   * Username of user to which this event belongs
   */
  private String associatedUsername;
  /**
   * ID of person to which this event belongs
   */
  private String personID;
  /**
   * Latitude of event's location
   */
  private Float latitude;
  /**
   * Longitude of event's location
   */
  private Float longitude;
  /**
   * Country in which event occurred
   */
  private String country;
  /**
   * City in which even occurred
   */
  private String city;
  /**
   * Type of event
   */
  private String eventType;
  /**
   * Year in which event occurred
   */
  private Integer year;
  /**
   * error message
   */
  private String message;
  /**
   * whether it was successful or not
   */
  private boolean success;


  /**
   * Creates a result object if successful
   *
   * @param eventID Unique identifier for this event
   * @param associatedUsername Username of user to which this event belongs
   * @param personID ID of person to which this event belongs
   * @param latitude Latitude of event's location
   * @param longitude Longitude of event's location
   * @param country Country in which event occurred
   * @param city City in which event occurred
   * @param eventType Type of event
   * @param year Year in which event occurred
   * @param success whether it was successful or not
   */
  public SingleEventResult(String eventID, String associatedUsername, String personID, float latitude, float longitude,
                           String country, String city, String eventType, int year, boolean success) {
    this.eventID = eventID;
    this.associatedUsername = associatedUsername;
    this.personID = personID;
    this.latitude = latitude;
    this.longitude = longitude;
    this.country = country;
    this.city = city;
    this.eventType = eventType;
    this.year = year;
    this.success = success;
    this.message = null;
  }

  /**
   * Creates a Result object with an error message if unsuccessful
   * @param success whether it was successful or not
   * @param message error message
   */
  public SingleEventResult(String message, boolean success) {
    this.message = message;
    this.success = success;
    this.eventID = null;
    this.associatedUsername = null;
    this.personID = null;
    this.latitude = null;
    this.longitude = null;
    this.country = null;
    this.city = null;
    this.eventType = null;
    this.year = null;
  }

  public String getEventID() {
    return eventID;
  }

  public void setEventID(String eventID) {
    this.eventID = eventID;
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

  public float getLatitude() {
    return latitude;
  }

  public void setLatitude(float latitude) {
    this.latitude = latitude;
  }

  public float getLongitude() {
    return longitude;
  }

  public void setLongitude(float longitude) {
    this.longitude = longitude;
  }

  public String getCountry() {
    return country;
  }

  public void setCountry(String country) {
    this.country = country;
  }

  public String getCity() {
    return city;
  }

  public void setCity(String city) {
    this.city = city;
  }

  public String getEventType() {
    return eventType;
  }

  public void setEventType(String eventType) {
    this.eventType = eventType;
  }

  public int getYear() {
    return year;
  }

  public void setYear(int year) {
    this.year = year;
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
