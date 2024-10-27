package dataaccess;

import chess.ChessGame;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import javax.xml.crypto.Data;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.stream.Stream;

public class DataAccessTests {

  public static Stream<Arguments> DataAccessImplementations() {
    return Stream.of(
            Arguments.of(new MemoryDataAccess())
    );
  }

  @ParameterizedTest
  @MethodSource("DataAccessImplementations")
  void clear(DataAccess database) {
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
  @MethodSource("DataAccessImplementations")
  void createUser(DataAccess database) {
    Assertions.assertDoesNotThrow(() -> database.createUser("name", "password", "email"));
    Assertions.assertEquals(new UserData("name", "password", "email"), database.getUser("name"));
    Assertions.assertDoesNotThrow(() -> database.createUser("2", "2", "@"));
    Assertions.assertEquals(new UserData("2", "2", "@"), database.getUser("2"));
  }

  @ParameterizedTest
  @MethodSource("DataAccessImplementations")
  void getUser(DataAccess database) {
    Assertions.assertDoesNotThrow(() -> database.createUser("name", "password", "email"));
    Assertions.assertEquals(new UserData("name", "password", "email"), database.getUser("name"));
    Assertions.assertDoesNotThrow(() -> database.createUser("2", "2", "@"));
    Assertions.assertEquals(new UserData("name", "password", "email"), database.getUser("name"));
    Assertions.assertEquals(new UserData("2", "2", "@"), database.getUser("2"));
  }

  @ParameterizedTest
  @MethodSource("DataAccessImplementations")
  void createGame(DataAccess database) {
    Assertions.assertDoesNotThrow(() -> database.createGame("game1"));
    var game1 = database.getGame("1");
    Assertions.assertEquals("game1", game1.gameName());
    Assertions.assertEquals(new ChessGame(), game1.game());
    Assertions.assertNull(game1.blackUsername());
    Assertions.assertDoesNotThrow(() -> database.createGame("game2"));
  }

  @ParameterizedTest
  @MethodSource("DataAccessImplementations")
  void getGame(DataAccess database) {
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
  @MethodSource("DataAccessImplementations")
  void listGames(DataAccess database) {

  }

  @ParameterizedTest
  @MethodSource("DataAccessImplementations")
  void updateGame(DataAccess database) {

  }

  @ParameterizedTest
  @MethodSource("DataAccessImplementations")
  void createAuth(DataAccess database) {

  }

  @ParameterizedTest
  @MethodSource("DataAccessImplementations")
  void getAuth(DataAccess database) {

  }

  @ParameterizedTest
  @MethodSource("DataAccessImplementations")
  void deleteAuthData(DataAccess database) {

  }

}
