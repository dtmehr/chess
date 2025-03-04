package service;
import dataaccess.DataAccessException;
import dataaccess.DataAccess;
import model.GameData;


public class GameService {
    private final DataAccess dataAccess;

    public GameService(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }

    public record LoginResult(String username, String authToken){}
    public LoginResult login(String username, String password, String authToken) throws DataAccessException {
        String token = dataAccess.login(username, password);
        return new LoginResult(username, token);
    }
}
