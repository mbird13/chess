package service;

import dataaccess.MemoryDataAccess;
import model.AuthData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class ServiceUnitTests {
  @Test
  @DisplayName("Clear database")

  public void clear() {
    //Add elements to database to be cleared
    MemoryDataAccess database = new MemoryDataAccess();
    ServiceHandler service = new ServiceHandler(database);
    LoginService loginService = new LoginService(database);
    Assertions.assertDoesNotThrow(() -> loginService.register(new LoginRequest("name", "password")));

    //clear database
    database.clear();

    //database is equal to empty database
    Assertions.assertEquals(new MemoryDataAccess(), database, "Database not cleared properly");
  }

  @Test
  void createUser() {

  }

  @Test
  void getUser() {
  }

  @Test
  void createGame() {
  }

  @Test
  void getGame() {
  }

  @Test
  void listGames() {
  }

  @Test
  void updateGame() {
  }

  @Test
  void createAuth() {
  }

  @Test
  void getAuth() {
  }

  @Test
  void deleteAuthData() {
  }
}
