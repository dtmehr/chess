package service;

import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import dataaccess.GameTests;
import dataaccess.MemoryDataAccess;
import model.GameData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collection;


public class ServiceTests extends GameTests {
    private UserService userService;
    private GameService gameService;



    @BeforeEach
    public void setUp() throws DataAccessException {
        DataAccess dataAccess = new MemoryDataAccess();
        userService = new UserService(dataAccess);
        gameService = new GameService(dataAccess);

        userService.clear();
    }

    @Override
    protected DataAccess createDataAccess() throws DataAccessException {
        return null;
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
        Assertions.assertNotNull(registered.authToken(), "username is empty, but auth shouldnt be null");

    }

    @Test
    public void loginTestValid()throws DataAccessException {
        //fake user to test
        userService.register("Jimmer", "32", "Jimmer@mail.com");
        var result = userService.login("Jimmer", "32");

        Assertions.assertEquals("Jimmer", result.username());
        Assertions.assertNotNull(result.authToken());

    }

    @Test
    public void loginTestInvalid()throws DataAccessException{
        userService.register("Jimmer", "32", "Jimmer@mail.com");
        var result = userService.login("Jimmer", "32");

        Assertions.assertThrows(DataAccessException.class, () -> {
            userService.login("Jimmer", "COUGARS");}, "Expected exception for invalid password");

    }

    @Test
    public void logoutTestValid() throws DataAccessException{
       var registered = userService.register("Jimmer", "32", "Jimmer@mail.com");
        userService.logout(registered.authToken());

        Assertions.assertThrows(DataAccessException.class, () -> {
            userService.logout(registered.authToken());}, "logout failed");

    }

    @Test
    public void logoutTestInvalid(){
        Assertions.assertThrows(DataAccessException.class, () -> {
            userService.logout("not real token");
        });

    }

    @Test
    public void createTestValid() throws DataAccessException{
        var registered = userService.register("Jimmer", "32", "Jimmer@mail.com");
        int gameID = gameService.createGame(registered.authToken(), "0032");

        Assertions.assertTrue(gameID > 0);

    }

    @Test
    public void createTestInvalid(){
        Assertions.assertThrows(DataAccessException.class, () -> {
            gameService.createGame("this is not a real token", "034");
        });

    }

    @Test
    public void joinTestValid() throws DataAccessException{
        var jimmer = userService.register("Jimmer", "32", "Jimmer@mail.com");
        var steve = userService.register("Steve", "Jobs", "Jobs@mail.com");

        int gameID = gameService.createGame(jimmer.authToken(), "078");
        gameService.joinGame(steve.authToken(), gameID, "BLACK");

    }

    @Test
    public void joinTestInvalid() throws DataAccessException{
        var jimmer = userService.register("Jimmer", "32", "Jimmer@mail.com");

        Assertions.assertThrows(DataAccessException.class, () -> {
            gameService.joinGame(jimmer.authToken(), 9999, "WHITE");}, "no 9999 id");

    }

    @Test
    public void listTestValid() throws DataAccessException{
        var jimmer = userService.register("Jimmer", "32", "jimmer@mail.com");

        gameService.createGame(jimmer.authToken(), "Game One");
        gameService.createGame(jimmer.authToken(), "Game Two");

        var allGames = gameService.listGames();
        Assertions.assertEquals(2, allGames.size(), "2 games");

    }

    @Test
    public void listTestInvalid(){
        //call real data
        Collection<GameData> games = null;
        try {//there might not be any lists
            games = gameService.listGames();
        } catch (DataAccessException e) {
            Assertions.fail("listGames threw an exception: " + e.getMessage());
        }
        Assertions.assertTrue(games.isEmpty(), "Expected empty collection when no games are created");

    }

    @Test
    public void clearTestValid() throws DataAccessException{
        userService.register("Jimmer", "32", "jimmer@mail.com");
        userService.clear();

        Assertions.assertThrows(DataAccessException.class, () -> {
            userService.login("Jimmer", "32");
        });

    }

    @Test
    public void clearTestInvalid() throws DataAccessException{
        userService.clear();
    }

}
