package service;

import com.google.gson.Gson;
import dataaccess.DataAccess;
import dataaccess.MemoryDataAccess;
import dataaccess.SqlDataAccess;
import model.GameData;
import service.*;
import servicehelpers.*;
import spark.Request;
import exception.ResponseException;

import java.util.Collection;

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
    try {
      database=new SqlDataAccess();
    } catch (Exception e) {
      database=new MemoryDataAccess();
    }
    userService = new UserService(database);
    gameService = new GameService(database);
  }

  public Object register(Request request) throws ResponseException {
    var user = new Gson().fromJson(request.body(), RegisterRequest.class);
    LoginResult result = userService.register(user);
    return new Gson().toJson(result);
  }

  public void clear() throws ResponseException {
    database.clear();
  }

  public Object login(Request request) throws ResponseException {
    var user = new Gson().fromJson(request.body(), LoginRequest.class);
    LoginResult result = userService.login(user);
    return new Gson().toJson(result);
  }

  public void logout(Request request) throws ResponseException {
    LogoutRequest logoutRequest = new LogoutRequest(request.headers("authorization"));
    userService.logout(logoutRequest);
  }

  public Object createGame(Request request) throws ResponseException {
    CreateGameRequest tempGameRequest = new Gson().fromJson(request.body(), CreateGameRequest.class);
    CreateGameRequest gameRequest = new CreateGameRequest(request.headers("authorization"), tempGameRequest.gameName());
    CreateGameResponse gameResponse = gameService.createGame(gameRequest);
    return new Gson().toJson(gameResponse);
  }

  public void joinGame(Request request) throws ResponseException {
    JoinGameRequest tempRequest = new Gson().fromJson(request.body(), JoinGameRequest.class);
    JoinGameRequest joinGameRequest = new JoinGameRequest(request.headers("authorization"), tempRequest.playerColor(), tempRequest.gameID());
    gameService.joinGame(joinGameRequest);
  }

  public Object listGames(Request request) throws ResponseException {
    ListGamesRequest listGamesRequest = new ListGamesRequest(request.headers("authorization"));
    Collection<GameData> games = gameService.listGames(listGamesRequest);
    var wrapper = new GameListWrapper(games);
    return new Gson().toJson(wrapper);
  }

  public void leaveGame(Request request) throws ResponseException {
    String authToken = request.headers("authorization");
    String gameId = new Gson().fromJson(request.body(), String.class);
    gameService.leaveGame(authToken, gameId);

  }
}

