package server;

import com.google.gson.Gson;
import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import dataaccess.SqlDataAccess;
import service.GameService;
import service.UserService;
import spark.*;
import java.util.Map;
import static spark.Spark.delete;

public class Server {
    DataAccess dataAccess;
    UserService userService;
    UserHandler userHandler;
    GameService gameService;
    GameHandler gameHandler;

    public Server() {
        try {
            dataAccess = new SqlDataAccess();
        } catch (DataAccessException e) {
            throw new RuntimeException("Failed to initialize SqlDataAccess", e);
        }
        userService = new UserService(dataAccess);
        userHandler = new UserHandler(userService);
        gameService = new GameService(dataAccess);
        gameHandler = new GameHandler(gameService, userService);
    }

    public int run(int desiredPort) {
        Spark.port(desiredPort);
        Spark.staticFiles.location("resources/web");
        Spark.exception(DataAccessException.class, (exception, req, res) -> {
            res.status(403);
            res.type("application/json");
            res.body(new Gson().toJson(Map.of("error", exception.getMessage())));
        });
        Spark.webSocket("/ws", new WebSocketHandler(gameService, userService));
        Spark.delete("/db", this::clear);
        Spark.post("/user", userHandler::register);
        Spark.post("/session", userHandler::login);
        Spark.delete("/session", userHandler::logout);
        Spark.post("/game", gameHandler::createGame);
        Spark.put("/game", gameHandler::joinGame);
        Spark.get("/game", gameHandler::listGames);

        Spark.init();
        Spark.awaitInitialization();
        return Spark.port();
    }

    public Object clear(Request request, Response response){
        response.type("application/json");
        try {
            userService.clear();
            response.status(200);
            return "{}";
        } catch (DataAccessException e) {
            response.status(500);
            return new Gson().toJson(Map.of("Failed clear", "Error"));
        }
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }
}
