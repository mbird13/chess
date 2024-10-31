package dataaccess;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;
import chess.InvalidMoveException;
import exception.ResponseException;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

public class DataAccessTests {

  public static Stream<Arguments> dataAccessImplementations() throws ResponseException, DataAccessException {
    return Stream.of(
            Arguments.of(new MemoryDataAccess()),
            Arguments.of(new SqlDataAccess())
    );
  }

  @ParameterizedTest
  @MethodSource("dataAccessImplementations")
  void clear(DataAccess database) throws ResponseException {
    Assertions.assertDoesNotThrow(database::clear);
    database.createUser("name", "password", "email");
    Assertions.assertEquals(new UserData("name", "password", "email"), database.getUser("name"));
    database.createUser("2", "2", "@");
    database.createGame("game");
    database.createGame("2");
    database.createGame("pp");
    database.createAuth(new AuthData("token", "name"));
    database.createAuth(new AuthData("2", "2"));

    Assertions.assertDoesNotThrow(database::clear);

    Assertions.assertNull(database.getUser("name"));
    Assertions.assertEquals(0, database.listGames().size());
  }

  @ParameterizedTest
  @MethodSource("dataAccessImplementations")
  void createUser(DataAccess database) throws ResponseException {
    Assertions.assertDoesNotThrow(database::clear);

    Assertions.assertDoesNotThrow(() -> database.createUser("name", "password", "email"));
    Assertions.assertEquals(new UserData("name", "password", "email"), database.getUser("name"));
    Assertions.assertDoesNotThrow(() -> database.createUser("2", "2", "@"));
    Assertions.assertEquals(new UserData("2", "2", "@"), database.getUser("2"));
  }

  @ParameterizedTest
  @MethodSource("dataAccessImplementations")
  void getUser(DataAccess database) throws ResponseException {
    Assertions.assertDoesNotThrow(database::clear);

    Assertions.assertDoesNotThrow(() -> database.createUser("name", "password", "email"));
    Assertions.assertEquals(new UserData("name", "password", "email"), database.getUser("name"));
    Assertions.assertDoesNotThrow(() -> database.createUser("2", "2", "@"));
    Assertions.assertEquals(new UserData("name", "password", "email"), database.getUser("name"));
    Assertions.assertEquals(new UserData("2", "2", "@"), database.getUser("2"));
  }

  @ParameterizedTest
  @MethodSource("dataAccessImplementations")
  void createGame(DataAccess database) throws ResponseException {
    Assertions.assertDoesNotThrow(database::clear);

    Assertions.assertDoesNotThrow(() -> database.createGame("game1"));
    var game1 = database.getGame("1");
    Assertions.assertEquals("game1", game1.gameName());
    Assertions.assertEquals(new ChessGame(), game1.game());
    Assertions.assertNull(game1.blackUsername());
    Assertions.assertDoesNotThrow(() -> database.createGame("game2"));
  }

  @ParameterizedTest
  @MethodSource("dataAccessImplementations")
  void getGame(DataAccess database) throws ResponseException {
    Assertions.assertDoesNotThrow(database::clear);

    Assertions.assertDoesNotThrow(() -> database.createGame("game1"));
    Assertions.assertDoesNotThrow(() -> database.createGame("game2"));
    var game1 = database.getGame("1");
    Assertions.assertEquals("game1", game1.gameName());
    Assertions.assertEquals(new ChessGame(), game1.game());
    Assertions.assertNull(game1.blackUsername());
    var game2 = database.getGame("2");
    Assertions.assertEquals("game2", game2.gameName());
    Assertions.assertEquals(new ChessGame(), game2.game());
    Assertions.assertNull(game2.blackUsername());
  }

  @ParameterizedTest
  @MethodSource("dataAccessImplementations")
    void getManyGames(DataAccess database) throws ResponseException {
    Assertions.assertDoesNotThrow(database::clear);

    for (int i = 1; i < 20; i++) {
      int finalI=i;
      Assertions.assertDoesNotThrow(() -> database.createGame(String.valueOf(finalI)));
    }

    for (int i = 1; i < 20; i++) {
      var game = database.getGame(String.valueOf(i));
      Assertions.assertEquals(String.valueOf(i), game.gameName());
      Assertions.assertEquals(new ChessGame(), game.game());
      Assertions.assertNull(game.blackUsername());
    }
  }

