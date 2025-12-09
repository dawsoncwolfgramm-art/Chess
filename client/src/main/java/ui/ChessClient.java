package ui;

import java.util.Arrays;
import java.util.List;
import java.util.Scanner;


import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;
import client.ServerFacade;
import datamodel.GameData;
import websocket.messages.ServerMessage;

import static ui.EscapeSequences.*;


public class ChessClient implements NotificationHandler {
    private final ServerFacade serverFacade;

    private State state = State.SIGNEDOUT;
    private String clientName;
    private String authToken;
    private String currentColor = "white";
    private Integer currentGameId = null;
    private ChessGame currentGame;   // latest game state from server


    public ChessClient(String serverUrl) {
        this.serverFacade = new ServerFacade(serverUrl, this);
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
                case "joingame" -> joinGame(params);
                case "observegame" -> observeGame(params);
                case "clear" -> clear();
                case "move" -> makeMove(params);
                case "redraw" -> redrawBoard();
//                case "leave" -> leaveGameCommand();
//                case "resign" -> resignGameCommand();
//                case "highlight" -> highlightMoves(params);
                case "quit" -> "quit";
                default -> "";
            };
        } catch (Exception ex) {
            String cleanMessage = extractErrorMessage(ex);
            return cleanMessage;
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
            String cleanMessage = extractErrorMessage(ex);
            return "Registration failed: " + cleanMessage;
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
            String cleanMessage = extractErrorMessage(ex);
            return "Login failed: " + cleanMessage;
        }
    }

    public String logout() throws Exception {
        assertSignedIn();
        try {
            serverFacade.logout(authToken);
            state = State.SIGNEDOUT;
            return String.format("%s left chess", clientName);
        } catch (Exception ex) {
            String cleanMessage = extractErrorMessage(ex);
            return "Logout failed: " + cleanMessage;
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
            return "Created Game Successful: Game Number = " + gameID;
        } catch (Exception ex) {
            String cleanMessage = extractErrorMessage(ex);
            return "Create Game failed: " + cleanMessage;
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
                        .append("  Name: ").append(g.gameName())
                        .append("  White: ").append(white)
                        .append("  Black: ").append(black)
                        .append("\n");
            }

            return stringBuilder.toString();
        } catch (Exception ex) {
            String cleanMessage = extractErrorMessage(ex);
            return "List Games failed: " + cleanMessage;
        }
    }

    public String joinGame(String[] params) throws Exception {
        assertSignedIn();
        if (params.length != 2) {
            throw new Exception("Expected: <GAMECOLOR> <GAMEID>");
        }
        if (!(params[0].equalsIgnoreCase("black")) && !(params[0].equalsIgnoreCase("white"))) {
            throw new Exception("Expected: <GAMECOLOR (white or black)> <GAMEID>");
        }
        try {
            String gameColor = params[0];
            String gameId = params[1];
            currentColor = gameColor.toLowerCase();
            currentGameId = Integer.parseInt(gameId);
            List<GameData> games = serverFacade.listGames(authToken);
            GameData chosenGame = null;
            for (GameData g : games) {
                if (g.gameID() == Integer.parseInt(gameId)) {
                    chosenGame = g;
                    break;
                }
            }
            if (chosenGame == null) {
                return "No game found";
            }
            serverFacade.joinGame(authToken, gameColor, gameId);
            serverFacade.connectToGame(authToken, gameId);
            state = State.GAMEPLAY;
            return "Joined Game Successful";
        } catch (Exception ex) {
            String cleanMessage = extractErrorMessage(ex);
            return "Joined Game failed: " + cleanMessage;
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
                return "No game found";
            }
            currentColor = "white";
            serverFacade.connectToGame(authToken, gameId);
            state = State.OBSERVE;
            return "Observing Game Successful";
        } catch (Exception ex) {
            String cleanMessage = extractErrorMessage(ex);
            return "Joined Game failed: " + cleanMessage;
        }
    }

    public String clear() throws Exception {
        assertSignedIn();
        try {
            serverFacade.clear();
            state = State.SIGNEDOUT;
            return "Clear Successful";
        } catch (Exception ex) {
            String cleanMessage = extractErrorMessage(ex);
            return "Clear failed: " + cleanMessage;

        }
    }

    public String makeMove(String[] params) throws Exception {
        assertGamePlay();
        if (currentGameId == null) {
            throw new Exception("You must join a game first");
        }
        if (params.length != 2) {
            throw new Exception("Expected: move <startSquare> <endSquare>  (example, move a2 a3)");
        }

        try {
            ChessPosition start = parseSquare(params[0]);
            ChessPosition end = parseSquare(params[1]);
            ChessMove move = new ChessMove(start, end, null); // no promotion yet

            serverFacade.sendMove(authToken, currentGameId, move);
            return "Move sent.";
        } catch (Exception ex) {
            return "Move failed: " + extractErrorMessage(ex);
        }
    }

    private ChessPosition parseSquare(String square) throws Exception {
        if (square == null || square.length() != 2) {
            throw new Exception("Expected positions like a2, h7, etc.");
        }

        char colLetter = Character.toLowerCase(square.charAt(0));
        char rowNum = square.charAt(1);

        if (colLetter < 'a' || colLetter > 'h') {
            throw new Exception("Column (colLetter) must be a–h");
        }
        if (rowNum < '1' || rowNum > '8') {
            throw new Exception("Row (rowNum) must be 1–8");
        }

        int col = colLetter - 'a' + 1;
        int row = rowNum - '0';

        return new ChessPosition(row, col);
    }

    private String redrawBoard() throws Exception {
        assertGamePlay();
        if (currentGameId == null) {
            return "No game board to redraw.";
        }
        DrawChessBoard drawer = new DrawChessBoard(currentColor);
        drawer.printChessBoard(currentGame.getBoard());
        return "";
    }

    public String help() {
        if (state == State.SIGNEDOUT) {
            return """
                    - register <USERNAME> <PASSWORD> <EMAIL> = create an account
                    - login <USERNAME> <PASSWORD>            = login to account
                    - quit                                   = exit the program
                    - help                                   = to print possible commands""";
        } else if (state == State.GAMEPLAY) {
            return """
                    Gameplay commands:
                    - move <from> <to>         example = move e2 e4
                    - highlight <square>       example = highlight e2
                    - redraw                   re-draw the current board
                    - leave                    leave the game (back to lobby)
                    - resign                   resign the game (but stay in board view)
                    - help                     show this list""";
        } else if (state == State.OBSERVE) {
            return """
                    Observe commands:
                    - redraw                   re-draw the current board
                    - leave                    leave the game (back to lobby)
                    - help                     show this list""";
        }
        return """
                - logout                             = sign out of account
                - creategame <GAMENAME>              = creates game with name of game
                - listgames                          = show list of games
                - joingame <GAMECOLOR> <GAMEID>      = joins game through gamename
                - observegame <GAMEID>               = joins game through gamename
                - quit                               = exit the program
                - help                               = to print possible commands""";
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

    private void assertGamePlay() throws Exception {
        if (state != State.GAMEPLAY) {
            throw new Exception("Need to join a game");
        }
    }

    private void assertObserve() throws Exception {
        if (state == State.OBSERVE) {
            throw new Exception("You can only Observe");
        }
    }


    private String extractErrorMessage(Exception ex) {
        String raw = ex.getMessage();
        int start = raw.indexOf("Error:");
        if (start == -1) {
            return raw;
        }
        int end = raw.indexOf('"', start);
        if (end == -1) {
            end = raw.length();
        }

        return raw.substring(start, end);
    }

    @Override
    public void notify(ServerMessage message) {
        switch (message.getServerMessageType()) {
            case LOAD_GAME -> {
                var load = (websocket.messages.LoadGameMessage) message;
                this.currentGame = load.game;
                var board = currentGame.getBoard();
                String color = currentColor;
                DrawChessBoard drawer = new DrawChessBoard(color);
                drawer.printChessBoard(board);
            }

            case NOTIFICATION -> {
                var note = (websocket.messages.NotificationMessage) message;
                System.out.println("NOTIFICATION: " + note.message);
            }

            case ERROR -> {
                var err = (websocket.messages.ErrorMessage) message;
                System.out.println(err.message);
            }
        }
        printPrompt();
        System.out.flush();
    }
}

