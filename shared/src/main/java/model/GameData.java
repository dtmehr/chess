package model;

import java.util.Map;

public class GameData {
    private String whiteUsername = "";
    private String blackUsername = "";


    public GameData(int gameId) {
        this.whiteUsername = "";
        this.blackUsername = "";
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

}
