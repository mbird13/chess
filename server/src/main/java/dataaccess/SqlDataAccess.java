package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import exception.ResponseException;
import model.AuthData;
import model.GameData;
import model.UserData;
import service.GameService;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
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

  private int executeUpdate(String statement, Object... params) throws ResponseException {
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

        var resultSet = preparedStatement.getGeneratedKeys();
        if (resultSet.next()) {
          return resultSet.getInt(1);
        }
      }

      return 0;

    } catch (DataAccessException | SQLException e) {
      throw new ResponseException(500, String.format("unable to update database: %s, %s", statement, e.getMessage()));
    }
  }

  @Override
  public void clear() throws ResponseException {
    var statement = "DROP DATABASE IF EXISTS chess;";
    executeUpdate(statement);
    try {
      configureDatabase();
    } catch (DataAccessException e) {
      throw new ResponseException(500, "error in creating database");
    }
  }

  @Override
  public UserData createUser(String username, String password, String email) throws ResponseException {
    var statement = "INSERT INTO user (username, password, email)\n" +
            "VALUES (?, ?, ?);";
    executeUpdate(statement, username, password, email);
    return new UserData(username, password, email);
  }

  @Override
  public UserData getUser(String username) throws ResponseException {
    var statement = "SELECT * FROM user WHERE username=?";
    try (var connection = DatabaseManager.getConnection()) {
      try (var preparedStatement=connection.prepareStatement(statement)) {
        preparedStatement.setString(1, username);
        try (var results=preparedStatement.executeQuery()) {
          if (results.next()) {
            return readUser(results);
          }
        }
      }
    } catch (Exception e) {
      throw new ResponseException(500, String.format("Unable to read data: %s", e.getMessage()));
    }
    return null;
  }

  @Override
  public GameData createGame(String gameName) throws ResponseException {
    var statement = "INSERT INTO game (name, game) VALUES (?, ?);";
    var game = new ChessGame();
    var jsonGame = new Gson().toJson(game, ChessGame.class);
    var id = executeUpdate(statement, gameName, jsonGame);

    return new GameData(Integer.toString(id), gameName, null, null, game);
  }

  @Override
  public GameData getGame(String gameID) throws ResponseException {
    var statement = "SELECT * FROM game WHERE id=?";
    try (var connection = DatabaseManager.getConnection()) {
      try (var preparedStatement = connection.prepareStatement(statement)) {
        preparedStatement.setInt(1, Integer.parseInt(gameID));

        try (var results = preparedStatement.executeQuery()) {
          if (results.next()) {
            return readGame(results);
          }
        }
      }
    } catch (Exception e) {
      throw new ResponseException(500, String.format("Unable to read data: %s", e.getMessage()));
    }
    return null;
  }

  private GameData readGame(ResultSet results) throws SQLException {
    var gameID = results.getInt("id");
    var gameName = results.getString("name");
    var whiteUser = results.getString("white_username");
    var blackUser = results.getString("black_username");
    var gameJson = results.getString("game");

    var game = new Gson().fromJson(gameJson, ChessGame.class);

    return new GameData(Integer.toString(gameID), whiteUser, blackUser, gameName, game);
  }

  @Override
  public Collection<GameData> listGames() throws ResponseException {
    var games = new ArrayList<GameData>();
    var statement = "SELECT * FROM game";
    try (var connection = DatabaseManager.getConnection()) {
      try (var preparedStatement = connection.prepareStatement(statement)) {
        var resultSet = preparedStatement.executeQuery();

        while (resultSet.next()) {
          games.add(readGame(resultSet));
        }
        return games;
      }
    } catch (Exception e) {
      throw new ResponseException(500, "unable to access database");
    }
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

  private UserData readUser(ResultSet results) throws SQLException {
    var username = results.getString("username");
    var password = results.getString("password");
    var email = results.getString("email");
    return new UserData(username, password, email);
  }
}
