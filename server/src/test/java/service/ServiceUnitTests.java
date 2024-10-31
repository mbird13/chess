package service;

import chess.ChessGame;
import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import dataaccess.MemoryDataAccess;
import dataaccess.SqlDataAccess;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import exception.ResponseException;
import org.mindrot.jbcrypt.BCrypt;
import servicehelpers.*;

public class ServiceUnitTests {

  @BeforeEach
  public void setup() {

  }

  @Test
  @DisplayName("Clear database")

  public void clear() {
    //Add elements to database to be cleared
    MemoryDataAccess database = new MemoryDataAccess();
    UserService userService= new UserService(database);
    Assertions.assertDoesNotThrow(() -> userService.register(new RegisterRequest("name", "password", "email")));

    //clear database
    database.clear();

    //database is equal to empty database
    Assertions.assertEquals(new MemoryDataAccess(), database, "Database not cleared properly");
  }

  @Test
  void loginSuccess() throws ResponseException, DataAccessException {
    DataAccess database = new SqlDataAccess();
    UserService service = new UserService(database);
    database.clear();

    var request = new RegisterRequest("name", "password", "email");
    Assertions.assertDoesNotThrow(() -> service.register(request));
    var loginRequest = new LoginRequest("name", "password");
    Assertions.assertDoesNotThrow(() -> service.login(loginRequest));

    Assertions.assertTrue(BCrypt.checkpw("password", database.getUser("name").password()));
  }

  @Test
  void loginFailure() throws ResponseException {
    DataAccess database = new MemoryDataAccess();
    UserService service = new UserService(database);

    var request = new RegisterRequest("name", "password", "email");
    Assertions.assertDoesNotThrow(() -> service.register(request));
    var loginRequest = new LoginRequest("name", "password");
    Assertions.assertDoesNotThrow(() -> service.login(loginRequest));
    Assertions.assertTrue(BCrypt.checkpw("password", database.getUser("name").password()));

    //duplicate username request
    LoginRequest duplicateRequest = new LoginRequest("name", "newPassword");
    Assertions.assertThrows(ResponseException.class, () -> service.login(duplicateRequest));

    Assertions.assertTrue(BCrypt.checkpw("password", database.getUser("name").password()));
  }

  @Test
  void logoutSuccess() throws ResponseException {
    DataAccess database = new MemoryDataAccess();
    UserService service = new UserService(database);

    var request = new RegisterRequest("name", "password", "email");
    LoginResult loginResult = Assertions.assertDoesNotThrow(() -> service.register(request));
    Assertions.assertTrue(BCrypt.checkpw("password", database.getUser("name").password()));

    LogoutRequest logoutRequest = new LogoutRequest(loginResult.authToken());
    Assertions.assertDoesNotThrow(() -> service.logout(logoutRequest));

    Assertions.assertNull(database.getAuth(loginResult.authToken()));
  }

  @Test
  void logoutFailure() throws ResponseException {
    DataAccess database = new MemoryDataAccess();
    UserService service = new UserService(database);

    //random authToken
    LogoutRequest randomLogoutRequest = new LogoutRequest("randomstring");
    Assertions.assertThrows(ResponseException.class, () -> service.logout(randomLogoutRequest));

    var request = new RegisterRequest("name", "password", "email");
    LoginResult loginResult = Assertions.assertDoesNotThrow(() -> service.register(request));
    Assertions.assertTrue(BCrypt.checkpw("password", database.getUser("name").password()));

    LogoutRequest logoutRequest = new LogoutRequest(loginResult.authToken());
    Assertions.assertDoesNotThrow(() -> service.logout(logoutRequest));

    Assertions.assertNull(database.getAuth(loginResult.authToken()));

    //duplicate logout
    Assertions.assertThrows(ResponseException.class, () -> service.logout(logoutRequest));
  }

