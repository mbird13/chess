package client;

import dataaccess.DataAccessException;
import dataaccess.SqlDataAccess;
import exception.ResponseException;
import model.UserData;
import org.junit.jupiter.api.*;
import server.Server;
import ServerFacade.ServerFacade;
import servicehelpers.LoginRequest;
import servicehelpers.RegisterRequest;

public class ServerFacadeTests {

    private static Server server;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }


    @Test
    public void registerSuccess() throws ResponseException, DataAccessException {
        var facade = new ServerFacade("http://localhost:8080");
        var database = new SqlDataAccess();

        database.clear();
        Assertions.assertDoesNotThrow(() -> facade.register(new RegisterRequest("ExistingUser", "password", "email")));
        Assertions.assertEquals("email", database.getUser("ExistingUser").email());

        Assertions.assertDoesNotThrow(() -> facade.register(new RegisterRequest("SecondUser", "password", "secondEmail")));
        Assertions.assertEquals("secondEmail", database.getUser("SecondUser").email());
        database.clear();
    }

    @Test
    public void registerFail() throws ResponseException, DataAccessException {
        var facade = new ServerFacade("http://localhost:8080");
        var database = new SqlDataAccess();

        database.clear();
        Assertions.assertDoesNotThrow(() -> facade.register(new RegisterRequest("ExistingUser", "password", "email")));
        Assertions.assertEquals("email", database.getUser("ExistingUser").email());

        //duplicate user
        Assertions.assertThrows(ResponseException.class, () -> facade.register(new RegisterRequest("ExistingUser", "password", "secondEmail")));
        Assertions.assertEquals("email", database.getUser("ExistingUser").email());
        database.clear();
    }

    @Test
    public void loginSuccess() throws ResponseException, DataAccessException {
        var facade = new ServerFacade("http://localhost:8080");
        var database = new SqlDataAccess();

        database.clear();
        Assertions.assertDoesNotThrow(() -> facade.register(new RegisterRequest("ExistingUser", "password", "email")));
        Assertions.assertEquals("email", database.getUser("ExistingUser").email());

        var auth = Assertions.assertDoesNotThrow(() -> facade.login(new LoginRequest("ExistingUser", "password")));
        Assertions.assertEquals("email", database.getUser("ExistingUser").email());
        Assertions.assertEquals("ExistingUser", database.getAuth(auth.authToken()).username());
        database.clear();
    }

    @Test
    public void loginFail() throws ResponseException, DataAccessException {
        var facade = new ServerFacade("http://localhost:8080");
        var database = new SqlDataAccess();

        database.clear();
        Assertions.assertDoesNotThrow(() -> facade.register(new RegisterRequest("ExistingUser", "password", "email")));
        Assertions.assertEquals("email", database.getUser("ExistingUser").email());

        var auth = Assertions.assertDoesNotThrow(() -> facade.login(new LoginRequest("ExistingUser", "password")));
        Assertions.assertEquals("email", database.getUser("ExistingUser").email());
        Assertions.assertEquals("ExistingUser", database.getAuth(auth.authToken()).username());
        database.clear();
    }

}
