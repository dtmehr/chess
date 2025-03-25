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
    private int port;
    private final Gson gson = new Gson();

    @BeforeEach
    public void setUp() throws Exception {
        server = new Server();
        port = server.run(0);
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
        assertEquals(username, result.username, "username needs to match");
        assertNotNull(result.authToken, "Token should not be null");
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
        assertEquals(username, result.username);
        assertNotNull(result.authToken);
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



}
