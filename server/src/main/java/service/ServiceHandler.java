package service;

import com.google.gson.Gson;
import dataaccess.DataAccess;
import dataaccess.MemoryDataAccess;
import model.UserData;
import spark.Request;
import Exception.ResponseException;

import javax.xml.crypto.Data;

public class ServiceHandler {

  private DataAccess database;
  private UserService userService;
  private GameService gameService;

  public ServiceHandler(DataAccess database) {
    this.database = database;
    userService= new UserService(database);
    gameService = new GameService(database);
  }

  public ServiceHandler() {
    database = new MemoryDataAccess();
    userService = new UserService(database);
    gameService = new GameService(database);
  }

  public Object register(Request request) throws ResponseException {
    var user = new Gson().fromJson(request.body(), RegisterRequest.class);
    LoginResult result = userService.register(user);
    return new Gson().toJson(result);
  }

  public void clear() {
    database.clear();
  }

  public Object login(Request request) throws ResponseException {
    var user = new Gson().fromJson(request.body(), LoginRequest.class);
    LoginResult result = userService.login(user);
    return new Gson().toJson(result);
  }

  public void logout(Request request) throws ResponseException {
    LogoutRequest logoutRequest = new LogoutRequest(request.headers("authorization"));
    userService.logout(logoutRequest);
  }
}
