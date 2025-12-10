package server.websocket;

import chess.ChessGame;
import chess.ChessMove;
import com.google.gson.Gson;
import datamodel.GameData;
import websocket.commands.UserGameCommand;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;
import io.javalin.websocket.WsMessageContext;
import io.javalin.websocket.WsConnectContext;
import io.javalin.websocket.WsCloseContext;

import service.UserService;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class WebSocketHandler {

    private static final Set<WsConnectContext> CONNECTIONS = new HashSet<>();
    private final Map<Integer, Set<WsMessageContext>> gameSessions =
            new ConcurrentHashMap<>();
    private final UserService userService;
    private static final Gson GSON = new Gson();


    public WebSocketHandler(UserService userService) {
        this.userService = userService;
    }

    public void clear() {
        gameSessions.clear();
        CONNECTIONS.clear();
    }

    public void connect(WsConnectContext ctx) {
        ctx.enableAutomaticPings();
        CONNECTIONS.add(ctx);
        System.out.println("WebSocket connected");
    }

    public void close(WsCloseContext ctx) {
        CONNECTIONS.remove(ctx);
        System.out.println("WebSocket closed");
    }

    public void message(WsMessageContext ctx) {
        try {
            String json = ctx.message();

            UserGameCommand base = GSON.fromJson(json, UserGameCommand.class);
            System.out.println("WS RECEIVED: " + base.getCommandType());

            switch (base.getCommandType()) {
                case CONNECT -> handleConnect(ctx, base);
                case MAKE_MOVE -> {
                    UserGameCommand.Move moveCmd = GSON.fromJson(json, UserGameCommand.Move.class);
                    handleMove(ctx, moveCmd);
                }
                case LEAVE -> handleLeave(ctx, base);
                case RESIGN -> handleResign(ctx, base);
            }

        } catch (Exception ex) {
            ServerMessage error =
                    new websocket.messages.ErrorMessage("Error: " + ex.getMessage());
            ctx.send(GSON.toJson(error));
        }
    }


    private void handleConnect(WsMessageContext ctx, UserGameCommand command) {
        try {
            int gameID = command.getGameID();
            String auth = command.getAuthToken();
            String username = userService.getUsername(auth);

            gameSessions.putIfAbsent(gameID, ConcurrentHashMap.newKeySet());
            gameSessions.get(gameID).add(ctx);

            GameData gameData = userService.getGame(gameID);
            ChessGame chessGame = gameData.game();

            ServerMessage load = new LoadGameMessage(chessGame);
            ctx.send(GSON.toJson(load));
            NotificationMessage note =
                    new NotificationMessage(username + " joined the game");
            broadcastToOthers(gameID, ctx, note);

        } catch (Exception ex) {
            ServerMessage error =
                    new websocket.messages.ErrorMessage("Error: " + ex.getMessage());
            ctx.send(GSON.toJson(error));
        }
    }

    private void handleLeave(WsMessageContext ctx, UserGameCommand command) {
        try {
            int gameID = command.getGameID();
            String auth = command.getAuthToken();
            String username = userService.getUsername(auth);

            var sessions = gameSessions.get(gameID);
            if (sessions != null) {
                sessions.remove(ctx);
            }
            userService.leaveGame(auth, gameID);

            NotificationMessage note =
                    new NotificationMessage(username + " left the game");
            broadcastToOthers(gameID, ctx, note);
        } catch (Exception ex) {
            ServerMessage error =
                    new websocket.messages.ErrorMessage("Error: " + ex.getMessage());
            ctx.send(GSON.toJson(error));
        }
    }

    private void handleResign(WsMessageContext ctx, UserGameCommand command) {
        try {
            int gameID = command.getGameID();
            String auth = command.getAuthToken();
            String username = userService.getUsername(auth);

            var sessions = gameSessions.get(gameID);
            if (sessions != null) {
                sessions.remove(ctx);
            }
            userService.leaveGame(auth, gameID);

            NotificationMessage note =
                    new NotificationMessage(username + " has resigned");
            broadcastToOthers(gameID, ctx, note);
        } catch (Exception ex) {
            ServerMessage error =
                    new websocket.messages.ErrorMessage("Error: " + ex.getMessage());
            ctx.send(GSON.toJson(error));
        }
    }

    private void handleMove(WsMessageContext ctx, UserGameCommand.Move command) throws Exception {
        int gameID = command.getGameID();
        String auth = command.getAuthToken();
        ChessMove move = command.getMove();

        String username = userService.getUsername(auth);
        GameData gameData = userService.getGame(gameID);
        ChessGame chessGame = gameData.game();

        String start = toChessNotation(
                move.getStartPosition().getRow(),
                move.getStartPosition().getColumn()
        );

        String end = toChessNotation(
                move.getEndPosition().getRow(),
                move.getEndPosition().getColumn()
        );

        try {
            ChessGame.TeamColor playerColor = null;
            if (username != null) {
                if (username.equals(gameData.whiteUsername())) {
                    playerColor = ChessGame.TeamColor.WHITE;
                } else if (username.equals(gameData.blackUsername())) {
                    playerColor = ChessGame.TeamColor.BLACK;
                }
            }
            if (playerColor == null) {
                ServerMessage error =
                        new websocket.messages.ErrorMessage("Error: you are not a player in this game");
                ctx.send(GSON.toJson(error));
                return;
            }
            if (chessGame.getTeamTurn() != playerColor) {
                ServerMessage error =
                        new websocket.messages.ErrorMessage("Error: it is not your turn");
                ctx.send(GSON.toJson(error));
                return;
            }
            var piece = chessGame.getBoard().getPiece(move.getStartPosition());
            if (piece == null || piece.getTeamColor() != playerColor) {
                ServerMessage error =
                        new websocket.messages.ErrorMessage("Error: cannot move opponent's piece");
                ctx.send(GSON.toJson(error));
                return;
            }
            chessGame.makeMove(move);
            userService.updateGameState(gameID, chessGame);
        } catch (chess.InvalidMoveException ex) {
            String msg = "Move " + start + " to " + end + " not allowed: " + ex.getMessage();
            ServerMessage error = new websocket.messages.ErrorMessage(msg);
            ctx.send(GSON.toJson(error));
            return;
        } catch (Exception ex) {
            ServerMessage error = new websocket.messages.ErrorMessage("Error: " + ex.getMessage());
            ctx.send(GSON.toJson(error));
            return;
        }

        ServerMessage load = new LoadGameMessage(chessGame);
        broadcast(gameID, load);

        String desc = username + " moved from " + start + " to " + end;

        NotificationMessage note =
                new NotificationMessage(desc);
        broadcastToOthers(gameID, ctx, note);
        if (chessGame.isInCheck(ChessGame.TeamColor.WHITE)) {
            broadcast(gameID, new NotificationMessage("White is in check"));
        }
        if (chessGame.isInCheck(ChessGame.TeamColor.BLACK)) {
            broadcast(gameID, new NotificationMessage("Black is in check"));
        }
        if (chessGame.isInCheckmate(ChessGame.TeamColor.WHITE)) {
            broadcast(gameID, new NotificationMessage("White is in checkmate"));
        }
        if (chessGame.isInCheckmate(ChessGame.TeamColor.BLACK)) {
            broadcast(gameID, new NotificationMessage("Black is in checkmate"));
        }
    }

    private String toChessNotation(int row, int col) {
        char file = (char) ('a' + col - 1);
        return "" + file + row;
    }

    private void broadcast(int gameID, ServerMessage message) {
        var sessions = gameSessions.get(gameID);
        if (sessions == null) {
            return;
        }

        String json = GSON.toJson(message);
        for (WsMessageContext session : sessions) {
            session.send(json);
        }
    }

    private void broadcastToOthers(int gameID, WsMessageContext exclude, ServerMessage message) {
        var sessions = gameSessions.get(gameID);
        if (sessions == null) {
            return;
        }

        String json = GSON.toJson(message);
        for (WsMessageContext session : sessions) {
            if (session.session != exclude.session) {
                session.send(json);
            }
        }
    }
}
