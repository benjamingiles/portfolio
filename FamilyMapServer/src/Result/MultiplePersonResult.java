package Result;

import model.Person;

/**
 * An object that represents a MultiplePersonResult
 */
public class MultiplePersonResult {
  /**
   * json array of Person objects
   */
  private Person[] data;
  /**
   * whether it was successful or not
   */
  private boolean success;
  /**
   * error message
   */
  private String message;

  /**
   * if successful create a MultiplePersonResult object with an array of Persons
   * @param data json array of Person objects
   * @param success whether it was successful
   */
  public MultiplePersonResult(Person[] data, boolean success) {
    this.data = data;
    this.success = success;
    this.message = null;
  }

  /**
   * Create a MultiplePersonResult object with an error message if unsuccessful
   * @param success whether it was successful
   * @param message error message
   */
  public MultiplePersonResult(String message, boolean success) {
    this.message = message;
    this.success = success;
    this.data = null;
  }

  public Person[] getData() {
    return data;
  }

  public void setData(Person[] data) {
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
