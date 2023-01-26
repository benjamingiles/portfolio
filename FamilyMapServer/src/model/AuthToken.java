package model;

/**
 * An object that represents an AuthToken
 */
public class AuthToken {
  /**
   * Unique authToken
   */
  private String authToken;
  /**
   * Username that is associated with the authToken
   */
  private String username;

  /**
   * Creates an AuthToken object
   *
   * @param authToken Unique authToken
   * @param username Username that is associated with authToken
   */
  public AuthToken(String authToken, String username) {
    this.authToken=authToken;
    this.username=username;
  }

  public String getAuthToken() {
    return authToken;
  }

  public void setAuthToken(String authToken) {
    this.authToken=authToken;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username=username;
  }

  /**
   *
   * @param o Object being compared to this one
   * @return true if the data in object matches this one or false if not
   */
  @Override
  public boolean equals(Object o) {
    if (o == null)
      return false;
    if (o instanceof AuthToken) {
      AuthToken oAuthToken = (AuthToken) o;
      return oAuthToken.getAuthToken().equals(getAuthToken()) &&
              oAuthToken.getUsername().equals(getUsername());
    } else {
      return false;
    }
  }
}
