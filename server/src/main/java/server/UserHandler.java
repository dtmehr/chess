package server;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import service.UserService;
import spark.Request;
import spark.Response;

public class UserHandler {
    private final UserService userService;

    private static class RegisterRequest {
        String username;
        String password;
        String email;
    }

    public UserHandler(UserService userService) {
        this.userService = userService;
    }

    public Object register(Request request, Response response) throws DataAccessException {
        response.type("application/json");
        var gson = new Gson();
        RegisterRequest data = gson.fromJson(request.body(), RegisterRequest.class);
        UserService.RegisterResult result = userService.register(data.username, data.password, data.email);
        response.status(200);
        return gson.toJson(result);
    }
}
