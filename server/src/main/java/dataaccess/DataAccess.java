package dataaccess;

import exception.ResponseException;
import model.AuthData;
import model.GameData;
import model.UserData;

import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

public interface DataAccess {

//  clear: A method for clearing all data from the database. This is used during testing.
  void clear() throws ResponseException;
//createUser: Create a new user.
  UserData createUser(String username, String password, String email) throws ResponseException;
//getUser: Retrieve a user with the given username.
  UserData getUser(String username) throws ResponseException;
//createGame: Create a new game.
  GameData createGame(String gameName) throws ResponseException;
//getGame: Retrieve a specified game with the given game ID.
  GameData getGame(String gameID) throws ResponseException;
//listGames: Retrieve all games.
  Collection<GameData> listGames() throws ResponseException;
//updateGame: Updates a chess game. It should replace the chess game string corresponding to a given gameID.
// This is used when players join a game or when a move is made.
  GameData updateGame(String gameID, GameData newGameData) throws ResponseException;
//createAuth: Create a new authorization.
  void createAuth(AuthData authData);
//getAuth: Retrieve an authorization given an authToken.
  AuthData getAuth(String authToken);
//deleteAuth: Delete an authorization so that it is no longer valid.
  void deleteAuthData(AuthData authData);
}
