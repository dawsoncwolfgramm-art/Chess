package ui;

import java.util.Arrays;
import java.util.List;
import java.util.Scanner;


import client.ServerFacade;
import datamodel.GameData;

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
                System.out.print(SET_TEXT_COLOR_BLUE + result + SET_BG_COLOR_BLACK);
            } catch (Throwable e) {
                var msg = e.toString();
                System.out.print(msg);
            }
        }
        System.out.println();
    }

    public void printPrompt() {
        System.out.print("\n" + SET_TEXT_COLOR_WHITE + ">>>" + SET_BG_COLOR_BLACK);
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
                case "logout" -> logout();
                case "creategame" -> createGame(params);
                case "listgames" -> listGames();
                case "playgame" -> joinGame(params);
                case "observegame" -> observeGame(params);
                case "clear" -> clear();
                case "quit" -> "quit";
                default -> "";
            };
        } catch (Exception ex) {
            return ex.getMessage();
        }
    }

    public String register(String[] params) throws Exception {
        assertSignedOut();
        if (params.length != 3) {
            throw new Exception("Expected: <USERNAME> <PASSWORD> <EMAIL>");
        }
        try {
            var auth = serverFacade.register(params);
            authToken = auth.authToken();
            clientName = params[0];
            state = State.SIGNEDIN;
            return "Registered as " + params[0];
        } catch (Exception ex) {
            return "Registration failed: " + ex.getMessage();
        }
    }

    public String login(String[] params) throws Exception {
        assertSignedOut();
        if (params.length != 2) {
            throw new Exception("Expected: <USERNAME> <PASSWORD>");
        }
        try {
            var auth = serverFacade.login(params);
            authToken = auth.authToken();
            clientName = params[0];
            state = State.SIGNEDIN;
            return "Logged In as " + params[0];
        } catch (Exception ex) {
            return "Login failed: " + ex.getMessage();
        }
    }

    public String logout() throws Exception {
        assertSignedIn();
        try {
            serverFacade.logout(authToken);
            state = State.SIGNEDOUT;
            return String.format("%s left chess", clientName);
        } catch (Exception ex) {
            return "Logout failed: " + ex.getMessage();
        }
    }

    public String createGame(String[] params) throws Exception {
        assertSignedIn();
        if (params.length != 1) {
            throw new Exception("Expected: <GANENAME>");
        }
        try {
            String gameName = params[0];
            Integer gameID = serverFacade.createGame(authToken, gameName);
            return "Created Game Successful: GameID = " + gameID;
        } catch (Exception ex) {
            return "Create Game failed: " + ex.getMessage();
        }
    }

    public String listGames() throws Exception {
        assertSignedIn();
        try {
            List<GameData> games = serverFacade.listGames(authToken);
            if (games.isEmpty()) {
                return "No games currently available.";
            }
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Games:\n");

            for (int i = 0; i < games.size(); i++) {
                GameData g = games.get(i);

                int listNumber = i + 1;

                String white = (g.whiteUsername() == null) ? "-" : g.whiteUsername();
                String black = (g.blackUsername() == null) ? "-" : g.blackUsername();

                stringBuilder.append(listNumber).append(". ")
                        .append("GameID: ").append(g.gameID())
                        .append("  Name: ").append(g.gameName())
                        .append("  White: ").append(white)
                        .append("  Black: ").append(black)
                        .append("\n");
            }

            return stringBuilder.toString();
        } catch (Exception ex) {
            return "List Games failed: " + ex.getMessage();
        }
    }

    public String joinGame(String[] params) throws Exception {
        assertSignedIn();
        if (params.length != 2) {
            throw new Exception("Expected: <GAMECOLOR> <GAMEID>");
        }
        try {
            String gameColor = params[0];
            String gameId = params[1];
            List<GameData> games = serverFacade.listGames(authToken);
            GameData chosenGame = null;
            for (GameData g : games) {
                if (g.gameID() == Integer.parseInt(gameId)) {
                    chosenGame = g;
                    break;
                }
            }
            if (chosenGame == null) {
                return "No game found with ID " + gameId;
            }

            serverFacade.joinGame(authToken, gameColor, gameId);
            return "Joined Game Successful: GameID = " + gameId;
        } catch (Exception ex) {
            return "Joined Game failed: " + ex.getMessage();
        }
    }

    public String observeGame(String[] params) throws Exception {
        assertSignedIn();
        if (params.length != 1) {
            throw new Exception("Expected: <GAMEID>");
        }
        try {
            String gameId = params[0];
            List<GameData> games = serverFacade.listGames(authToken);
            GameData chosenGame = null;
            for (GameData g : games) {
                if (g.gameID() == Integer.parseInt(gameId)) {
                    chosenGame = g;
                    break;
                }
            }
            if (chosenGame == null) {
                return "No game found with ID " + gameId;
            }
            return "Joined Game Successful: GameID = " + gameId;
        } catch (Exception ex) {
            return "Joined Game failed: " + ex.getMessage();
        }
    }

    public void makeChessBoard() {
        for (int x = 0; x < 8; x++) {
            for (int y = 0; y < 8; y++) {
                System.out.print(SET_TEXT_COLOR_BLUE + x + SET_BG_COLOR_BLACK);
                System.out.print(SET_TEXT_COLOR_BLUE + y + SET_BG_COLOR_WHITE);
            }
        }
    }


    public String clear() throws Exception {
        assertSignedIn();
        try {
            serverFacade.clear();
            state = State.SIGNEDOUT;
            return "Clear Successful";
        } catch (Exception ex) {
            return "Clear failed: " + ex.getMessage();

        }
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
                - creategame <GAMENAME> = creates game with name of game
                - listgames = show list of games
                - playgame <GAMECOLOR> <GAMEID> = joins game through gamename
                - observegame <GAMEID> = joins game through gamename
                - quit = exit the program
                - help = to print possible commands""";
    }

    private void assertSignedIn() throws Exception {
        if (state == State.SIGNEDOUT) {
            throw new Exception("You must sign in");
        }
    }

    private void assertSignedOut() throws Exception {
        if (state == State.SIGNEDIN) {
            throw new Exception("You're already Signed In");
        }
    }
}

