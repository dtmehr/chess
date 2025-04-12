package websocket.messages;

import com.google.gson.annotations.SerializedName;
import model.GameData;

/**
 * Represents a Message the server can send through a WebSocket
 *
 * Note: You can add to this class, but you should not alter the existing methods.
 */
public class ServerMessage {
    public enum ServerMessageType { LOAD_GAME, NOTIFICATION, ERROR }

    @SerializedName("serverMessageType")
    private ServerMessageType type;
    private String message;
    private GameData game;
    private String errorMessage;

    // Constructor for messages that carry a String payload.
    public ServerMessage(ServerMessageType type, String message) {
        this.type = type;
        if (type == ServerMessageType.ERROR) {
            // For error messages, set errorMessage (so the tests find a non-null value)
            this.errorMessage = message;
        } else {
            this.message = message;
        }
    }

    // Constructor for LOAD_GAME messages that carry a GameData payload.
    public ServerMessage(ServerMessageType type, GameData game) {
        this.type = type;
        this.game = game;
    }

    public ServerMessageType getType() {
        return type;
    }
    public String getMessage() {
        return message;
    }
    public GameData getGame() {
        return game;
    }
    public String getErrorMessage() {
        return errorMessage;
    }

    public void setType(ServerMessageType type) {
        this.type = type;
    }
    public void setMessage(String message) {
        this.message = message;
    }
    public void setGame(GameData game) {
        this.game = game;
    }
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
