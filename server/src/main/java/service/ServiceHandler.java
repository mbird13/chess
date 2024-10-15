package service;

import dataaccess.DataAccess;

public class ServiceHandler {

  private LoginService loginService;

  public ServiceHandler(DataAccess database) {
    loginService = new LoginService(database);
  }

}
