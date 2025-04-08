package server;
import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.*;
import org.eclipse.jetty.websocket.api.annotations.*;
import service.*;


import java.util.*;
import java.util.concurrent.*;

@WebSocket
public class WebSocketHandler {
    private static final Gson gson = new Gson();
//    private final GameService  gameService;
//    private final UserService  userService;
}
