package dataaccess;

import model.AuthData;
import model.GameData;
import model.UserData;

import java.util.List;

public interface DataAccess {

//  clear: A method for clearing all data from the database. This is used during testing.
  void clear();
//createUser: Create a new user.
  UserData createUser(String username, String password);
//getUser: Retrieve a user with the given username.
  UserData getUser(String username);
//createGame: Create a new game.
  GameData createGame();
//getGame: Retrieve a specified game with the given game ID.
  GameData getGame(int gameID);
//listGames: Retrieve all games.
  List<GameData> listGames();
//updateGame: Updates a chess game. It should replace the chess game string corresponding to a given gameID. This is used when players join a game or when a move is made.
  GameData updateGame(int gameID); //TODO: what is chess game string?
//createAuth: Create a new authorization.
  void createAuth(AuthData authData);
//getAuth: Retrieve an authorization given an authToken.
  AuthData getAuth(String authToken);
//deleteAuth: Delete an authorization so that it is no longer valid.
  void deleteAuthData(UserData user);
}
