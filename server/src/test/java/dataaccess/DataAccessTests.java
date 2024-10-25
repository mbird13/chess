package dataaccess;

import model.GameData;
import model.UserData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import javax.xml.crypto.Data;
import java.util.ArrayList;
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

    database.clear();

    Assertions.assertNull(database.getUser("name"));
    Assertions.assertEquals(0, database.listGames().size());
  }

}
