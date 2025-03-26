package dataaccess;

import model.GameData;
import org.junit.jupiter.api.*;
import service.GameService;
import service.UserService;
import java.util.Collection;

@SuppressWarnings("PMD.DuplicateCode")
public class SqlDataAccessTests {
    private UserService userService;
    private GameService gameService;

    @BeforeEach
    void setup() throws DataAccessException {
        DataAccess dataAccess = new SqlDataAccess();
        userService = new UserService(dataAccess);
        gameService = new GameService(dataAccess);
        userService.clear();
    }

    @Test
    public void registerTestValid() throws DataAccessException {
        var result = userService.register("jakeman32", "password123", "jack@email.com");
        Assertions.assertEquals("jakeman32", result.username());
        Assertions.assertNotNull(result.authToken(), "Token should not be null");
    }

    @Test
    public void registerTestInvalid() throws DataAccessException {
        var registered = userService.register("", "", "");
        Assertions.assertEquals("", registered.username());
        Assertions.assertNotNull(registered.authToken(), "Username is empty, but auth token shouldn't be null");
    }

    @Test
    public void registerTestDuplicateUser() throws DataAccessException {
        userService.register("d", "pass", "ma");
        Assertions.assertThrows(DataAccessException.class, () -> userService.register("d", "pass", "ma"));
    }

    @Test
    public void loginTestValid() throws DataAccessException {
        userService.register("Jimmer", "32", "Jimmer@mail.com");
        var result = userService.login("Jimmer", "32");
        Assertions.assertEquals("Jimmer", result.username());
        Assertions.assertNotNull(result.authToken());
    }

    @Test
    public void loginTestInvalid() throws DataAccessException {
        userService.register("Jimmer", "32", "Jimmer@mail.com");
        Assertions.assertThrows(DataAccessException.class, () -> userService.login("Jimmer", "COUGARS"), "Expected exception for invalid password");
    }

    @Test
    public void loginTestNonExistentUser() {
        Assertions.assertThrows(DataAccessException.class, () -> userService.login("n", "any"));
    }

    @Test
    public void loginTestNullUsername() {
        Assertions.assertThrows(DataAccessException.class, () -> userService.login(null, "password"), "Expected exception for null username");
    }

    @Test
    public void logoutTestValid() throws DataAccessException {
        var registered = userService.register("Jim", "32", "@mail.com");
        userService.logout(registered.authToken());
        Assertions.assertThrows(DataAccessException.class, () -> userService.logout(registered.authToken()), "error");
    }

    @Test
    public void logoutTestInvalid() {
        Assertions.assertThrows(DataAccessException.class, () -> userService.logout("not real token"));
    }

    @Test
    public void logoutTestNullToken() {
        Assertions.assertThrows(DataAccessException.class, () -> userService.logout(null), "Expected exception for null token on logout");
    }

    @Test
    public void createTestValid() throws DataAccessException {
        var registered = userService.register("Jimmer", "32", "Jimmer@mail.com");
        int gameID = gameService.createGame(registered.authToken(), "0032");
        Assertions.assertTrue(gameID > 0, "Game ID should be positive");
    }

    @Test
    public void createTestInvalid() {
        Assertions.assertThrows(DataAccessException.class, () -> gameService.createGame("afke", "034"));
    }

    @Test
    public void createTestEmptyGameName() throws DataAccessException {
        var registered = userService.register("Jimmer", "32", "Jimmer@mail.com");
        Assertions.assertThrows(DataAccessException.class, () -> gameService.createGame(registered.authToken(), ""));
    }

    @Test
    public void createTestNullToken() {
        Assertions.assertThrows(DataAccessException.class, () -> gameService.createGame(null, "ValidGameName"));
    }

    @Test
    public void joinTestValid() throws DataAccessException {
        var jimmer = userService.register("Jimmer", "32", "Jimmer@mail.com");
        var steve = userService.register("Steve", "Jobs", "Jobs@mail.com");
        int gameID = gameService.createGame(jimmer.authToken(), "078");
        gameService.joinGame(steve.authToken(), gameID, "BLACK");
    }

    @Test
    public void joinTestInvalid() throws DataAccessException {
        var jimmer = userService.register("Jimmer", "32", "@mail.com");
        Assertions.assertThrows(DataAccessException.class, () -> gameService.joinGame(jimmer.authToken(), 9999, "WHITE"));
    }
//test
    @Test
    public void joinTestInvalidTeamColor() throws DataAccessException {
        var jimmer = userService.register("Jimmer", "32", "mail.com");
        var steve = userService.register("S", "Jobs", "J@mail.com");
        int gameID = gameService.createGame(jimmer.authToken(), "GameX");
        Assertions.assertThrows(DataAccessException.class, () -> gameService.joinGame(steve.authToken(), gameID, "GREEN"));
    }

    @Test
    public void joinTestTeamAlreadyTaken() throws DataAccessException {
        var jimmer = userService.register("Jimmer", "32", "Jimmer@mail.com");
        var steve = userService.register("Steve", "Jobs", "Jobs@mail.com");
        int gameID = gameService.createGame(jimmer.authToken(), "GameY");
        gameService.joinGame(jimmer.authToken(), gameID, "WHITE");
        Assertions.assertThrows(DataAccessException.class, () -> gameService.joinGame(steve.authToken(), gameID, "WHITE"));
    }

    @Test
    public void listTestValid() throws DataAccessException {
        var jimmer = userService.register("Jimmer", "32", "jimmer@mail.com");
        gameService.createGame(jimmer.authToken(), "Game One");
        gameService.createGame(jimmer.authToken(), "Game Two");
        var allGames = gameService.listGames();
        Assertions.assertEquals(2, allGames.size(), "Expected 2 games");
    }

    @Test
    public void listTestInvalid() {
        Collection<GameData> games = null;
        try {
            games = gameService.listGames();
        } catch (DataAccessException e) {
            Assertions.fail("listGames threw an exception: " + e.getMessage());
        }
        Assertions.assertTrue(games.isEmpty(), "Expected empty collection when no games are created");
    }

    @Test
    public void clearTestValid() throws DataAccessException {
        userService.register("Jimmer", "32", "jimmer@mail.com");
        userService.clear();
        Assertions.assertThrows(DataAccessException.class, () -> userService.login("Jimmer", "32"), "Expected exception when logging in after clear");
    }

    @Test
    public void clearTestInvalid() throws DataAccessException {
        userService.clear();
    }
}
