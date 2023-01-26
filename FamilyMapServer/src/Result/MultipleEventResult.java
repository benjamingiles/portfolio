package Result;

import model.Event;

/**
 * An object that represents a MultipleEventResult
 */
public class MultipleEventResult {
  /**
   * json array of Event objects
   */
  private Event[] data;
  /**
   * whether it was successful or not
   */
  private boolean success;
  /**
   * error message
   */
  private String message;

  /**
   * if successful create a MultipleEventResult object with an array of Events
   * @param data json array of Event objects
   * @param success whether it was successful
   */
  public MultipleEventResult(Event[] data, boolean success) {
    this.data = data;
    this.success = success;
    this.message = null;
  }

  /**
   * Create a MultipleEventResult object with an error message if unsuccessful
   * @param success whether it was successful
   * @param message error message
   */
  public MultipleEventResult(String message, boolean success) {
    this.message = message;
    this.success = success;
    this.data = null;
  }

  public Event[] getData() {
    return data;
  }

  public void setData(Event[] data) {
    this.data = data;
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
