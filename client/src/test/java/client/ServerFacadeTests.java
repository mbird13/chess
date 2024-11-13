package client;

import chess.ChessGame;
import dataaccess.DataAccessException;
import dataaccess.SqlDataAccess;
import exception.ResponseException;
import model.UserData;
import org.junit.jupiter.api.*;
import server.Server;
import ServerFacade.ServerFacade;
import servicehelpers.*;

public class ServerFacadeTests {

    private static Server server;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
    }

    @BeforeEach
    public void initEach() throws ResponseException, DataAccessException {
        var database = new SqlDataAccess();
        database.clear();
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }


    @Test
    public void registerSuccess() throws ResponseException, DataAccessException {
        var facade = new ServerFacade("http://localhost:8080");
        var database = new SqlDataAccess();

        Assertions.assertDoesNotThrow(() -> facade.register(new RegisterRequest("ExistingUser", "password", "email")));
        Assertions.assertEquals("email", database.getUser("ExistingUser").email());

        Assertions.assertDoesNotThrow(() -> facade.register(new RegisterRequest("SecondUser", "password", "secondEmail")));
        Assertions.assertEquals("secondEmail", database.getUser("SecondUser").email());
    }

    @Test
    public void registerFail() throws ResponseException, DataAccessException {
        var facade = new ServerFacade("http://localhost:8080");
        var database = new SqlDataAccess();

        Assertions.assertDoesNotThrow(() -> facade.register(new RegisterRequest("ExistingUser", "password", "email")));
        Assertions.assertEquals("email", database.getUser("ExistingUser").email());

        //duplicate user
        Assertions.assertThrows(ResponseException.class, () -> facade.register(new RegisterRequest("ExistingUser", "password", "secondEmail")));
        Assertions.assertEquals("email", database.getUser("ExistingUser").email());
    }

    @Test
    public void loginSuccess() throws ResponseException, DataAccessException {
        var facade = new ServerFacade("http://localhost:8080");
        var database = new SqlDataAccess();

        Assertions.assertDoesNotThrow(() -> facade.register(new RegisterRequest("ExistingUser", "password", "email")));
        Assertions.assertEquals("email", database.getUser("ExistingUser").email());

        var auth = Assertions.assertDoesNotThrow(() -> facade.login(new LoginRequest("ExistingUser", "password")));
        Assertions.assertEquals("email", database.getUser("ExistingUser").email());
        Assertions.assertEquals("ExistingUser", database.getAuth(auth.authToken()).username());
    }

    @Test
    public void loginFail() throws ResponseException, DataAccessException {
        var facade = new ServerFacade("http://localhost:8080");
        var database = new SqlDataAccess();

        Assertions.assertDoesNotThrow(() -> facade.register(new RegisterRequest("ExistingUser", "password", "email")));
        Assertions.assertEquals("email", database.getUser("ExistingUser").email());

        var exception = Assertions.assertThrows(ResponseException.class, () -> facade.login(new LoginRequest("ExistingUser", "wrongPassword")));
        Assertions.assertEquals("email", database.getUser("ExistingUser").email());
        Assertions.assertEquals(500, exception.statusCode());
    }

    @Test
    public void logoutSuccess() throws ResponseException, DataAccessException {
        var facade = new ServerFacade("http://localhost:8080");
        var database = new SqlDataAccess();

        Assertions.assertDoesNotThrow(() -> facade.register(new RegisterRequest("ExistingUser", "password", "email")));
        Assertions.assertEquals("email", database.getUser("ExistingUser").email());

        var auth = Assertions.assertDoesNotThrow(() -> facade.login(new LoginRequest("ExistingUser", "password")));
        Assertions.assertEquals("email", database.getUser("ExistingUser").email());
        Assertions.assertEquals("ExistingUser", database.getAuth(auth.authToken()).username());

        Assertions.assertDoesNotThrow(() -> facade.logout(new LogoutRequest(auth.authToken())));
        Assertions.assertNull(database.getAuth(auth.authToken()));
    }

    @Test
    public void logoutFail() throws ResponseException, DataAccessException {
        var facade = new ServerFacade("http://localhost:8080");
        var database = new SqlDataAccess();

        Assertions.assertDoesNotThrow(() -> facade.register(new RegisterRequest("ExistingUser", "password", "email")));
        Assertions.assertEquals("email", database.getUser("ExistingUser").email());

        var auth = Assertions.assertDoesNotThrow(() -> facade.login(new LoginRequest("ExistingUser", "password")));
        Assertions.assertEquals("email", database.getUser("ExistingUser").email());
        Assertions.assertEquals("ExistingUser", database.getAuth(auth.authToken()).username());

        Assertions.assertDoesNotThrow(() -> facade.logout(new LogoutRequest(auth.authToken())));
        Assertions.assertNull(database.getAuth(auth.authToken()));

        var exception = Assertions.assertThrows(ResponseException.class, () -> facade.logout(new LogoutRequest(auth.authToken())));
        Assertions.assertEquals(500, exception.statusCode());
    }

    @Test
    public void createGameSuccess() throws ResponseException, DataAccessException {
        var facade = new ServerFacade("http://localhost:8080");
        var database = new SqlDataAccess();

        Assertions.assertDoesNotThrow(() -> facade.register(new RegisterRequest("ExistingUser", "password", "email")));
        Assertions.assertEquals("email", database.getUser("ExistingUser").email());

        var auth = Assertions.assertDoesNotThrow(() -> facade.login(new LoginRequest("ExistingUser", "password")));
        Assertions.assertEquals("email", database.getUser("ExistingUser").email());
        Assertions.assertEquals("ExistingUser", database.getAuth(auth.authToken()).username());

        Assertions.assertDoesNotThrow(() -> facade.createGame(new CreateGameRequest(auth.authToken(), "game")));
        Assertions.assertEquals("game", database.getGame("1").gameName());
    }

    @Test
    public void createGameFail() throws ResponseException, DataAccessException {
        var facade = new ServerFacade("http://localhost:8080");
        var database = new SqlDataAccess();

        Assertions.assertDoesNotThrow(() -> facade.register(new RegisterRequest("ExistingUser", "password", "email")));
        Assertions.assertEquals("email", database.getUser("ExistingUser").email());

        var auth = Assertions.assertDoesNotThrow(() -> facade.login(new LoginRequest("ExistingUser", "password")));
        Assertions.assertEquals("email", database.getUser("ExistingUser").email());
        Assertions.assertEquals("ExistingUser", database.getAuth(auth.authToken()).username());

        var exception = Assertions.assertThrows(ResponseException.class, () -> facade.createGame(new CreateGameRequest("invalidAuth", "game")));
        Assertions.assertEquals(500, exception.statusCode());
    }

    @Test
    public void listGamesSuccess() throws ResponseException, DataAccessException {
        var facade = new ServerFacade("http://localhost:8080");
        var database = new SqlDataAccess();

        Assertions.assertDoesNotThrow(() -> facade.register(new RegisterRequest("ExistingUser", "password", "email")));
        Assertions.assertEquals("email", database.getUser("ExistingUser").email());

        var auth = Assertions.assertDoesNotThrow(() -> facade.login(new LoginRequest("ExistingUser", "password")));
        Assertions.assertEquals("email", database.getUser("ExistingUser").email());
        Assertions.assertEquals("ExistingUser", database.getAuth(auth.authToken()).username());

        Assertions.assertDoesNotThrow(() -> facade.createGame(new CreateGameRequest(auth.authToken(), "1")));
        Assertions.assertDoesNotThrow(() -> facade.createGame(new CreateGameRequest(auth.authToken(), "2")));
        Assertions.assertDoesNotThrow(() -> facade.createGame(new CreateGameRequest(auth.authToken(), "3")));
        Assertions.assertDoesNotThrow(() -> facade.createGame(new CreateGameRequest(auth.authToken(), "4")));
        Assertions.assertDoesNotThrow(() -> facade.createGame(new CreateGameRequest(auth.authToken(), "5")));

        var games = Assertions.assertDoesNotThrow(() -> facade.listGames(new ListGamesRequest(auth.authToken())));
        Assertions.assertEquals(5, games.games.size());
    }

    @Test
    public void listGamesFail() throws ResponseException, DataAccessException {
        var facade = new ServerFacade("http://localhost:8080");
        var database = new SqlDataAccess();

        Assertions.assertDoesNotThrow(() -> facade.register(new RegisterRequest("ExistingUser", "password", "email")));
        Assertions.assertEquals("email", database.getUser("ExistingUser").email());

        var auth = Assertions.assertDoesNotThrow(() -> facade.login(new LoginRequest("ExistingUser", "password")));
        Assertions.assertEquals("email", database.getUser("ExistingUser").email());
        Assertions.assertEquals("ExistingUser", database.getAuth(auth.authToken()).username());

        Assertions.assertDoesNotThrow(() -> facade.createGame(new CreateGameRequest(auth.authToken(), "1")));
        Assertions.assertDoesNotThrow(() -> facade.createGame(new CreateGameRequest(auth.authToken(), "2")));
        Assertions.assertDoesNotThrow(() -> facade.createGame(new CreateGameRequest(auth.authToken(), "3")));
        Assertions.assertDoesNotThrow(() -> facade.createGame(new CreateGameRequest(auth.authToken(), "4")));
        Assertions.assertDoesNotThrow(() -> facade.createGame(new CreateGameRequest(auth.authToken(), "5")));

        var exception = Assertions.assertThrows(ResponseException.class, () -> facade.listGames(new ListGamesRequest("invalidAuth")));
        Assertions.assertEquals(500, exception.statusCode());
    }

    @Test
    public void joinGameSuccess() throws ResponseException, DataAccessException {
        var facade = new ServerFacade("http://localhost:8080");
        var database = new SqlDataAccess();

        Assertions.assertDoesNotThrow(() -> facade.register(new RegisterRequest("ExistingUser", "password", "email")));
        Assertions.assertEquals("email", database.getUser("ExistingUser").email());

        var auth = Assertions.assertDoesNotThrow(() -> facade.login(new LoginRequest("ExistingUser", "password")));
        Assertions.assertEquals("email", database.getUser("ExistingUser").email());
        Assertions.assertEquals("ExistingUser", database.getAuth(auth.authToken()).username());

        Assertions.assertDoesNotThrow(() -> facade.createGame(new CreateGameRequest(auth.authToken(), "game")));
        Assertions.assertEquals("game", database.getGame("1").gameName());

        Assertions.assertDoesNotThrow(() -> facade.joinGame(new JoinGameRequest(auth.authToken(), ChessGame.TeamColor.WHITE, "1")));
        Assertions.assertEquals("ExistingUser", database.getGame("1").whiteUsername());
        Assertions.assertDoesNotThrow(() -> facade.joinGame(new JoinGameRequest(auth.authToken(), ChessGame.TeamColor.BLACK, "1")));
        Assertions.assertEquals("ExistingUser", database.getGame("1").blackUsername());
    }

    @Test
    public void joinGameFail() throws ResponseException, DataAccessException {
        var facade = new ServerFacade("http://localhost:8080");
        var database = new SqlDataAccess();

        Assertions.assertDoesNotThrow(() -> facade.register(new RegisterRequest("ExistingUser", "password", "email")));
        Assertions.assertEquals("email", database.getUser("ExistingUser").email());

        var auth = Assertions.assertDoesNotThrow(() -> facade.login(new LoginRequest("ExistingUser", "password")));
        Assertions.assertEquals("email", database.getUser("ExistingUser").email());
        Assertions.assertEquals("ExistingUser", database.getAuth(auth.authToken()).username());

        Assertions.assertDoesNotThrow(() -> facade.createGame(new CreateGameRequest(auth.authToken(), "game")));
        Assertions.assertEquals("game", database.getGame("1").gameName());

        Assertions.assertThrows(ResponseException.class, () -> facade.joinGame(new JoinGameRequest("invalidAuth", ChessGame.TeamColor.WHITE, "1")));
        Assertions.assertThrows(ResponseException.class, () -> facade.joinGame(new JoinGameRequest(auth.authToken(), ChessGame.TeamColor.BLACK, "invalidId")));
    }

    @Test
    public void leaveGameSuccess() throws ResponseException, DataAccessException {
        var facade = new ServerFacade("http://localhost:8080");
        var database = new SqlDataAccess();

        Assertions.assertDoesNotThrow(() -> facade.register(new RegisterRequest("ExistingUser", "password", "email")));
        Assertions.assertEquals("email", database.getUser("ExistingUser").email());

        var auth = Assertions.assertDoesNotThrow(() -> facade.login(new LoginRequest("ExistingUser", "password")));
        Assertions.assertEquals("email", database.getUser("ExistingUser").email());
        Assertions.assertEquals("ExistingUser", database.getAuth(auth.authToken()).username());

        Assertions.assertDoesNotThrow(() -> facade.createGame(new CreateGameRequest(auth.authToken(), "game")));
        Assertions.assertEquals("game", database.getGame("1").gameName());

        Assertions.assertDoesNotThrow(() -> facade.joinGame(new JoinGameRequest(auth.authToken(), ChessGame.TeamColor.WHITE, "1")));
        Assertions.assertEquals("ExistingUser", database.getGame("1").whiteUsername());
        Assertions.assertDoesNotThrow(() -> facade.leaveGame("1", auth.authToken()));
        Assertions.assertNull(database.getGame("1").whiteUsername());
    }

    @Test
    public void leaveGameFail() throws ResponseException, DataAccessException {
        var facade = new ServerFacade("http://localhost:8080");
        var database = new SqlDataAccess();

        Assertions.assertDoesNotThrow(() -> facade.register(new RegisterRequest("ExistingUser", "password", "email")));
        Assertions.assertEquals("email", database.getUser("ExistingUser").email());

        var auth = Assertions.assertDoesNotThrow(() -> facade.login(new LoginRequest("ExistingUser", "password")));
        Assertions.assertEquals("email", database.getUser("ExistingUser").email());
        Assertions.assertEquals("ExistingUser", database.getAuth(auth.authToken()).username());

        Assertions.assertDoesNotThrow(() -> facade.createGame(new CreateGameRequest(auth.authToken(), "game")));
        Assertions.assertEquals("game", database.getGame("1").gameName());

        Assertions.assertDoesNotThrow(() -> facade.joinGame(new JoinGameRequest(auth.authToken(), ChessGame.TeamColor.WHITE, "1")));
        Assertions.assertEquals("ExistingUser", database.getGame("1").whiteUsername());
        Assertions.assertThrows(ResponseException.class, () -> facade.leaveGame("invalidID", auth.authToken()));
        Assertions.assertThrows(ResponseException.class, () -> facade.leaveGame("1", "invalidAuth"));
        Assertions.assertEquals("ExistingUser", database.getGame("1").whiteUsername());
    }
}
