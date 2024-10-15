package service;


import dataaccess.DataAccess;
import model.UserData;
import Exception.ResponseException;

/**
 * Implements services related to creating and logging in users
 */
public class LoginService implements Service {


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

  private LoginResult login(LoginRequest loginRequest) {
    database.getUser(loginRequest.username());
  }

}

record LoginRequest(String username, String password) {}
record LoginResult(String username, String authToken, String exceptionMessage) {}

