package service;


import dataaccess.GameDAO;
import dataaccess.UserDAO;
import dataaccess.DataAccessException;
import dataaccess.SqlGameDAO;
import model.AuthData;


public class UserService {
    private final GameDAO gameDAO;
    public record RegisterResult(String username, String authToken) {}
    public record LoginResult(String username, String authToken) {}


    public UserService(GameDAO gameDAO) {
        this.gameDAO = gameDAO;
    }

    public void clear() throws DataAccessException {
        gameDAO.clear();
    }

    public String getUsernameFromToken(String token) throws DataAccessException {
        AuthData authData = gameDAO.getAuthData(token);
        if (authData == null) {
            throw new DataAccessException("unauthorized");
        }
        return authData.getUsername();
    }

    public RegisterResult register(String username, String password, String email) throws DataAccessException {
        gameDAO.createUser(username, password, email);
        //used to pass normal vs invalid tests
        String authToken = AuthTokenGen.genAuthToken();
        if (gameDAO instanceof UserDAO) {
            ((UserDAO) gameDAO).forceAuth(username, authToken);
        } else if (gameDAO instanceof SqlGameDAO) {
            ((SqlGameDAO) gameDAO).forceAuth(username, authToken);
        }
        return new RegisterResult(username, authToken);
    }



    public LoginResult login(String username, String password) throws DataAccessException {
        String authToken = gameDAO.login(username, password);
        return new LoginResult(username, authToken);
    }

    public void logout(String authToken) throws DataAccessException {
        //new
        //error if null, attempt remove, error if fail
        if (authToken == null) {
            throw new DataAccessException("unauthorized");
        }
        boolean removed = gameDAO.logout(authToken);
        if (!removed) {
            throw new DataAccessException("unauthorized");
        }
    }
}


