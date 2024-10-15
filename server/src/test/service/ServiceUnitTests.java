package service;

import dataaccess.MemoryDataAccess;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class ServiceUnitTests {
  @Test
  @DisplayName("Clear database")

  public void clear() {
    //Add elements to database to be cleared
    MemoryDataAccess database = new MemoryDataAccess();
    database.createGame();
    database.createGame();
    database.createUser("name", "password");
    database.createAuth("name");

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
