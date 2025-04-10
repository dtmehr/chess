package dataaccess;

import org.junit.jupiter.api.*;
import service.GameService;
import service.UserService;


public abstract class GameTests {
    protected UserService userService;
    protected GameService gameService;

    protected abstract GameDAO createDataAccess() throws DataAccessException;

    @BeforeEach
    void setup() throws DataAccessException {
        GameDAO gameDAO = createDataAccess();
        userService = new UserService(gameDAO);
        gameService = new GameService(gameDAO);
        userService.clear();
    }


}