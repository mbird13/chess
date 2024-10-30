package dataaccess;

import chess.ChessGame;
import exception.ResponseException;
import model.AuthData;
import model.GameData;
import model.UserData;

import java.lang.module.ResolutionException;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;

import static java.sql.Types.NULL;

public class SqlDataAccess implements DataAccess {

  private final String[] createStatements = {
          """
          CREATE TABLE IF NOT EXISTS  user (
              `username` varchar(256) NOT NULL,
              `password` varchar(256) NOT NULL,
              `email` varchar(256) NOT NULL,
              PRIMARY KEY (`username`)
            )""",
          """
          CREATE TABLE IF NOT EXISTS  auth (
              `username` varchar(256) NOT NULL,
              `auth_token` varchar(256) NOT NULL,
              PRIMARY KEY (`username`),
              INDEX(auth_token)
            )""",
          """
           CREATE TABLE IF NOT EXISTS  game (
              `id` int NOT NULL AUTO_INCREMENT,
              `name` varchar(256) NOT NULL,
              `white_username` varchar(256) DEFAULT NULL,
              `black_username` varchar(256) DEFAULT NULL,
              `game` TEXT DEFAULT NULL,
              PRIMARY KEY (`id`)
            )"""
  };

  public SqlDataAccess() throws DataAccessException, ResponseException {
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

  private void executeStatement(String statement, Object... params) throws ResponseException {
    try (var connection = DatabaseManager.getConnection()) {
      try (var preparedStatement = connection.prepareStatement(statement, Statement.RETURN_GENERATED_KEYS)) {
        for (var i = 0; i < params.length; i++) {
          var param = params[i];
          if (param instanceof String p) preparedStatement.setString(i + 1, p);
          else if (param instanceof Integer p) preparedStatement.setInt(i + 1, p);
          else if (param instanceof ChessGame p) preparedStatement.setString(i + 1, p.toString());
          else if (param == null) preparedStatement.setNull(i + 1, NULL);
        }
        preparedStatement.executeUpdate();
      }
    } catch (DataAccessException | SQLException e) {
      throw new ResponseException(500, String.format("unable to update database: %s, %s", statement, e.getMessage()));
    }
  }

  @Override
  public void clear() throws ResponseException {
    var statement = "DROP DATABASE IF EXISTS chess;";
    executeStatement(statement);
  }

  @Override
  public UserData createUser(String username, String password, String email) throws ResponseException {
    var statement = "INSERT INTO user (username, password, email)\n" +
            "VALUES (?, ?, ?);";
    executeStatement(statement, username, password, email);
    return new UserData(username, password, email);
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
