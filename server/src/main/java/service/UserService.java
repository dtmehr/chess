package service;

import com.google.gson.Gson;
import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import model.UserData;
import spark.Request;
import spark.Response;

public class UserService {
    private final DataAccess dataAccess;
    public record RegisterResult(String username, String authToken) {}
    public record LoginResult(String username, String authToken) {}

    public UserService(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }

    public void clear() throws DataAccessException {
        dataAccess.clear();
    }


    public RegisterResult register(String username, String password, String email) throws DataAccessException {
        dataAccess.createUser(username, password, email);
        String authToken = AuthTokenGen.genAuthToken();
        return new RegisterResult(username, authToken);
    }

//    private static class LoginRequest {
//        String username;
//        String password;
//    }

    public LoginResult login(String username, String password) throws DataAccessException {
        String authToken = dataAccess.login(username, password);
        return new LoginResult(username, authToken);
    }

//    public Object loginHandler(Request req, Response res) throws DataAccessException {
//        var gson = new Gson();
//        req.attribute("type", "application/json");
//        LoginRequest data = gson.fromJson(req.body(), LoginRequest.class);
//        LoginResult result = login(data.username, data.password);
//        res.status(200);
//        return gson.toJson(result);
//    }
}

