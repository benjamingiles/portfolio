package Result;

/**
 * An object that represents a LoadResult
 */
public class LoadResult {
  /**
   * error if unsuccessful or successful otherwise
   */
  private String message;
  /**
   * whether it was successful or not
   */
  private boolean success;

  /**
   * Creates a LoadResult object with the response
   * @param message error if unsuccessful or successful otherwise
   * @param success whether it was successful or not
   */
  public LoadResult(String message, boolean success) {
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
