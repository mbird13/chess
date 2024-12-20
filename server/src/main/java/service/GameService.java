package service;

import chess.ChessGame;
import dataaccess.DataAccess;
import model.AuthData;
import exception.ResponseException;
import model.GameData;
import servicehelpers.CreateGameRequest;
import servicehelpers.CreateGameResponse;
import servicehelpers.JoinGameRequest;
import servicehelpers.ListGamesRequest;

import java.util.Collection;
import java.util.Objects;

public class GameService implements Service {

  private DataAccess database;

  public GameService(DataAccess database) {
    this.database = database;
  }

  public CreateGameResponse createGame(CreateGameRequest gameRequest) throws ResponseException {

    verifyAuthToken(gameRequest.authToken());

    GameData gameData = database.createGame(gameRequest.gameName());
    return new CreateGameResponse(gameData.gameID());
  }

  public void leaveGame(String authToken, String gameId) throws ResponseException {
    verifyAuthToken(authToken);
    AuthData user = database.getAuth(authToken);

    GameData oldGameData;
    try {
      oldGameData = database.getGame(gameId);
    } catch (ResponseException e) {
      throw new ResponseException(400, "Error: bad request");
    }
    if (oldGameData == null) {
      throw new ResponseException(400, "Error: bad request");
    }
    GameData newGameData;
    if (Objects.equals(oldGameData.whiteUsername(), user.username())) {
      newGameData = new GameData(oldGameData.gameID(),
              null, oldGameData.blackUsername(), oldGameData.gameName(), oldGameData.game());
    } else if (Objects.equals(oldGameData.blackUsername(), user.username())) {
      newGameData = new GameData(oldGameData.gameID(),
              oldGameData.whiteUsername(), null, oldGameData.gameName(), oldGameData.game());
    } else {
      throw new ResponseException(400, "Error: bad request");
    }

    database.updateGame(gameId, newGameData);

  }

  public void joinGame(JoinGameRequest joinGameRequest) throws ResponseException {

    verifyAuthToken(joinGameRequest.authToken());
    AuthData user = database.getAuth(joinGameRequest.authToken());

    GameData oldGameData;
    try {
      oldGameData = database.getGame(joinGameRequest.gameID());
    } catch (ResponseException e) {
      throw new ResponseException(400, "Error: bad request");
    }
    if (oldGameData == null) {
      throw new ResponseException(400, "Error: bad request");
    }
    GameData newGameData;
    if (Objects.equals(joinGameRequest.playerColor(), ChessGame.TeamColor.BLACK)) {
      if (oldGameData.blackUsername() != null) {
        throw new ResponseException(403, "Error: already taken");
      }
      newGameData = new GameData(oldGameData.gameID(), oldGameData.whiteUsername(), user.username(), oldGameData.gameName(), oldGameData.game());
    }
    else if (Objects.equals(joinGameRequest.playerColor(), ChessGame.TeamColor.WHITE)){
      if (oldGameData.whiteUsername() != null) {
        throw new ResponseException(403, "Error: already taken");
      }
      newGameData = new GameData(oldGameData.gameID(), user.username(), oldGameData.blackUsername(), oldGameData.gameName(), oldGameData.game());
    }
    else {
      throw new ResponseException(400, "Error: bad request");
    }

    database.updateGame(joinGameRequest.gameID(), newGameData);
  }

  private void verifyAuthToken(String token) throws ResponseException {
    AuthData authData = database.getAuth(token);
    if (authData == null) {
      throw new ResponseException(401, "Error: unauthorized");
    }
  }

  public Collection<GameData> listGames(ListGamesRequest listGamesRequest) throws ResponseException {
    verifyAuthToken(listGamesRequest.authToken());
    return database.listGames();
  }
}

