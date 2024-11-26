package server;

import service.ServiceHandler;
import spark.*;
import exception.ResponseException;


public class Server {

    private final ServiceHandler chessService;
    private final WebSocketHandler webSocketHandler;

    public Server() {
        chessService = new ServiceHandler();
        webSocketHandler = new WebSocketHandler();
    }

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        // Register your endpoints and handle exceptions here.
        Spark.webSocket("/ws", webSocketHandler);

        Spark.delete("/db", this::clear);
        Spark.post("/user", this::register);
        Spark.post("/session", this::login);
        Spark.delete("/session", this::logout);
        Spark.post("/game", this::createGame);
        Spark.put("/game", this::joinGame);
        Spark.put("/leave_game", this::leaveGame);
        Spark.get("/game", this::listGames);

        Spark.exception(ResponseException.class, this::exceptionHandler);

        //This line initializes the server and can be removed once you have a functioning endpoint 
        Spark.init();

        Spark.awaitInitialization();
        return Spark.port();
    }

    private Object leaveGame(Request request, Response response) throws ResponseException {
        chessService.leaveGame(request);
        response.status(200);
        return "";
    }

    private Object listGames(Request request, Response response) throws ResponseException {
        Object games = chessService.listGames(request);
        response.status(200);
        return games;
    }

    private Object joinGame(Request request, Response response) throws ResponseException {
        chessService.joinGame(request);
        response.status(200);
        return "";
    }

    private Object createGame(Request request, Response response) throws ResponseException {
        Object gameResponse = chessService.createGame(request);
        response.status(200);
        return gameResponse;
    }

    private Object logout(Request request, Response response) throws ResponseException {
        chessService.logout(request);
        response.status(200);
        return "";
    }


    private Object login(Request request, Response response) throws ResponseException {
        Object loginResponse = chessService.login(request);
        response.status(200);
        return loginResponse;
    }

    private Object register(Request request, Response response) throws ResponseException {
        Object loginResponse = chessService.register(request);
        response.status(200);
        return loginResponse;
    }

    private Object clear(Request request, Response response) throws ResponseException {
        chessService.clear();
        response.status(200);
        return "";
    }

    private void exceptionHandler(ResponseException exception, Request request, Response response) {
        response.status(exception.statusCode());
        response.body(String.format("{ \"message\": \"%s\" }", exception.getMessage()));
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }
}
