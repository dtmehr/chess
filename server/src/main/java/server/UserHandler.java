package server;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import service.UserService;
import spark.Response;

import java.util.Map;

public class UserHandler {
    private final UserService userService;

    private static class RegisterRequest {
        String username;
        String password;
        String email;
    }

    private static class LoginRequest {
        String username;
        String password;
    }

    public UserHandler(UserService userService) {
        this.userService = userService;
    }

    public Object logout(spark.Request request, Response response) {
        response.type("application/json");
        Gson gson = new Gson();
        try {
            //401 err check
            String authToken = request.headers("authorization");
            if (authToken == null || authToken.isEmpty()) {
                response.status(401);
                return gson.toJson(Map.of("message", "Error: unauthorized"));
            }
            //working
            userService.logout(authToken);
            response.status(200);
            return "{}";
            //other checks because it wasn't workign at first
            //401 500
        } catch (DataAccessException error) {
            response.status(401);
            return gson.toJson(Map.of("message", "Error: " + error.getMessage()));
        } catch (Exception error) {
            response.status(500);
            return gson.toJson(Map.of("message", "Error: " + error.getMessage()));
        }
    }

    public Object register(spark.Request request, Response response) {
        response.type("application/json");
        Gson gson = new Gson();
        try {
            //possibly redundant if statement?
            RegisterRequest data = gson.fromJson(request.body(), RegisterRequest.class);
            if (data == null || data.password == null ) {
                throw new DataAccessException("bad request");
            }
            //working
            UserService.RegisterResult result = userService.register(data.username, data.password, data.email);
            response.status(200);
            return gson.toJson(result);
            //in case it doesn't work. might condense later
            //400 403 500 errors
        } catch (DataAccessException error) {
            if ("bad request".equals(error.getMessage())) {
                response.status(400);
            } else if ("username taken".equals(error.getMessage())) {
                response.status(403);
            } else {
                response.status(500);
            }
            return gson.toJson(Map.of("message", "Error: " + error.getMessage()));
        }
    }

    public Object login(spark.Request request, Response response) {
        response.type("application/json");
        Gson gson = new Gson();
        try {
            //working
            LoginRequest data = gson.fromJson(request.body(), LoginRequest.class);
            UserService.LoginResult result = userService.login(data.username, data.password);
            response.status(200);
            return gson.toJson(result);
            //401 error
        } catch (DataAccessException e) {
            response.status(401);
            return gson.toJson(Map.of("message", "Error: " + e.getMessage()));
        }
    }
}