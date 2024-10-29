package dataaccess;

import exception.ResponseException;
import model.AuthData;
import model.GameData;
import model.UserData;

import java.lang.module.ResolutionException;
import java.sql.SQLException;
import java.util.Collection;

public class SqlDataAccess implements DataAccess {

  private final String[] createStatements = {
          """
          CREATE TABLE IF NOT EXISTS  user (
              `username` varchar(256) NOT NULL,
              `password` varchar(256) NOT NULL,
              `email` varchar(256) NOT NULL,
              PRIMARY KEY (`username`),
            )""",
          """
          CREATE TABLE IF NOT EXISTS  auth (
              `id` int NOT NULL AUTO_INCREMENT,
              `username` varchar(256) NOT NULL,
              `auth_token` varchar(256) NOT NULL,
              PRIMARY KEY (`username`),
              INDEX(authtoken),
            )""",
          """
           CREATE TABLE IF NOT EXISTS  game (
              `id` int NOT NULL AUTO_INCREMENT,
              `name` varchar(256) NOT NULL,
              `white_username` varchar(256) DEFAULT NULL,
              `black_username` varchar(256) DEFAULT NULL,
              'game' TEXT DEFAULT NULL,
              PRIMARY KEY (`id`),
              INDEX(type),
              INDEX(name)
            )"""
  };

  SqlDataAccess() throws DataAccessException, ResponseException {
    configureDatabase();
  }

  private void configureDatabase() throws DataAccessException, ResponseException {
    DatabaseManager.createDatabase();
    try (var connection = DatabaseManager.getConnection()) {
      for (var statement : createStatements) {
        try (var preparedStatement = connection.prepareStatement(statement)) {
          preparedStatement.executeUpdate();
        }
      }
    } catch (SQLException e) {
      throw new ResponseException(500, String.format("Unable to configure database: %s", e.getMessage()));
    }

  }

  @Override
  public void clear() {

  }

  @Override
  public UserData createUser(String username, String password, String email) {
    return null;
  }

  @Override
  public UserData getUser(String username) {
    return null;
  }

  @Override
  public GameData createGame(String gameName) {
    return null;
  }

  @Override
  public GameData getGame(String gameID) {
    return null;
  }

  @Override
  public Collection<GameData> listGames() {
    return null;
  }

  @Override
  public GameData updateGame(String gameID, GameData newGameData) {
    return null;
  }

  @Override
  public void createAuth(AuthData authData) {

  }

  @Override
  public AuthData getAuth(String authToken) {
    return null;
  }

  @Override
  public void deleteAuthData(AuthData authData) {

  }
}
