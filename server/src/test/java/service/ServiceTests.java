package service;

import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import dataaccess.MemoryDataAccess;
import model.AuthData;
import model.GameData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collection;


public class ServiceTests {
    private UserService userService;
    private GameService gameService;



    @BeforeEach
    public void setUp() throws DataAccessException {
        DataAccess dataAccess = new MemoryDataAccess();
        userService = new UserService(dataAccess);
        gameService = new GameService(dataAccess);

        userService.clear();
    }

    @Test
    public void registerTest_Valid() throws DataAccessException {
        var result = userService.register("jakeman32", "password123", "jack@email.com");

        Assertions.assertEquals("jakeman32", result.username());
        Assertions.assertNotNull(result.authToken(), "Token should not be null");

    }
    @Test
    public void registerTest_Invalid() throws DataAccessException {
        var registered = userService.register("", "", "");
        Assertions.assertEquals("", registered.username());
        Assertions.assertNotNull(registered.authToken(), "username is empty, but auth shouldnt be null");

    }

    @Test
    public void loginTest_Valid()throws DataAccessException {
        //fake user to test
        userService.register("Jimmer", "32", "Jimmer@mail.com");
        var result = userService.login("Jimmer", "32");

        Assertions.assertEquals("Jimmer", result.username());
        Assertions.assertNotNull(result.authToken());

    }

    @Test
    public void loginTest_Invalid()throws DataAccessException{
        userService.register("Jimmer", "32", "Jimmer@mail.com");
        var result = userService.login("Jimmer", "32");

        Assertions.assertThrows(DataAccessException.class, () -> {
            userService.login("Jimmer", "COUGARS");}, "Expected exception for invalid password");

    }

    @Test
    public void logoutTest_Valid() throws DataAccessException{
       var registered = userService.register("Jimmer", "32", "Jimmer@mail.com");
        userService.logout(registered.authToken());

        Assertions.assertThrows(DataAccessException.class, () -> {
            userService.logout(registered.authToken());}, "logout failed");

    }

    @Test
    public void logoutTest_Invalid(){
        Assertions.assertThrows(DataAccessException.class, () -> {
            userService.logout("not real token");
        });

    }

    @Test
    public void createTest_Valid() throws DataAccessException{
        var registered = userService.register("Jimmer", "32", "Jimmer@mail.com");
        int gameID = gameService.createGame(registered.authToken(), "0032");

        Assertions.assertTrue(gameID > 0);

    }

    @Test
    public void createTest_Invalid(){
        Assertions.assertThrows(DataAccessException.class, () -> {
            gameService.createGame("this is not a real token", "034");
        });

    }

    @Test
    public void joinTest_Valid() throws DataAccessException{
        var jimmer = userService.register("Jimmer", "32", "Jimmer@mail.com");
        var steve = userService.register("Steve", "Jobs", "Jobs@mail.com");

        int gameID = gameService.createGame(jimmer.authToken(), "078");
        gameService.joinGame(steve.authToken(), gameID, "BLACK");

    }

    @Test
    public void joinTest_Invalid() throws DataAccessException{
        var jimmer = userService.register("Jimmer", "32", "Jimmer@mail.com");

        Assertions.assertThrows(DataAccessException.class, () -> {
            gameService.joinGame(jimmer.authToken(), 9999, "WHITE");}, "no 9999 id");

    }

    @Test
    public void listTest_Valid() throws DataAccessException{
        var jimmer = userService.register("Jimmer", "32", "jimmer@mail.com");

        gameService.createGame(jimmer.authToken(), "Game One");
        gameService.createGame(jimmer.authToken(), "Game Two");

        var allGames = gameService.listGames();
        Assertions.assertEquals(2, allGames.size(), "2 games");

    }

    @Test
    public void listTest_Invalid(){
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
    public void clearTest_Valid() throws DataAccessException{
        userService.register("Jimmer", "32", "jimmer@mail.com");
        userService.clear();

        Assertions.assertThrows(DataAccessException.class, () -> {
            userService.login("Jimmer", "32");
        });

    }

    @Test
    public void clearTest_Invalid() throws DataAccessException{
        userService.clear();
    }

}
