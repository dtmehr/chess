package service;
import dataaccess.DataAccessException;
import dataaccess.DataAccess;


public class GameService {
    private final DataAccess dataAccess;


    public GameService(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }

    public int createGame(String authToken, String gameId) throws DataAccessException {
        if (gameId == null || gameId.isEmpty()) {
            throw new DataAccessException("bad request");
        }
        return dataAccess.createGame(authToken, gameId);
    }

    public void joinGame(String authToken, int gameId, String playerColor) throws DataAccessException {
        dataAccess.joinGame(gameId, authToken, playerColor);
    }

}
