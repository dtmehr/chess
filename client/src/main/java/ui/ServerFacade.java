package ui;

import model.AuthData;
import model.GameData;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import java.net.http.*;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.lang.reflect.Type;

public class ServerFacade {
    private final HttpClient http;
    private final String baseUrl;

    public ServerFacade(int port) {
        this.baseUrl = "http://localhost:" + port;
        this.http = HttpClient.newHttpClient();
    }

//    register
//    follow general setup include http stuff
    public AuthData register(String username, String password, String email) throws Exception {
        String endpoint = baseUrl + "/user";
        Gson gson = new Gson();
//        create json info player
        JsonObject json = new JsonObject();
        json.addProperty("username", username);
        json.addProperty("password", password);
        json.addProperty("email", email);
        String requestBody = json.toString();
        //http req
        HttpRequest request = HttpRequest.newBuilder().uri(new URI(endpoint)).header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();
        //response
        HttpResponse<String> response = http.send(request, HttpResponse.BodyHandlers.ofString());

        //error catching
        if (response.statusCode() != 200) {
            throw new Exception("Registration failed");
        }
        return gson.fromJson(response.body(), AuthData.class);
    }

//    login
    public AuthData login(String username, String password) throws Exception {
        String endpoint = baseUrl + "/session";
        Gson gson = new Gson();
//get username password
        JsonObject json = new JsonObject();
        json.addProperty("username", username);
        json.addProperty("password", password);
        String requestBody = json.toString();

        HttpRequest request = HttpRequest.newBuilder().uri(new URI(endpoint))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody)).build();

        HttpResponse<String> response = http.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new Exception("Login failed");
        }
        return gson.fromJson(response.body(), AuthData.class);
    }

    //logout
    public void logout(String authToken) throws Exception {
        String endpoint = baseUrl + "/session";
        HttpRequest request = HttpRequest.newBuilder().uri(new URI(endpoint)).header("authorization", authToken)
                .DELETE()
                .build();

        HttpResponse<String> response = http.send(request, HttpResponse.BodyHandlers.ofString());
        //        errors
        if (response.statusCode() != 200) {
            throw new Exception("Logout failed");
        }
    }

//create game
    public int createGame(String authToken, String gameName) throws Exception {
        String endpoint = baseUrl + "/game";
        Gson gson = new Gson();
        //use gameName or id?
        JsonObject json = new JsonObject();
        json.addProperty("gameName", gameName);
        String requestBody = json.toString();

//same as before
        HttpRequest request = HttpRequest.newBuilder().uri(new URI(endpoint)).header("authorization", authToken)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        HttpResponse<String> response = http.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) {
            String body = response.body() != null ? response.body() : "";
            throw new Exception("Create game failed: " + body);
        }

        Map<String, Double> result = gson.fromJson(response.body(), Map.class);
        //need gameID
        return result.get("gameID").intValue();
    }
//join game
    public void joinGame(String authToken, int gameId, String color) throws Exception {
        String endpoint = baseUrl + "/game";

//needs gameID and player color
        JsonObject json = new JsonObject();
        json.addProperty("gameID", gameId);
        json.addProperty("playerColor", color);
        String requestBody = json.toString();

        HttpRequest request = HttpRequest.newBuilder().uri(new URI(endpoint))
                .header("authorization", authToken).header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        HttpResponse<String> response = http.send(request, HttpResponse.BodyHandlers.ofString());
        //errors
        if (response.statusCode() != 200) {
            throw new Exception("Join game failed ");
        }
    }

    public List<GameData> listGames(String authToken) throws Exception {
        String endpoint = baseUrl + "/game";
        Gson gson = new Gson();

        HttpRequest request = HttpRequest.newBuilder().uri(new URI(endpoint))
                .header("authorization", authToken).header("Content-Type", "application/json")
                .GET()
                .build();

        HttpResponse<String> response = http.send(request, HttpResponse.BodyHandlers.ofString());
        //errors
        if (response.statusCode() != 200) {
            throw new Exception("List games failed ");
        }

        Type type = new TypeToken<Map<String, List<GameData>>>(){}.getType();
        Map<String, List<GameData>> result = gson.fromJson(response.body(), type);
        return result.get("games");
    }

}
