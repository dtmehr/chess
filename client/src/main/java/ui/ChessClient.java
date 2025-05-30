package ui;

import java.util.Scanner;
import java.util.List;
import model.AuthData;
import model.GameData;

public class ChessClient {
    private static ServerFacade facade;
    private static final Scanner SCAN = new Scanner(System.in);
    private static String currentAuthToken = null;
    private static List<GameData> lastGameList;

    public static void begin() {
        int port = 8080;
        facade = new ServerFacade(port);

        while (true) {
            try {
                if (currentAuthToken == null) {
                    opening();
                } else {
                    menu();
                }
            } catch (Exception e) {
                System.out.println("an error occurred: " + e.getMessage());
            }
        }
    }

    private static void opening() throws Exception {
        System.out.println("todd chess");
        System.out.println("help");
        System.out.println("quit");
        System.out.println("login");
        System.out.println("register");
        System.out.print("enter your choice: ");
        String choice = SCAN.nextLine().trim();

        switch (choice.toLowerCase()) {
            case "help":
            case "1":
                System.out.println("choose: help, quit, login, register");
                break;
            case "quit":
            case "2":
                System.out.println("end program");
                System.exit(0);
                break;
            case "login":
            case "3":
                login();
                break;
            case "register":
            case "4":
                register();
                break;
            default:
                System.out.println("invalid input. please try again.");
        }
    }

    private static void login() throws Exception {
        System.out.print("enter username: ");
        String username = SCAN.nextLine().trim();
        System.out.print("enter password: ");
        String password = SCAN.nextLine().trim();
        AuthData authData = facade.login(username, password);
        currentAuthToken = authData.getAuthToken();
        System.out.println("login successful.");
    }

    private static void register() throws Exception {
        System.out.print("enter username: ");
        String username = SCAN.nextLine().trim();
        System.out.print("enter password: ");
        String password = SCAN.nextLine().trim();
        System.out.print("enter email: ");
        String email = SCAN.nextLine().trim();
        AuthData authData = facade.register(username, password, email);
        currentAuthToken = authData.getAuthToken();
        System.out.println("registration successful");
    }

    static void menu() throws Exception {
        System.out.println("main menu");
        System.out.println("help");
        System.out.println("logout");
        System.out.println("create game");
        System.out.println("list games");
        System.out.println("play game");
        System.out.println("observe game");
        System.out.print("enter choice: ");
        String choice = SCAN.nextLine().trim();

        switch (choice.toLowerCase()) {
            case "help":
            case "1":
                System.out.println("commands: help, logout, create game, list games, play game, observe game");
                break;
            case "logout":
            case "2":
                facade.logout(currentAuthToken);
                currentAuthToken = null;
                System.out.println("you have been logged out.");
                break;
            case "create game":
            case "3":
                createGame();
                break;
            case "list games":
            case "4":
                listGames();
                break;
            case "play game":
            case "5":
                playGame();
                break;
            case "observe game":
            case "6":
                observeGame();
                break;
            default:
                System.out.println("invalid input. please try again.");
        }
    }

    private static void createGame() throws Exception {
        System.out.print("enter a name for the new game: ");
        String gameName = SCAN.nextLine().trim();
        int gameID = facade.createGame(currentAuthToken, gameName);
        System.out.println("game '" + gameName + "' created with id: " + gameID);
    }

    private static void listGames() throws Exception {
        lastGameList = facade.listGames(currentAuthToken);
        if (lastGameList == null || lastGameList.isEmpty()) {
            System.out.println("no games available.");
        } else {
            int index = 1;
            System.out.println("available games:");
            for (GameData game : lastGameList) {
                System.out.println(index + ". " + game.getGameName() +
                        " | white: " + (game.getWhiteUsername() != null ? game.getWhiteUsername() : "none") +
                        " | black: " + (game.getBlackUsername() != null ? game.getBlackUsername() : "none"));
                index++;
            }
        }
    }

    private static void playGame() throws Exception {
        if (lastGameList == null || lastGameList.isEmpty()) {
            System.out.println("please list games first.");
            return;
        }
        System.out.print("enter the game number to join: ");
        int number;
        try {
            number = Integer.parseInt(SCAN.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("please enter a valid number");
            return;
        }
        if (number < 1 || number > lastGameList.size()) {
            System.out.println("invalid game number");
            return;
        }
        System.out.print("enter desired color (white/black)");
        String color = SCAN.nextLine().trim().toUpperCase();
        int gameID = lastGameList.get(number - 1).getGameID();
        facade.joinGame(currentAuthToken, gameID, color);
        System.out.println("you have joined game '" + lastGameList.get(number - 1).getGameName() +
                "' as " + color + ".");
        boolean whitePerspective = color.equals("WHITE");

        GameplaySession session = new GameplaySession(currentAuthToken, gameID, whitePerspective);
        session.start();
    }
    private static void observeGame() throws Exception {
        if (lastGameList == null || lastGameList.isEmpty()) {
            System.out.println("please list games first");
            return;
        }
        System.out.print("enter the game number you want to observe: ");
        int number;
        try {
            number = Integer.parseInt(SCAN.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("please enter a valid number");
            return;
        }
        if (number < 1 || number > lastGameList.size()) {
            System.out.println("invalid game number");
            return;
        }
        int gameID = lastGameList.get(number - 1).getGameID();
        System.out.println("Observing game '" + lastGameList.get(number - 1).getGameName() + "'.");
        boolean whitePerspective = true;
        GameplaySession session = new GameplaySession(currentAuthToken, gameID, whitePerspective);
        session.start();
    }
}
