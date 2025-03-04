package service;

import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import model.UserData;

public class UserService {
    private final DataAccess dataAccess;

    public UserService(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }

    public void clear() throws DataAccessException {
        dataAccess.clear();
    }

    public record RegisterResult(String username, String authToken) {}

    public RegisterResult register(String username, String password, String email) throws DataAccessException {
        dataAccess.createUser();
        String authToken = AuthTokenGen.genAuthToken();
        return new RegisterResult(username, authToken);
    }
}

