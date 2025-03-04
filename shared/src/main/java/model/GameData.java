package model;

import java.util.Map;

public class GameData {
    private String whiteUsername = "";
    private String blackUsername = "";
    private String gameName = "";


    public GameData(int gameId) {
        this.whiteUsername = null;
        this.blackUsername = null;
        this.gameName = null;
    }
    //helpers for logic
    public String getWhiteUsername() {
        return whiteUsername;
    }

    public void setWhiteUsername(String whiteUsername) {
        this.whiteUsername = whiteUsername;
    }

    public String getBlackUsername() {
        return blackUsername;
    }

    public void setBlackUsername(String blackUsername) {
        this.blackUsername = blackUsername;
    }

    public void setGameName(String gameName) {
        this.gameName = gameName;
    }
}
