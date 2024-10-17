package service;

import dataaccess.DataAccess;
import dataaccess.MemoryDataAccess;

public class ServiceHandler {

  private UserService userService;
  private GameService gameService;

  public ServiceHandler(DataAccess database) {
    userService= new UserService(database);
    gameService = new GameService(database);
  }

  public ServiceHandler() {
    DataAccess database = new MemoryDataAccess();
    userService = new UserService(database);
    gameService = new GameService(database);
  }



}
