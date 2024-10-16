package service;

import dataaccess.DataAccess;

public class GameService implements Service {

  private DataAccess database;

  public GameService(DataAccess database) {
    this.database = database;
  }
}
