package service;


import dataaccess.DataAccess;
import model.AuthData;
import model.UserData;
import exception.ResponseException;
import servicehelpers.LoginRequest;
import servicehelpers.LoginResult;
import servicehelpers.LogoutRequest;
import servicehelpers.RegisterRequest;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.Objects;

/**
 * Implements services related to creating and logging in users
 */
public class UserService implements Service {

  private static final SecureRandom SECURE_RANDOM= new SecureRandom();
  private static final int TOKEN_LENGTH = 24;
  private final DataAccess database;

  public UserService(DataAccess database) {
    this.database = database;
  }


  /**
   * add a user to the database and logs in the user
   *
   * @param request contains username and password for new user
   * @return RegisterResult contains UserData of new user
   * @throws ResponseException username already taken
   */
  public LoginResult register(RegisterRequest request) throws ResponseException {
    if (request.username() == null || request.password() == null) {
      throw new ResponseException(400, "Error: bad request");
    }

    UserData user = database.getUser(request.username());
    if (user != null) {
      throw new ResponseException(403, "Error: already taken");
    }

    user = database.createUser(request.username(), request.password(), request.email());

    return login(new LoginRequest(user.username(), user.password()));
  }

  public LoginResult login(LoginRequest loginRequest) throws ResponseException {

    UserData userData = database.getUser(loginRequest.username());
    if (userData == null || !Objects.equals(userData.password(), loginRequest.password())) {
      throw new ResponseException(401, "Error: unauthorized");
    }

    //create new authToken
    byte[] randomBytes = new byte[TOKEN_LENGTH];
    SECURE_RANDOM.nextBytes(randomBytes);
    String token = Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);

    AuthData authData = new AuthData(token, userData.username());
    database.createAuth(authData);

    return new LoginResult(userData.username(), token, null);
  }

  public void logout(LogoutRequest logoutRequest) throws ResponseException {
    AuthData authData = database.getAuth(logoutRequest.authToken());
    if (authData == null) {
      throw new ResponseException(401, "Error: unauthorized");
    }

    database.deleteAuthData(authData);
  }

}

