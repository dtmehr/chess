package service;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import model.GameData;

import java.util.Collection;


public class GameService {
    private final GameDAO gameDAO;


    public GameService(GameDAO gameDAO) {
        this.gameDAO = gameDAO;
    }

    public int createGame(String authToken, String gameId) throws DataAccessException {
        if (gameId == null || gameId.isEmpty()) {
            throw new DataAccessException("bad request");
        }
        return gameDAO.createGame(authToken, gameId);
    }

    public void joinGame(String authToken, int gameId, String playerColor) throws DataAccessException {
        gameDAO.joinGame(gameId, authToken, playerColor);
    }

    public Collection<GameData> listGames() throws DataAccessException{
        return gameDAO.listGames();
    }

}
