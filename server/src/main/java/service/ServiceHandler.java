package service;

import dataaccess.DataAccess;

public class ServiceHandler {

  private UserService userService;
  private GameService gameService;

  public ServiceHandler(DataAccess database) {
    userService= new UserService(database);
    gameService = new GameService(database);
  }

}
