package client;

import org.junit.jupiter.api.*;
import server.Server;



public class ServerFacadeTests {

    private static Server server;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }


    @Test
    public void testRegisterPositive() {

    }

    @Test
    public void testRegisterNegative() {

    }

    @Test
    public void testLoginPositive() {

    }

    @Test
    public void testLoginNegative() {

    }

    @Test
    public void testLogoutPositive() {

    }

    @Test
    public void testLogoutNegative() {

    }

    @Test
    public void testCreateGamePositive() {

    }

    @Test
    public void testCreateGameNegative() {

    }

}
