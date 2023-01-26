package Result;

/**
 * An object that represents a ClearResult
 */
public class ClearResult {
  /**
   * "Clear succeeded" if successful or an error message if not
   */
  private String message;
  /**
   * whether the clear was successful
   */
  private boolean success;

  /**
   * creates a ClearResult object that says if it was successful or not
   * @param message says "clear succeeded" if successful or an error message if not
   * @param success whether the clear was successful
   */
  public ClearResult(String message, boolean success) {
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
