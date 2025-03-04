package server;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import service.UserService;
import spark.Response;

import java.util.Map;

public class UserHandler {
    private final UserService userService;

    private static class Request {
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

    public Object register(spark.Request request, Response response) throws DataAccessException {
        response.type("application/json");
        var gson = new Gson();
        Request data = gson.fromJson(request.body(), Request.class);
        UserService.RegisterResult result = userService.register(data.username, data.password, data.email);
        response.status(200);
        return gson.toJson(result);
    }

    public Object login(spark.Request request, Response response) {
        response.type("application/json");
        Gson gson = new Gson();
        try {
            LoginRequest data = gson.fromJson(request.body(), LoginRequest.class);
            UserService.LoginResult result = userService.login(data.username, data.password);
            response.status(200);
            return gson.toJson(result);
        } catch (DataAccessException e) {
            response.status(401);
            return gson.toJson(Map.of("message", "Error: " + e.getMessage()));
        }
    }



}
