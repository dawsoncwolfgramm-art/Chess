package ui;

import java.util.Arrays;
import java.util.Scanner;


import client.ServerFacade;

import static ui.EscapeSequences.*;


public class ChessClient {
    private final ServerFacade serverFacade;
    private State state = State.SIGNEDOUT;
    private String clientName;
    private String authToken;

    public ChessClient(String serverUrl) {
        this.serverFacade = new ServerFacade(serverUrl);
    }

    public void run() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Welcome to chess! Type \"help\" to get help!");

        var result = "";
        while (!result.equals("quit")) {
            printPrompt();
            String line = scanner.nextLine();
            try {
                result = eval(line);
                System.out.print(SET_BG_COLOR_BLUE + result);
            } catch (Throwable e) {
                var msg = e.toString();
                System.out.print(msg);
            }
        }
        System.out.println();
    }

    public void printPrompt() {
        System.out.print("\n" + SET_TEXT_COLOR_BLACK + ">>>" + SET_BG_COLOR_GREEN);
    }

    public String eval(String input) {
        try {
            String[] tokens = input.toLowerCase().split(" ");
            String cmd = (tokens.length > 0) ? tokens[0] : "help";
            String[] params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "help" -> help();
                case "register" -> register(params);
                case "login" -> login(params);
                //                case "logout" -> logout(params);
                //                case "create game" -> createGame(params);
                //                case "list games" -> listGames(params);
                //                case "play game" -> joinGame(params);
                //                case "observe game" -> joinGame(params);
                case "quit" -> "quit";
                default -> "";
            };
        } catch (Exception ex) {
            return ex.getMessage();
        }
    }

    public String register(String[] params) throws Exception {
        System.out.println(params[0]);
        System.out.println(params[1]);
        if (params.length == 3) {
            try {
                var auth = serverFacade.register(params);
                authToken = auth.authToken();
                clientName = params[0];
                state = State.SIGNEDIN;
                return "Registered as " + params[0];
            } catch (Exception ex) {
                System.out.println("Failed due to " + ex.getMessage());
            }
        }
        throw new Exception("Expected: <USERNAME> <PASSWORD> <EMAIL>");
    }

    public String login(String[] params) throws Exception {
        if (params.length == 2) {
            try {
                var auth = serverFacade.login(params);
                authToken = auth.authToken();
                clientName = params[0];
                state = State.SIGNEDIN;
                return "Logged In as " + params[0];
            } catch (Exception ex) {
                System.out.println("Failed due to " + ex.getMessage());
            }
        }
        throw new Exception("Expected: <USERNAME> <PASSWORD>");
    }

    public String help() {
        if (state == State.SIGNEDOUT) {
            return """
                    - register <USERNAME> <PASSWORD> <EMAIL> = create an account
                    - login <USERNAME> <PASSWORD> = login to account
                    - quit = exit the program
                    - help = to print possible commands""";
        }
        return """
                - logout = sign out of account
                - create game <GAMENAME> = creates game with name of game
                - list games = show list of games
                - play game <GAMENAME> = joins game through gamename
                - observe game <GAMENAME> = joins game through gamename
                - quit = exit the program
                - help = to print possible commands""";
    }
}

