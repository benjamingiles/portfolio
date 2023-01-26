package model;

/**
 * An object that represents an Event
 */
public class Event {
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
  private float latitude;
  /**
   * Longitude of event's location
   */
  private float longitude;
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
  private int year;

  /**
   * Creates an event object
   *
   * @param eventID Unique identifier for this event
   * @param username Username of user to which this event belongs
   * @param personID ID of person to which this event belongs
   * @param latitude Latitude of event's location
   * @param longitude Longitude of event's location
   * @param country Country in which event occurred
   * @param city City in which event occurred
   * @param eventType Type of event
   * @param year Year in which event occurred
   */
  public Event(String eventID, String username, String personID, float latitude, float longitude,
               String country, String city, String eventType, int year) {
    this.eventID=eventID;
    this.associatedUsername=username;
    this.personID=personID;
    this.latitude=latitude;
    this.longitude=longitude;
    this.country=country;
    this.city=city;
    this.eventType=eventType;
    this.year=year;
  }

  public String getEventID() {
    return eventID;
  }

  public void setEventID(String eventID) {
    this.eventID=eventID;
  }

  public String getUsername() {
    return associatedUsername;
  }

  public void setUsername(String associatedUsername) {
    this.associatedUsername=associatedUsername;
  }

  public String getPersonID() {
    return personID;
  }

  public void setPersonID(String personID) {
    this.personID=personID;
  }

  public float getLatitude() {
    return latitude;
  }

  public void setLatitude(float latitude) {
    this.latitude=latitude;
  }

  public float getLongitude() {
    return longitude;
  }

  public void setLongitude(float longitude) {
    this.longitude=longitude;
  }

  public String getCountry() {
    return country;
  }

  public void setCountry(String country) {
    this.country=country;
  }

  public String getCity() {
    return city;
  }

  public void setCity(String city) {
    this.city=city;
  }

  public String getEventType() {
    return eventType;
  }

  public void setEventType(String eventType) {
    this.eventType=eventType;
  }

  public int getYear() {
    return year;
  }

  public void setYear(int year) {
    this.year=year;
  }

  /**
   *
   * @param o object we are comparing
   * @return true if the object is equal or false if not
   */
  @Override
  public boolean equals(Object o) {
    if (o == null)
      return false;
    if (o instanceof Event) {
      Event oEvent = (Event) o;
      return oEvent.getEventID().equals(getEventID()) &&
              oEvent.getUsername().equals(getUsername()) &&
              oEvent.getPersonID().equals(getPersonID()) &&
              oEvent.getLatitude() == (getLatitude()) &&
              oEvent.getLongitude() == (getLongitude()) &&
              oEvent.getCountry().equals(getCountry()) &&
              oEvent.getCity().equals(getCity()) &&
              oEvent.getEventType().equals(getEventType()) &&
              oEvent.getYear() == (getYear());
    } else {
      return false;
    }
  }

}
