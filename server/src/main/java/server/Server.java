package server;

import com.google.gson.Gson;
import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import dataaccess.MemoryDataAccess;
import service.GameService;
import service.UserService;
import spark.*;

import java.util.Map;

import static spark.Spark.delete;

public class Server {
    DataAccess dataAccess = new MemoryDataAccess();
    UserService userService = new UserService(dataAccess);

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("resources/web");

        // Register your endpoints and handle exceptions here.
        delete("/db", this::clear);
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
