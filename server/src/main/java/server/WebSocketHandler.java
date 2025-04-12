package server;

import chess.ChessGame;
import chess.InvalidMoveException;
import com.google.gson.Gson;
import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import dataaccess.SqlGameDAO;
import dataaccess.UserDAO;
import model.GameData;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;
import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

@WebSocket
public class WebSocketHandler {

    static UserDAO userDAO;
    static AuthDAO authDAO;
    static GameDAO gameDAO;

    public WebSocketHandler() throws DataAccessException {
        userDAO = new UserDAO();

        authDAO = new dataaccess.SqlAuthDAO();
        gameDAO = new SqlGameDAO();
    }

    private final ConnectionM connections = new ConnectionM();
    public static void initialize(UserDAO userDAO, AuthDAO authDAO, GameDAO gameDAO) {}

    //cases
    @OnWebSocketMessage
    public void onMessage(Session session, String message) {
        //make a switch with cases
        try {
            UserGameCommand command = new Gson().fromJson(message, UserGameCommand.class);
            switch (command.getCommandType()) {
                case CONNECT -> connect(session, command);
                case MAKE_MOVE -> makeMove(session, command);
                case LEAVE -> leave(session, command);
                case RESIGN -> resign(session, command);
            }
        } catch (Exception e) {
            try {
                ServerMessage errorMessage = new ServerMessage(
                        ServerMessage.ServerMessageType.ERROR,
                        "Error: " + e.getMessage()
                );
                session.getRemote().sendString(new Gson().toJson(errorMessage));
            } catch (IOException ignored) {}
        }
    }
//connect
    private void connect(Session session, UserGameCommand command) throws IOException, DataAccessException {
        if (authDAO.getAuth(command.getAuthToken()) == null) {
            session.getRemote().sendString(jsonError("Error: unauthenticated user"));
            return;
        }
        String username = authDAO.getAuth(command.getAuthToken()).getUsername();
        int gameID = command.getGameID();
        GameData game = gameDAO.getGame(gameID);
        if (game == null) {
            session.getRemote().sendString(jsonError("Error: game not found"));
            return;
        }

        String playerColor;
        if (username.equals(game.getWhiteUsername())) {
            playerColor = "WHITE";
        } else if (username.equals(game.getBlackUsername())) {
            playerColor = "BLACK";
        } else if (game.getWhiteUsername() == null) {
            playerColor = "WHITE";
            gameDAO.joinGame(gameID, command.getAuthToken(), "WHITE");
            game.setWhiteUsername(username);
            gameDAO.updateGame(gameID, game);
        } else if (game.getBlackUsername() == null) {
            playerColor = "BLACK";
            gameDAO.joinGame(gameID, command.getAuthToken(), "BLACK");
            game.setBlackUsername(username);
            gameDAO.updateGame(gameID, game);
        } else {
            playerColor = "OBSERVER";
        }

        connections.add(username, session, gameID);
        ServerMessage loadGameMessage = new ServerMessage(ServerMessage.ServerMessageType.LOAD_GAME, game);
        session.getRemote().sendString(new Gson().toJson(loadGameMessage));
        String notificationText = String.format("%s joined the game as %s.", username, playerColor);
        ServerMessage notification = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, notificationText);
        connections.broadcast(username, notification, gameID);
    }

    private String PlayerColo(GameData game, String authToken) throws DataAccessException {
        String username = authDAO.getAuth(authToken).getUsername();
        if (username.equals(game.getWhiteUsername())) {
            return "WHITE";
        } else if (username.equals(game.getBlackUsername())) {
            return "BLACK";
        } else if (game.getWhiteUsername() == null || game.getWhiteUsername().isEmpty()) {
            gameDAO.joinGame(game.getGameID(), authToken, "WHITE");
            game.setWhiteUsername(username);
            gameDAO.updateGame(game.getGameID(), game);
            return "WHITE";
        } else if (game.getBlackUsername() == null || game.getBlackUsername().isEmpty()) {
            gameDAO.joinGame(game.getGameID(), authToken, "BLACK");
            game.setBlackUsername(username);
            gameDAO.updateGame(game.getGameID(), game);
            return "BLACK";
        } else {
            return "OBSERVER";
        }
    }

    private void resign(Session session, UserGameCommand command) throws DataAccessException, IOException {
        String username = authDAO.getAuth(command.getAuthToken()).getUsername();
        int gameID = command.getGameID();
        GameData game = gameDAO.getGame(gameID);
        if (game.isGameOver()) {
            session.getRemote().sendString(jsonError("Error: game already ended"));
            return;
        }
        if (!Objects.equals(username, game.getWhiteUsername())
                && !Objects.equals(username, game.getBlackUsername())) {
            session.getRemote().sendString(jsonError("Error: cannot resign as an observer"));
            return;
        }
        if (game.getChessGame() != null) {
            game.getChessGame().setResigned();
        }
        game.setGameOver(true);
        game.setResignedPlayer(username);
        gameDAO.updateGame(gameID, game);
        String notificationText = String.format("%s resigned from the game", username);
        ServerMessage notification = new ServerMessage(
                ServerMessage.ServerMessageType.NOTIFICATION, notificationText
        );
        connections.broadcastToGame(notification, gameID);
    }

    private void leave(Session session, UserGameCommand command)
            throws DataAccessException, IOException {
        String username = authDAO.getAuth(command.getAuthToken()).getUsername();
        connections.remove(username);
        GameData game = gameDAO.getGame(command.getGameID());
        if (username.equals(game.getWhiteUsername())) {
            game.setWhiteUsername(null);
        } else if (username.equals(game.getBlackUsername())) {
            game.setBlackUsername(null);
        }
        gameDAO.updateGame(command.getGameID(), game);
        String notificationText = String.format("%s left the game", username);
        ServerMessage notification = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, notificationText);
        connections.broadcast(username, notification, command.getGameID());
    }

    public void makeMove(Session session, UserGameCommand command)
            throws DataAccessException, IOException {
        try {
            if (command.getAuthToken() == null
                    || authDAO.getAuth(command.getAuthToken()) == null) {
                session.getRemote().sendString(jsonError("Error: user not authenticated"));
                return;
            }
            String username = authDAO.getAuth(command.getAuthToken()).getUsername();
            int gameID = command.getGameID();
            GameData game = gameDAO.getGame(gameID);
            ChessGame chess = game.getChessGame();
            if (game.getResignedPlayer() != null || game.isGameOver()) {
                session.getRemote().sendString(jsonError("Error: game is already over"));
                return;
            }
            String playerColor = PlayerColo(game, command.getAuthToken());
            if (playerColor.equals("OBSERVER")) {
                session.getRemote().sendString(jsonError("Error: cannot move as an observer"));
                return;
            }
            ChessGame.TeamColor teamTurn = chess.getTeamTurn();
            ChessGame.TeamColor oppColor = (teamTurn == ChessGame.TeamColor.WHITE)
                    ? ChessGame.TeamColor.BLACK
                    : ChessGame.TeamColor.WHITE;
            boolean wrongTurn = (teamTurn == ChessGame.TeamColor.WHITE && playerColor.equals("BLACK"))
                    || (teamTurn == ChessGame.TeamColor.BLACK && playerColor.equals("WHITE"));
            if (wrongTurn) {
                session.getRemote().sendString(jsonError("Error: not your turn"));
                return;
            }
            if (command.getMove().getStartPosition() == null
                    || !chess.validMoves(command.getMove().getStartPosition()).contains(command.getMove())) {
                session.getRemote().sendString(jsonError("Error: invalid move"));
                return;
            }
            chess.makeMove(command.getMove());
            gameDAO.updateGame(gameID, game);
            String moveNote = chess.isInCheck(oppColor)
                    ? "check!"
                    : String.format("%s made a move", username);
            connections.broadcast(username, new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, moveNote), gameID);
            if (chess.isInCheckmate(oppColor) || chess.isInStalemate(oppColor)) {
                chess.setResigned();
                game.setGameOver(true);
                gameDAO.updateGame(gameID, game);

                String finalMessage = chess.isInCheckmate(oppColor)
                        ? oppColor + " is in checkmate! Game over."
                        : oppColor + " is in stalemate! Game over.";
                ServerMessage gameOverNote = new ServerMessage(
                        ServerMessage.ServerMessageType.NOTIFICATION,
                        finalMessage
                );
                connections.broadcastToGame(gameOverNote, gameID);
            }
            ServerMessage loadGameMessage = new ServerMessage(
                    ServerMessage.ServerMessageType.LOAD_GAME,
                    game
            );
            connections.broadcastToGame(loadGameMessage, gameID);

        } catch (InvalidMoveException e) {
            ServerMessage errorMsg = new ServerMessage(
                    ServerMessage.ServerMessageType.ERROR,
                    "Error: invalid move"
            );
            connections.broadcastToGame(errorMsg, command.getGameID());
            GameData game = gameDAO.getGame(command.getGameID());
            if (game.isGameOver() && game.getResignedPlayer() == null) {
                ServerMessage finalLoad = new ServerMessage(
                        ServerMessage.ServerMessageType.LOAD_GAME,
                        game
                );
                connections.broadcastToGame(finalLoad, command.getGameID());
            }
        }
    }

            //helpr
    private String jsonError(String msg) {
        return new Gson().toJson(
                new ServerMessage(ServerMessage.ServerMessageType.ERROR, msg)
        );
    }



    private static class Connection {
        public final Session session;
        public final int gameID;
        public Connection(Session session, int gameID) {
            this.session = session;
            this.gameID = gameID;}
        public void send(String message) throws IOException {
            if (session != null && session.isOpen()) {
                session.getRemote().sendString(message);
            }
        }
    }
    //wasn't working added it here
    private static class ConnectionM {
        private final Map<String, Connection> connections = new ConcurrentHashMap<>();

        public void add(String username, Session session, int gameID) {
            connections.put(username, new Connection(session, gameID));
        }
        public void remove(String username) {
            connections.remove(username);
        }
    //same

        public void broadcast(String fromUsername, ServerMessage message, int gameID)
                throws IOException {
            String jsonMessage = new Gson().toJson(message);
            for (Map.Entry<String, Connection> entry : connections.entrySet()) {
                if (entry.getValue().gameID == gameID && !entry.getKey().equals(fromUsername)) {
                    if (entry.getValue().session.isOpen()) {
                        entry.getValue().send(jsonMessage);
                    }
                }
            }
        }
//same
        public void broadcastToGame(ServerMessage message, int gameID)
                throws IOException {
            String jsonMessage = new Gson().toJson(message);
            for (Connection connection : connections.values()) {
                if (connection.gameID == gameID && connection.session.isOpen()) {
                    connection.send(jsonMessage);
                }
            }
        }
    }



}
