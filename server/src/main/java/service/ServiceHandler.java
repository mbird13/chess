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
    var user = new Gson().fromJson(request.body(), LoginRequest.class);
    LoginResult loginResult = userService.register(user);
    return new Gson().toJson(loginResult);
  }

  public void clear() {
    database.clear();
  }
}
