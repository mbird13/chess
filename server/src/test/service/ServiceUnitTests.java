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

    Assertions.assertEquals(database, new MemoryDataAccess(), "Database not cleared properly");
  }
}
