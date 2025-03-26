package dataaccess;

import model.GameData;
import org.junit.jupiter.api.*;
import service.GameService;
import service.UserService;
import java.util.Collection;


public abstract class GameTests {
    protected UserService userService;
    protected GameService gameService;

    protected abstract DataAccess createDataAccess() throws DataAccessException;

    @BeforeEach
    void setup() throws DataAccessException {
        DataAccess dataAccess = createDataAccess();
        userService = new UserService(dataAccess);
        gameService = new GameService(dataAccess);
        userService.clear();
    }


}