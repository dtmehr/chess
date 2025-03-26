package dataaccess;


import org.junit.jupiter.api.*;
import service.GameService;
import service.UserService;

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
    public void clearTestValid() throws DataAccessException {
        userService.register("Jimmer", "32", "jimmer@mail.com");
        userService.clear();
        Assertions.assertThrows(DataAccessException.class, () -> userService.login("Jimmer", "32"), "Expected exception when logging in after clear");
    }


}
