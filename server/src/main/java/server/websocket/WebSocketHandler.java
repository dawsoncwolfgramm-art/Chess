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

    private static final Set<WsConnectContext> connections = new HashSet<>();
    private final Map<Integer, Set<WsMessageContext>> gameSessions =
            new ConcurrentHashMap<>();
    private final UserService userService;
    private static final Gson gson = new Gson();

    public WebSocketHandler(UserService userService) {
        this.userService = userService;
    }

    public void connect(WsConnectContext ctx) {
        ctx.enableAutomaticPings();
        connections.add(ctx);
        System.out.println("WebSocket connected");
    }

    public void close(WsCloseContext ctx) {
        connections.remove(ctx);
        System.out.println("WebSocket closed");
    }

    public void message(WsMessageContext ctx) {
        try {
            String json = ctx.message();

            UserGameCommand base = gson.fromJson(json, UserGameCommand.class);
            System.out.println("WS RECEIVED: " + base.getCommandType());

            switch (base.getCommandType()) {
                case CONNECT -> handleConnect(ctx, base);
                case MAKE_MOVE -> {
                    UserGameCommand.Move moveCmd =
                            gson.fromJson(json, UserGameCommand.Move.class);
                    handleMove(ctx, moveCmd);
                }
//                case LEAVE -> handleLeave(ctx, base);
//                case RESIGN -> handleResign(ctx, base);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            ServerMessage error =
                    new websocket.messages.ErrorMessage("Error: " + ex.getMessage());
            ctx.send(gson.toJson(error));
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
            ctx.send(gson.toJson(load));

            broadcast(gameID,
                    new NotificationMessage(username + " connected"));

        } catch (Exception ex) {
            ex.printStackTrace();
            // later: send an ERROR message back
        }
    }

    private void handleMove(WsMessageContext ctx, UserGameCommand.Move command) throws Exception {
        int gameID = command.getGameID();
        String auth = command.getAuthToken();
        ChessMove move = command.getMove();

        String username = userService.getUsername(auth);
        GameData gameData = userService.getGame(gameID);
        ChessGame chessGame = gameData.game();

        try {
            chessGame.makeMove(move);
        } catch (Exception ex) {
            ServerMessage error = new websocket.messages.ErrorMessage("Error: illegal move");
            ctx.send(gson.toJson(error));
            return;
        }

        userService.updateGameState(gameID, chessGame);

        ServerMessage load = new LoadGameMessage(chessGame);
        broadcast(gameID, load);
    }

    private void broadcast(int gameID, ServerMessage message) {
        var sessions = gameSessions.get(gameID);
        if (sessions == null) {
            return;
        }

        String json = gson.toJson(message);
        for (WsMessageContext session : sessions) {
            session.send(json);
        }
    }
}
