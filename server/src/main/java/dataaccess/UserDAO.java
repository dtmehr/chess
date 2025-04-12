package dataaccess;

import java.util.Collection;
import java.util.Map;
import java.util.HashMap;
import chess.ChessGame;
import service.AuthTokenGen;
import model.AuthData;
import model.GameData;
import model.UserData;

public class UserDAO implements GameDAO, AuthDAO {

    private Map<String, UserData> users = new HashMap<>();
    private Map<String, AuthData> authTokens = new HashMap<>();
    private Map<Integer, GameData> games = new HashMap<>();
    private int gameIdCount = 1;


    @Override
    public AuthData getAuth(String authToken) throws DataAccessException {
        return getAuthData(authToken);
    }

    //these might be put in the wrong file tbd
    //methods
    @Override
    public void clear() throws DataAccessException {
        //clear data or throw err
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
        //make sure username not taken first
        if (users.containsKey(username)){
            throw new DataAccessException("username taken");
        }
        //assign username to data
        users.put(username, new UserData(username, password, email));
    }


    @Override
    public String login(String username, String password) throws DataAccessException {
        //password, username null or emtpy
        if (username == null || password == null || username.isEmpty() || password.isEmpty()){
            throw new DataAccessException("wrong username or password");
        }
        //get username check if null or wrong password
        UserData user = users.get(username);
        if (user == null || !user.password.equals(password)) {
            throw new DataAccessException("wrong username or password");
        }
        //create authtoken from other file
        String token = AuthTokenGen.genAuthToken();
        authTokens.put(token, new AuthData(username, token));
        return token;
    }


    @Override
    public boolean logout(String authToken) {
        return authTokens.remove(authToken) != null;
    }


    @Override
    public AuthData getAuthData(String authToken) {
        return authTokens.get(authToken);
    }

    //helper
    public void forceAuth(String username, String token) {
        authTokens.put(token, new AuthData(username, token));
    }

    @Override
    public int createGame(String makerToken, String gameName) throws DataAccessException {
        // Check authentication token.
        if (!authTokens.containsKey(makerToken)) {
            throw new DataAccessException("unauthorized");
        }
        int newGameId = gameIdCount++;
        GameData newGame = new GameData(newGameId);
        // Set the game name.
        newGame.setGameName(gameName);
        newGame.setChessGame(new ChessGame());
        games.put(newGameId, newGame);
        return newGameId;
    }


    @Override
    public void joinGame(int gameID, String authToken, String teamColor) throws DataAccessException {
        //check authdata
        AuthData authData = authTokens.get(authToken);
        //check empty
        if (authData == null) {
            throw new DataAccessException("unauthorized");
        }
        //id
        GameData id = games.get(gameID);
        //check game id empty
        if (id == null) {
            throw new DataAccessException("bad request");
        }
        //check player color
        if (!("WHITE".equals(teamColor) || "BLACK".equals(teamColor))) {
            throw new DataAccessException("bad request");
        }
        //white else black
        if ("WHITE".equals(teamColor)) {
            if (id.getWhiteUsername() != null && !id.getWhiteUsername().isEmpty()) {
                throw new DataAccessException("already taken");
            }
            id.setWhiteUsername(authData.getUsername());
        } else {
            if (id.getBlackUsername() != null && !id.getBlackUsername().isEmpty()) {
                throw new DataAccessException("already taken");
            }
            id.setBlackUsername(authData.getUsername());
        }
    }

    //to do
    @Override
    public Collection<GameData> listGames() {
        return games.values();
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        GameData game = games.get(gameID);
        if (game == null) {
            throw new DataAccessException("Game not found");
        }
        return game;
    }

    @Override
    public void updateGame(int gameID, GameData game) throws DataAccessException {
        // Replace the existing game with the updated game data.
        games.put(gameID, game);
    }



}
