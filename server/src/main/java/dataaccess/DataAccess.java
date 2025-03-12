package dataaccess;

import model.AuthData;
import model.GameData;

import java.util.Collection;

//will include CRUD stuff here?
public interface DataAccess {

    //user
    void clear() throws DataAccessException;
    void createUser(String username, String password, String email) throws DataAccessException;
    String login(String username, String password) throws DataAccessException;
    boolean logout(String authToken) throws DataAccessException;

    //game
    int createGame(String makerToken, String gameID) throws DataAccessException;
    void joinGame(int gameID, String authToken, String teamColor) throws DataAccessException;
    AuthData getAuthData(String token) throws DataAccessException;
    Collection<GameData> listGames()throws DataAccessException;
}

