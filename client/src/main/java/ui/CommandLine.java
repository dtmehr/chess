package ui;
import java.util.Scanner;

import model.AuthData;
import model.GameData;
import java.util.List;

public class CommandLine {
    private static ServerFacade facade;
    private static Scanner scanner = new Scanner(System.in);
    //check if logged in
    private static String currentAuthToken = null;
    private static List<GameData> lastGame;

    public static void main(String[] args) {
        //hard coded change later
        int port = 8080;
        facade = new ServerFacade(port);

        while (true) {
            try {
                if (currentAuthToken == null) {
                    //create these functions
//                    opening();
                } else {
//                    menu();
                }
            } catch (Exception e) {
                System.out.println("An error occurred: " + e.getMessage());
            }
        }
    }

    }