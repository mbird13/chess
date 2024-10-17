package server;

import com.google.gson.Gson;
import model.UserData;
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

        //This line initializes the server and can be removed once you have a functioning endpoint 
        Spark.init();

        Spark.awaitInitialization();
        return Spark.port();
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

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }
}
