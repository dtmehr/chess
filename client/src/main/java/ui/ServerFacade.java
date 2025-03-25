package ui;

import model.AuthData;
import model.GameData;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.http.*;
import java.net.URI;
import java.util.List;

public class ServerFacade {
    private final HttpClient http;
    private final ObjectMapper mapper;
    private final String baseUrl;

    public ServerFacade(int port) {
        this.baseUrl = "http://localhost:" + port;
        this.http = HttpClient.newHttpClient();
        this.mapper = new ObjectMapper();
    }

    public AuthData register(String username, String password, String email) throws Exception {

    }

    public AuthData login(String username, String password) throws Exception {

    }

    public void logout(String authToken) throws Exception {

    }

    public int createGame(String authToken, String gameName) throws Exception {

    }

    public void joinGame(String authToken, int gameId, String color) throws Exception {

    }

    public List<GameData> listGames(String authToken) throws Exception {

    }
}
