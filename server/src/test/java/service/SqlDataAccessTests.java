package service;

import dataaccess.DataAccessException;
import dataaccess.SqlDataAccess;
import model.GameData;
import org.junit.jupiter.api.*;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

public class SqlDataAccessTests {
    private SqlDataAccess dao;

    @BeforeEach
    void setup() throws DataAccessException {
        dao = new SqlDataAccess();
        dao.clear();
    }

    @Test
    void clearTest_Positive() throws DataAccessException {
        dao.createUser("jimmer", "32", "jimmer@email.com");
        dao.clear();
        assertThrows(DataAccessException.class, () -> {
            dao.login("jimmer", "32");
        });
    }


    @Test
    void createUserTest_Positive() throws DataAccessException {
        dao.createUser("jimmer", "32", "jimmer@email.com");
        String test = dao.login("jimmer", "32");
        assertNotNull(test);
    }

    @Test
    void createUserTest_Negative() throws DataAccessException{
        dao.createUser("jimmer", "32", "jimmer@email.com");
        assertThrows(DataAccessException.class, () -> {
            dao.createUser("jimmer", "332", "jimmer32@gmail.com");
        });
    }

    @Test
    void logoutTest_Negative() throws DataAccessException {

    }

    @Test
    void logoutTest_Positive() throws DataAccessException {

    }

    @Test
    void loginTest_Negative() throws DataAccessException {

    }

    @Test
    void loginTest_Positive() throws DataAccessException {
        dao.createUser("jimmer", "32", "jimmer@email.com");
        String token = dao.login("jimmer", "32");
        assertNotNull(token);
    }

    @Test
    void createGameTest_Positive() throws DataAccessException {
        dao.createUser("jimmer", "32", "jimmer@email.com");
        String token = dao.login("jimmer", "32");

        int gameId = dao.createGame(token, "123");
        assertTrue(gameId > 0);

        Collection<GameData> games = dao.listGames();
        assertEquals(1, games.size());
        GameData first = games.iterator().next();
        assertEquals("123", first.getGameName());
    }
}
