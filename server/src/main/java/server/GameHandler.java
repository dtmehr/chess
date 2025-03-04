package server;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import spark.Request;
import spark.Response;
import service.GameService;
import service.UserService;

import dataaccess.DataAccess;

import java.util.Map;

public class GameHandler {
    private final GameService gameService;
    private final UserService userService;

    public GameHandler(GameService gameService, UserService userService) {
        this.gameService = gameService;
        this.userService = userService;
    }

    private static class GameRequest {
        String gameName;

    }

    public Object createGame(Request request, Response response) {
        response.type("application/json");
        Gson gson = new Gson();
        //attmept success, go through edge cases
        try {
            String authToken = request.headers("authorization");
            GameRequest gameRequest = gson.fromJson(request.body(), GameRequest.class);
            int gameID = gameService.createGame(authToken, gameRequest.gameName);
            response.status(200);
            return gson.toJson(Map.of("gameID", gameID));

            //different catch all errors
        } catch (DataAccessException e) {
            response.status(401);
            return gson.toJson(Map.of("message", "Error: " + e.getMessage()));
        } catch (Exception e) {
            GameRequest gameRequest = gson.fromJson(request.body(), GameRequest.class);
            if (gameRequest == null){
                response.status(400);
            } else{
                response.status(500);
            }
            return gson.toJson(Map.of("message", "Error: " + e.getMessage()));
        }
    }

    public Object joinGame(Request request, Response response) {
        response.type("application/json");
        Gson gson = new Gson();
        try {
            String authToken = request.headers("authorization");
            if (authToken == null || authToken.trim().isEmpty()) {
                response.status(401);
                return gson.toJson(Map.of("message", "Error: unauthorized"));
            }
            JoinGameRequest data = gson.fromJson(request.body(), JoinGameRequest.class);
            if (data == null || data.playerColor == null || data.playerColor.isBlank()) {
                response.status(400);
                return gson.toJson(Map.of("message", "Error: bad request"));
            }

            gameService.joinGame(authToken, data.gameID, data.playerColor);
            response.status(200);
            return "{}";
        //check msg and match with code for error. switch cases worked better
        } catch (DataAccessException e) {
            String msg = e.getMessage();
            switch (msg) {
                case "unauthorized" -> response.status(401);
                case "bad request" -> response.status(400);
                case "already taken" -> response.status(403);
                case null, default -> response.status(500);
            }
            return gson.toJson(Map.of("message", "Error: " + msg));
        }
    }
    //helper
    private static class JoinGameRequest {
        String playerColor;
        int gameID;
    }

    public Object listGames(Request request, Response response) {
        response.type("application/json");
        Gson gson = new Gson();
        try {
            //try success, fail is
            String authToken = request.headers("authorization");
            if (authToken == null || authToken.isEmpty()) {
                response.status(401);
                return gson.toJson(Map.of("message", "Error: unauthorized"));
            }
            userService.getUsernameFromToken(authToken);
            var gamesList = gameService.listGames();
            //success
            response.status(200);
            return gson.toJson(Map.of("games", gamesList));
            //errors
        } catch (DataAccessException e) {
            switch (e.getMessage()) {
                case "unauthorized":
                    response.status(401);
                    break;
                default:
                    response.status(500);
                    break;
            }
            return gson.toJson(Map.of("message", "Error: " + e.getMessage()));
        }
    }

}
