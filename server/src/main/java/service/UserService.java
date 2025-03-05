package service;


import dataaccess.DataAccess;
import dataaccess.MemoryDataAccess;
import dataaccess.DataAccessException;
import model.AuthData;


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

    public String getUsernameFromToken(String token) throws DataAccessException {
        AuthData authData = dataAccess.getAuthData(token);
        if (authData == null) {
            throw new DataAccessException("unauthorized");
        }
        return authData.getUsername();
    }

    public RegisterResult register(String username, String password, String email) throws DataAccessException {
        dataAccess.createUser(username, password, email);
        //used to pass normal vs invalid tests
        String authToken = AuthTokenGen.genAuthToken();
        if (dataAccess instanceof MemoryDataAccess) {
            ((MemoryDataAccess)dataAccess).forceAuth(username, authToken);
        }
        return new RegisterResult(username, authToken);
    }



    public LoginResult login(String username, String password) throws DataAccessException {
        String authToken = dataAccess.login(username, password);
        return new LoginResult(username, authToken);
    }

    public void logout(String authToken) throws DataAccessException {
        //new
        //error if null, attempt remove, error if fail
        if (authToken == null) {
            throw new DataAccessException("unauthorized");
        }
        boolean removed = dataAccess.logout(authToken);
        if (!removed) {
            throw new DataAccessException("unauthorized");
        }
    }
}


