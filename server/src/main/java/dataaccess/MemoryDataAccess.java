package dataaccess;

import java.util.Map;
import java.util.HashMap;

import model.AuthData;
import model.GameData;
import model.UserData;

public class MemoryDataAccess implements DataAccess {

    private Map<String, UserData> users = new HashMap<>();
    private Map<String, AuthData> authTokens = new HashMap<>();
    private Map<Integer, GameData> games = new HashMap<>();

    //methods
    @Override
    public void clear() throws DataAccessException {
        try {
            users.clear();
            authTokens.clear();
            games.clear();
        } catch (Exception e) {
            throw new DataAccessException("Unable to clear data");
        }
    }

    @Override
    public void createUser(String username, String password, String email) throws DataAccessException {
        if (users.containsKey(username)){
            throw new DataAccessException("username taken");
        }
        users.put(username, new UserData(username, password, email));

    }


    @Override
    public String login(String username, String password) throws DataAccessException {
        return "";
    }

    @Override
    public void logout(String authToken) {

    }

    @Override
    public int createGame(String makerToken) {
        return 0;
    }

    @Override
    public void joinGame(int gameID, String authToken, String teamColor) {

    }

    @Override
    public void listGames() {

    }

}