  @ParameterizedTest
  @MethodSource("dataAccessImplementations")
  void listGames(DataAccess database) {
    Assertions.assertDoesNotThrow(database::clear);

    for (int i = 1; i <= 200; i++) {
      int finalI=i;
      Assertions.assertDoesNotThrow(() -> database.createGame(String.valueOf(finalI)));
    }

    var games = Assertions.assertDoesNotThrow(database::listGames);
    Assertions.assertEquals(200, games.size());

    for (int i = 1; i <= 200; i++) {
      var game = new GameData(Integer.toString(i), null, null, Integer.toString(i), new ChessGame());
      Assertions.assertTrue(games.contains(game));
    }
  }

  @ParameterizedTest
  @MethodSource("dataAccessImplementations")
  void listNoGames(DataAccess database) throws ResponseException {
    Assertions.assertDoesNotThrow(database::clear);
    database.clear();
    var games = Assertions.assertDoesNotThrow(database::listGames);
    Assertions.assertEquals(0, games.size());
  }

  @ParameterizedTest
  @MethodSource("dataAccessImplementations")
  void updateGame(DataAccess database) throws InvalidMoveException, ResponseException {
    Assertions.assertDoesNotThrow(database::clear);
    for (int i = 1; i <= 200; i++) {
      int finalI=i;
      Assertions.assertDoesNotThrow(() -> database.createGame(String.valueOf(finalI)));
    }

    var game20 = database.getGame("20");
    var expected = new GameData("20", null, null, "20", new ChessGame());
    Assertions.assertEquals(expected, game20);

    var updatedChessGame = new ChessGame();
    updatedChessGame.makeMove(new ChessMove(new ChessPosition(2,3), new ChessPosition(3, 3), null));
    var newGameData = new GameData("20", "white", "black", "20", updatedChessGame);
    database.updateGame("20", newGameData);

    var newRetrievedGame = database.getGame("20");
    Assertions.assertEquals(newGameData, newRetrievedGame);

    Assertions.assertDoesNotThrow(() ->
            newRetrievedGame.game().makeMove(new ChessMove(new ChessPosition(7,3), new ChessPosition(6, 3), null)));
  }

  @ParameterizedTest
  @MethodSource("dataAccessImplementations")
  void createAuth(DataAccess database) {
    Assertions.assertDoesNotThrow(database::clear);
    var auth = new AuthData("token", "name");
    Assertions.assertDoesNotThrow(() -> database.createAuth(auth));
  }

  @ParameterizedTest
  @MethodSource("dataAccessImplementations")
  void createDuplicateAuth(DataAccess database) {
    Assertions.assertDoesNotThrow(database::clear);
    var auth = new AuthData("token", "name");
    Assertions.assertDoesNotThrow(() -> database.createAuth(auth));

    Assertions.assertThrows(ResponseException.class, () -> database.createAuth(auth));
  }



  @ParameterizedTest
  @MethodSource("dataAccessImplementations")
  void getAuth(DataAccess database) {
    Assertions.assertDoesNotThrow(database::clear);
    var auth = new AuthData("token", "name");
    Assertions.assertDoesNotThrow(() -> database.createAuth(auth));

    var storedAuth = Assertions.assertDoesNotThrow(() -> database.getAuth(auth.authToken()));
    Assertions.assertEquals(auth, storedAuth);
  }

  @ParameterizedTest
  @MethodSource("dataAccessImplementations")
  void getNullAuth(DataAccess database) {
    Assertions.assertDoesNotThrow(database::clear);
    var auth = new AuthData("token", "name");
    var storedAuth = Assertions.assertDoesNotThrow(() -> database.getAuth(auth.authToken()));

    Assertions.assertNull(storedAuth);
  }


  @ParameterizedTest
  @MethodSource("dataAccessImplementations")
  void deleteAuthData(DataAccess database) {
    Assertions.assertDoesNotThrow(database::clear);
    var auth = new AuthData("token", "name");
    Assertions.assertDoesNotThrow(() -> database.createAuth(auth));

    var storedAuth = Assertions.assertDoesNotThrow(() -> database.getAuth(auth.authToken()));
    Assertions.assertEquals(auth, storedAuth);

    Assertions.assertDoesNotThrow(() -> database.deleteAuthData(storedAuth));

    var nullAuth = Assertions.assertDoesNotThrow(() -> database.getAuth(auth.authToken()));

    Assertions.assertNull(nullAuth);
  }

}
