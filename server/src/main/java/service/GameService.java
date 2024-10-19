package service;

import dataaccess.DataAccess;
import model.AuthData;
import Exception.ResponseException;
import model.GameData;

public class GameService implements Service {

  private DataAccess database;

  public GameService(DataAccess database) {
    this.database = database;
  }

  public CreateGameResponse createGame(CreateGameRequest gameRequest) throws ResponseException {
    AuthData auth = database.getAuth(gameRequest.authToken());
    if (auth == null) {
      throw new ResponseException(401, "Error: unauthorized");
    }
    GameData gameData = database.createGame(gameRequest.gameName());
    return new CreateGameResponse(gameData.gameID());
  }
}

record CreateGameRequest(String authToken, String gameName) {}
record CreateGameResponse(String gameID){}
