package Result;

/**
 * An object that represents a FillResult
 */
public class FillResult {
  /**
   * message with error if unsuccessful or successful if successful
   */
  private String message;
  /**
   * whether it was successful or not
   */
  private boolean success;

  /**
   * Creates a FillResult object
   * @param message error message if unsuccessful or success message otherwise
   * @param success whether it was successful
   */
  public FillResult(String message, boolean success) {
    this.message = message;
    this.success = success;
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
