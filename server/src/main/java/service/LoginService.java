package service;


import dataaccess.DataAccess;
import model.AuthData;
import model.UserData;
import Exception.ResponseException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Objects;

/**
 * Implements services related to creating and logging in users
 */
public class LoginService implements Service {

  private static final SecureRandom secureRandom = new SecureRandom();
  private static final int TOKEN_LENGTH = 24;
  private DataAccess database;

  public LoginService(DataAccess database) {
    this.database = database;
  }


  /**
   * add a user to the database and logs in the user
   *
   * @param request contains username and password for new user
   * @return RegisterResult contains UserData of new user
   * @throws ResponseException username already taken
   */
  public LoginResult register(LoginRequest request) throws ResponseException {

    UserData user = database.getUser(request.username());
    if (user != null) {
      throw new ResponseException(403, "Error: already taken");
    }

    user = database.createUser(request.username(), request.password());

    return login(new LoginRequest(user.username(), user.password()));
  }

  private LoginResult login(LoginRequest loginRequest) throws ResponseException {

    UserData userData = database.getUser(loginRequest.username());
    if (userData == null || !Objects.equals(userData.password(), loginRequest.password())) {
      throw new ResponseException(401, "Error: unauthorized");
    }

    //create new authToken
    byte[] randomBytes = new byte[TOKEN_LENGTH];
    secureRandom.nextBytes(randomBytes);
    String token = Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);

    AuthData authData = new AuthData(token, userData.username());
    database.createAuth(authData);

    return new LoginResult(userData.username(), token, null);
  }

}

record LoginRequest(String username, String password) {}
record LoginResult(String username, String authToken, String exceptionMessage) {}

