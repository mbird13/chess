package server;

import service.ServiceHandler;
import spark.*;
import Exception.ResponseException;


public class Server {

    private final ServiceHandler chessService;

    public Server() {
        chessService = new ServiceHandler();
    }

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        // Register your endpoints and handle exceptions here.
        Spark.delete("/db", this::clear);
        Spark.post("/user", this::register);
        Spark.post("/session", this::login);
        Spark.delete("/session", this::logout);

        Spark.exception(ResponseException.class, this::exceptionHandler);

        //This line initializes the server and can be removed once you have a functioning endpoint 
        Spark.init();

        Spark.awaitInitialization();
        return Spark.port();
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

    private Object clear(Request request, Response response) {
        chessService.clear();
        response.status(200);
        return "";
    }

    private void exceptionHandler(ResponseException exception, Request request, Response response) {
        response.status(exception.StatusCode());
        response.body(String.format("{ \"message\": \"%s\" }", exception.getMessage()));
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }
}
