package Request;

/**
 * An object that represents a FillRequest
 */
public class FillRequest {

  /**
   * Unique username for user
   */
  private String username;
  /**
   * amount of generations to fill in
   */
  private int generations;

  /**
   * Creates a FillRequest with a username given and sets generations to 4 as default
   * @param username Unique username for user
   */
  public FillRequest(String username) {
    this.username = username;
    this.generations = 4;
  }

  /**
   * Creates a FillRequest object with a username and generations
   *
   * @param username Unique username for user
   * @param generations amount of generations to fill in
   */
  public FillRequest(String username, int generations) {
    this.username = username;
    this.generations = generations;
  }

  public String getUsername() {
    return username;
  }

  public int getGenerations() {
    return generations;
  }
}
