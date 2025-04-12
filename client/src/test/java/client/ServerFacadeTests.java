package client;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import ui.ServerFacade;
import model.AuthData;
import model.GameData;
import server.Server;

import java.net.http.*;
import java.net.URI;
import com.google.gson.Gson;
import java.util.Collection;

public class ServerFacadeTests {

    private Server server;
    private ServerFacade facade;
//    private final Gson gson = new Gson();

    @BeforeEach
    public void setUp() throws Exception {
        server = new Server();
        int port = server.run(0);
        facade = new ServerFacade(port);

        //clear db
        HttpRequest clearRequest = HttpRequest.newBuilder().uri(new URI("http://localhost:" + port + "/db")).DELETE().build();
        HttpResponse<String> clearResponse = HttpClient.newHttpClient().send(clearRequest, HttpResponse.BodyHandlers.ofString());
    }

    @AfterEach
    public void tearDown() {
        server.stop();
    }

    @Test
    public void registerTestValid() throws Exception {
        String username = "jakeman32_" + System.currentTimeMillis();
        AuthData result = facade.register(username, "password123", "jack@email.com");
        assertEquals(username, result.getUsername(), "username needs to match");
        assertNotNull(result.getAuthToken(), "Token should not be null");
    }

    @Test
    public void registerTestInvalid() {
        Exception exception = assertThrows(Exception.class, () -> {facade.register("jakeman32", null, "jack@email.com");});
        String actualMessage = exception.getMessage();
        assertNotNull(actualMessage, "cant be null");
        assertTrue(actualMessage.contains("Registration failed"));
    }

    @Test
    public void loginTestValid() throws Exception {
        String username = "jimmer";
        facade.register(username, "32", "jimmer@mail.com");
        AuthData result = facade.login(username, "32");
        assertEquals(username, result.getUsername());
        assertNotNull(result.getAuthToken());
    }

    @Test
    public void loginTestInvalid() throws Exception {
        String username = "jimmer";
        facade.register(username, "32", "jimmer2@mail.com");
        Exception exception = assertThrows(Exception.class, () -> {
            facade.login(username, "wrongpassword");
        });
        assertTrue(exception.getMessage().contains("Login failed"));
    }

    @Test
    public void logoutTestValid() throws Exception {
        String username = "Jimmer";
        AuthData registered = facade.register(username, "32", "Jimmer@mail.com");
        facade.logout(registered.getAuthToken());
        Exception exception = assertThrows(Exception.class, () -> {
            facade.logout(registered.getAuthToken());
        });
        String message = exception.getMessage();
        assertNotNull(message, "Exception message should not be null");
        assertTrue(message.contains("Logout failed"));
    }

    @Test
    public void logoutTestInvalid() {
        Exception exception = assertThrows(Exception.class, () -> {
            facade.logout("not real token");});

        String message = exception.getMessage();
        assertNotNull(message, "Exception message should not be null");
        assertTrue(message.contains("Logout failed"));
    }

    @Test
    public void createTestValid() throws Exception {
        String username = "Jimmer";
        AuthData registered = facade.register(username, "32", "jimmer@mail.com");
        //not sure if this authtoken works or not
        int gameID = facade.createGame(registered.getAuthToken(), "032");
        assertTrue(gameID > 0);
    }

    @Test
    public void createTestInvalid() {
        Exception exception = assertThrows(Exception.class, () -> {
            facade.createGame("invalid token", "-1");
        });
        assertTrue(exception.getMessage().contains("Create game failed"));
    }

    @Test
    public void joinTestValid() throws Exception {
        String user1 = "Jimmer";
        String user2 = "Stevejobster";
        AuthData jimmer = facade.register(user1, "32", "jimmer@mail.com");
        AuthData steve = facade.register(user2, "Jobs", "jobs@mail.com");
        int gameID = facade.createGame(jimmer.getAuthToken(), "Game_078");
        facade.joinGame(steve.getAuthToken(), gameID, "BLACK");
    }

    @Test
    public void joinTestInvalid() throws Exception {
        String username = "Jimmer";
        AuthData jimmer = facade.register(username, "32", "jimmer@mail.com");
        Exception exception = assertThrows(Exception.class, () -> {
            facade.joinGame(jimmer.getAuthToken(), 9999, "WHITE");});
        assertTrue(exception.getMessage().contains("Join game failed"));
    }

    @Test
    public void listTestValid() throws Exception {
        String username = "JimmerMan";
        AuthData jimmer = facade.register(username, "32", "jimmer@mail.com");
        facade.createGame(jimmer.getAuthToken(), "Game One");
        facade.createGame(jimmer.getAuthToken(), "Game Two");
        Collection<GameData> allGames = facade.listGames(jimmer.getAuthToken());
        assertEquals(2, allGames.size());
    }

    @Test
    public void listTestInvalid() {
        Exception exception = assertThrows(Exception.class, () -> {
            facade.listGames("invalid token");
        });
        assertTrue(exception.getMessage().contains("List games failed"));
    }


}
