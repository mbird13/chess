package service;

import dataaccess.DataAccess;
import dataaccess.MemoryDataAccess;
import model.AuthData;
import model.UserData;
import org.eclipse.jetty.util.log.Log;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import Exception.ResponseException;

public class ServiceUnitTests {
  @Test
  @DisplayName("Clear database")

  public void clear() {
    //Add elements to database to be cleared
    MemoryDataAccess database = new MemoryDataAccess();
    ServiceHandler service = new ServiceHandler(database);
    UserService userService= new UserService(database);
    Assertions.assertDoesNotThrow(() -> userService.register(new LoginRequest("name", "password")));

    //clear database
    database.clear();

    //database is equal to empty database
    Assertions.assertEquals(new MemoryDataAccess(), database, "Database not cleared properly");
  }

  @Test
  void loginSuccess() {
    DataAccess database = new MemoryDataAccess();
    UserService service = new UserService(database);

    LoginRequest request = new LoginRequest("name", "password");
    Assertions.assertDoesNotThrow(() -> service.register(request));
    Assertions.assertDoesNotThrow(() -> service.login(request));

    Assertions.assertEquals(new UserData("name", "password"), database.getUser("name"));
  }

  @Test
  void loginFailure() {
    DataAccess database = new MemoryDataAccess();
    UserService service = new UserService(database);

    LoginRequest request = new LoginRequest("name", "password");
    Assertions.assertDoesNotThrow(() -> service.register(request));
    Assertions.assertDoesNotThrow(() -> service.login(request));
    Assertions.assertEquals(new UserData("name", "password"), database.getUser("name"));

    //duplicate username request
    LoginRequest duplicateRequest = new LoginRequest("name", "newPassword");
    Assertions.assertThrows(ResponseException.class, () -> service.login(duplicateRequest));

    Assertions.assertEquals(new UserData("name", "password"), database.getUser("name"));
  }

  @Test
  void logoutSuccess() {
    DataAccess database = new MemoryDataAccess();
    UserService service = new UserService(database);

    LoginRequest request = new LoginRequest("name", "password");
    LoginResult loginResult = Assertions.assertDoesNotThrow(() -> service.register(request));
    Assertions.assertEquals(new UserData("name", "password"), database.getUser("name"));

    LogoutRequest logoutRequest = new LogoutRequest(loginResult.authToken());
    Assertions.assertDoesNotThrow(() -> service.logout(logoutRequest));

    Assertions.assertNull(database.getAuth(loginResult.authToken()));
  }

  @Test
  void logoutFailure() {
    DataAccess database = new MemoryDataAccess();
    UserService service = new UserService(database);

    //random authToken
    LogoutRequest randomLogoutRequest = new LogoutRequest("randomstring");
    //Assertions.assertThrows(ResponseException.class, () -> service.logout(randomLogoutRequest));

    LoginRequest request = new LoginRequest("name", "password");
    LoginResult loginResult = Assertions.assertDoesNotThrow(() -> service.register(request));
    Assertions.assertEquals(new UserData("name", "password"), database.getUser("name"));

    LogoutRequest logoutRequest = new LogoutRequest(loginResult.authToken());
    Assertions.assertDoesNotThrow(() -> service.logout(logoutRequest));

    Assertions.assertNull(database.getAuth(loginResult.authToken()));

    //duplicate logout
    Assertions.assertThrows(ResponseException.class, () -> service.logout(logoutRequest));
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
