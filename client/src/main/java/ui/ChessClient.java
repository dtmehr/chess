package ui;

import client.ServerFacade;
import model.AuthData;
import model.GameData;
import java.util.List;
import java.util.Scanner;
//placeholders for now
public class ChessClient {
    private enum ClientMode {PRELOGIN, POSTLOGIN, GAMEPLAY}

    private final ServerFacade server;
    private ClientMode mode;
    private String authToken;

    public ChessClient() {
        server = new ServerFacade( );
        mode = ClientMode.PRELOGIN;
    }
//run method
    public void run() {
        Scanner in = new Scanner(System.in);
        boolean next = true;
//        while loop for conditions
        while (next) {
            switch (mode) {
                case PRELOGIN:
                    next = handlePrelogin(in);
                    break;
                case POSTLOGIN:
                    next = handlePostlogin(in);
                    break;
                case GAMEPLAY:
                    next = handleGameplay(in);
                    break;
            }
        }
        in.close();
    }


}