  @Test
  void createGameSuccess() throws ResponseException {
    //initialize a user
    DataAccess database = new MemoryDataAccess();
    UserService userService = new UserService(database);
    var registerRequest = new RegisterRequest("name", "password", "email");
    var registerResult = userService.register(registerRequest);


    var request = new CreateGameRequest(registerResult.authToken(), "name");
    GameService gameService = new GameService(database);
    Assertions.assertDoesNotThrow(() -> gameService.createGame(request));
    ListGamesRequest listRequest = new ListGamesRequest(registerResult.authToken());
    Assertions.assertEquals(1, gameService.listGames(listRequest).size());
    Assertions.assertTrue(gameService.listGames(listRequest).contains(new GameData("1", null, null, "name", new ChessGame())));
  }

  @Test
  void joinGame() throws ResponseException {
    //initialize a user and create game
    DataAccess database = new MemoryDataAccess();
    UserService userService = new UserService(database);
    var registerRequest = new RegisterRequest("name", "password", "email");
    var registerResult = userService.register(registerRequest);
    var createRequest = new CreateGameRequest(registerResult.authToken(), "gameName");
    GameService gameService = new GameService(database);
    Assertions.assertDoesNotThrow(() -> gameService.createGame(createRequest));

    JoinGameRequest request = new JoinGameRequest(registerResult.authToken(), ChessGame.TeamColor.WHITE, "1");
    Assertions.assertDoesNotThrow(() -> gameService.joinGame(request));

    ListGamesRequest listRequest = new ListGamesRequest(registerResult.authToken());
    Assertions.assertEquals(1, gameService.listGames(listRequest).size());
    var game = new GameData("1", "name", null, "gameName", new ChessGame());
    Assertions.assertTrue(gameService.listGames(listRequest).contains(game));
  }

  @Test
  void createGameFailure() throws ResponseException {
    //initialize a user
    DataAccess database = new MemoryDataAccess();
    UserService userService = new UserService(database);
    var registerRequest = new RegisterRequest("name", "password", "email");
    var registerResult = userService.register(registerRequest);

    //bad auth Token throws exception
    var request = new CreateGameRequest("badAuthToken", "name");
    GameService gameService = new GameService(database);
    Assertions.assertThrows(ResponseException.class, () -> gameService.createGame(request));
    ListGamesRequest listRequest = new ListGamesRequest(registerResult.authToken());
    Assertions.assertEquals(0, gameService.listGames(listRequest).size());
  }

  @Test
  void joinGameBadAuth() throws ResponseException {
    //initialize two users and create game
    DataAccess database = new MemoryDataAccess();
    UserService userService = new UserService(database);
    var registerRequest = new RegisterRequest("name", "password", "email");
    var registerResult = userService.register(registerRequest);
    var playerTwoRequest = new RegisterRequest("playerTwo", "pass", "@321");
    var playerTwoResult = userService.register(playerTwoRequest);
    var createRequest = new CreateGameRequest(registerResult.authToken(), "gameName");
    GameService gameService = new GameService(database);
    Assertions.assertDoesNotThrow(() -> gameService.createGame(createRequest));

    //bad authToken
    JoinGameRequest badRequest = new JoinGameRequest("badAuth", ChessGame.TeamColor.WHITE, "1");
    Assertions.assertThrows(ResponseException.class, () -> gameService.joinGame(badRequest));
  }

  @Test
  void joinGameBadID() throws ResponseException {
    //initialize two users and create game
    DataAccess database = new MemoryDataAccess();
    UserService userService = new UserService(database);
    var registerRequest = new RegisterRequest("name", "password", "email");
    var registerResult = userService.register(registerRequest);
    var playerTwoRequest = new RegisterRequest("playerTwo", "pass", "@321");
    var playerTwoResult = userService.register(playerTwoRequest);
    var createRequest = new CreateGameRequest(registerResult.authToken(), "gameName");
    GameService gameService = new GameService(database);
    Assertions.assertDoesNotThrow(() -> gameService.createGame(createRequest));

    //bad gameID
    var badGameID = new JoinGameRequest(registerResult.authToken(), ChessGame.TeamColor.WHITE, "5");
    Assertions.assertThrows(ResponseException.class, () -> gameService.joinGame(badGameID));
  }

