package dataaccess;

import model.AuthData;
import model.GameData;
import model.UserData;

import java.util.*;

public class MemoryDataAccess implements DataAccess {
  private HashMap<String, UserData> users;
  private HashMap<String, AuthData> authDataSet;
  private HashMap<String, GameData> games;

  public MemoryDataAccess() {
    users = new HashMap<>();
    authDataSet = new HashMap<>();
    games = new HashMap<>();
  }

  //  clear: A method for clearing all data from the database. This is used during testing.
  public void clear() {
    users.clear();
    authDataSet.clear();
    games.clear();
  }
  //createUser: Create a new user.
  public UserData createUser(String username, String password, String email) {
    UserData newUser = new UserData(username, password, email);
    users.put(username, newUser);
    return newUser;
  }
  //getUser: Retrieve a user with the given username.
  public UserData getUser(String username){
    return users.get(username);
  }
  //createGame: Create a new game.
  public GameData createGame(){
    return null;
  }
  //getGame: Retrieve a specified game with the given game ID.
  public GameData getGame(int gameID){
    return null;
  }
  //listGames: Retrieve all games.
  public List<GameData> listGames(){
    return null;
  }
  //updateGame: Updates a chess game. It should replace the chess game string corresponding to a given gameID. This is used when players join a game or when a move is made.
  public GameData updateGame(int gameID){
    return null;
  } //TODO: what is chess game string?
  //createAuth: Create a new authorization.
  public void createAuth(AuthData authData){
    this.authDataSet.put(authData.authToken(), authData);
  }
  //getAuth: Retrieve an authorization given an authToken.
  public AuthData getAuth(String authToken){
    return authDataSet.get(authToken);
  }
  //deleteAuth: Delete an authorization so that it is no longer valid.
  public void deleteAuthData(AuthData authData) {
    authDataSet.remove(authData.authToken());
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    MemoryDataAccess that=(MemoryDataAccess) o;
    return Objects.equals(users, that.users) && Objects.equals(authDataSet, that.authDataSet) && Objects.equals(games, that.games);
  }

  @Override
  public int hashCode() {
    return Objects.hash(users, authDataSet, games);
  }
}
