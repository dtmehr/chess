package server;

import com.google.gson.Gson;
import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import dataaccess.MemoryDataAccess;
import service.GameService;
import service.UserService;
import spark.*;
import server.UserHandler;

import java.util.Map;

import static spark.Spark.delete;

public class Server {
    DataAccess dataAccess = new MemoryDataAccess();
    UserService userService = new UserService(dataAccess);
    UserHandler userHandler = new UserHandler(userService);

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        //help for errors
        Spark.staticFiles.location("resources/web");
        Spark.exception(DataAccessException.class, (exception, req, res) -> {
            res.status(403);
            res.type("application/json");
            res.body(new Gson().toJson(Map.of("error", exception.getMessage())));
        });

        // Register your endpoints and handle exceptions here.
        delete("/db", this::clear);
        Spark.post("/user", userHandler::register);
        Spark.post("/session", userHandler::login);
        //This line initializes the server and can be removed once you have a functioning endpoint 
        Spark.init();

        Spark.awaitInitialization();
        return Spark.port();
    }

    public Object clear(Request request, Response response){
        response.type("application/json");
        try{
            userService.clear();
            response.status(200);
            return"{}";
        } catch (DataAccessException e){
            response.status(500);
            return new Gson().toJson(Map.of("Failed clear", "Error"));
        }
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }
}