  @Test
  void joinGameDuplicatePlayer() throws ResponseException {
    //initialize two users and create game
    DataAccess database = new MemoryDataAccess();
    UserService userService = new UserService(database);
    var registerRequest = new RegisterRequest("name", "password", "email");
    var registerResult = userService.register(registerRequest);
    var playerTwoRequest = new RegisterRequest("playerTwo", "pass", "@321");
    var playerTwoResult = userService.register(playerTwoRequest);
    var createRequest = new CreateGameRequest(registerResult.authToken(), "gameName");
    GameService gameService = new GameService(database);
    Assertions.assertDoesNotThrow(() -> gameService.createGame(createRequest));

    //add two white players
    JoinGameRequest request = new JoinGameRequest(registerResult.authToken(), ChessGame.TeamColor.WHITE, "1");
    Assertions.assertDoesNotThrow(() -> gameService.joinGame(request));
    JoinGameRequest duplicateRequest = new JoinGameRequest(playerTwoResult.authToken(), ChessGame.TeamColor.WHITE, "1");
    Assertions.assertThrows(ResponseException.class, () -> gameService.joinGame(duplicateRequest));

    ListGamesRequest listRequest = new ListGamesRequest(registerResult.authToken());
    Assertions.assertEquals(1, gameService.listGames(listRequest).size());
    var game = new GameData("1", "name", null, "gameName", new ChessGame());
    Assertions.assertTrue(gameService.listGames(listRequest).contains(game));
  }

  @Test
  void listMultipleGames() throws ResponseException {
    //initialize multiple games
    DataAccess database = new MemoryDataAccess();
    UserService userService = new UserService(database);
    var registerRequest = new RegisterRequest("name", "password", "email");
    var registerResult = userService.register(registerRequest);
    var createRequest = new CreateGameRequest(registerResult.authToken(), "gameName");
    GameService gameService = new GameService(database);
    Assertions.assertDoesNotThrow(() -> gameService.createGame(createRequest));

    JoinGameRequest request = new JoinGameRequest(registerResult.authToken(), ChessGame.TeamColor.WHITE, "1");
    Assertions.assertDoesNotThrow(() -> gameService.joinGame(request));

    var createRequest1 = new CreateGameRequest(registerResult.authToken(), "game1");
    var createRequest2 = new CreateGameRequest(registerResult.authToken(), "game2");
    var createRequest3 = new CreateGameRequest(registerResult.authToken(), "game3");
    var createRequest4 = new CreateGameRequest(registerResult.authToken(), "game4");
    Assertions.assertDoesNotThrow(() -> gameService.createGame(createRequest1));
    Assertions.assertDoesNotThrow(() -> gameService.createGame(createRequest2));
    Assertions.assertDoesNotThrow(() -> gameService.createGame(createRequest3));
    Assertions.assertDoesNotThrow(() -> gameService.createGame(createRequest4));



    ListGamesRequest listRequest = new ListGamesRequest(registerResult.authToken());
    Assertions.assertEquals(5, gameService.listGames(listRequest).size());
    var game = new GameData("1", "name", null, "gameName", new ChessGame());
    var game1 = new GameData("2", null, null, "game1", new ChessGame());
    var game2 = new GameData("3", null, null, "game2", new ChessGame());
    var game3 = new GameData("4", null, null, "game3", new ChessGame());
    var game4 = new GameData("5", null, null, "game4", new ChessGame());


    Assertions.assertTrue(gameService.listGames(listRequest).contains(game));
    Assertions.assertTrue(gameService.listGames(listRequest).contains(game1));
    Assertions.assertTrue(gameService.listGames(listRequest).contains(game2));
    Assertions.assertTrue(gameService.listGames(listRequest).contains(game3));
    Assertions.assertTrue(gameService.listGames(listRequest).contains(game4));
  }

  @Test
  void listNoGames() throws ResponseException {
    DataAccess database = new MemoryDataAccess();
    UserService userService = new UserService(database);
    var registerRequest = new RegisterRequest("name", "password", "email");
    var registerResult = userService.register(registerRequest);
    GameService gameService = new GameService(database);

    ListGamesRequest listRequest = new ListGamesRequest(registerResult.authToken());
    Assertions.assertEquals(0, gameService.listGames(listRequest).size());
  }
}
