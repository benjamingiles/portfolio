package Request;

/**
 * An object that represents a SingleEventRequest
 */
public class SingleEventRequest {
  /**
   * unique identifier for event
   */
  private String eventID;

  /**
   * Creates a Request with an eventID
   * @param eventID Unique identifier for event
   */
  public SingleEventRequest(String eventID) {
    this.eventID = eventID;
  }

  public String getEventID() {
    return eventID;
  }

  public void setEventID(String eventID) {
    this.eventID = eventID;
  }
}
