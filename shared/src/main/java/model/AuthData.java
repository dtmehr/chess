package model;

public class AuthData {
    public final String username;
    public final String authToken;

    public AuthData(String username, String authToken) {
        this.username = username;
        this.authToken = authToken;
    }

    public String getUsername() {
        return username;
    }
}