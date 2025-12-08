package server.websocket;

import chess.ChessGame;
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

            UserGameCommand command = gson.fromJson(json, UserGameCommand.class);
            System.out.println("WS RECEIVED: " + command.getCommandType());

            switch (command.getCommandType()) {
                case CONNECT -> handleConnect(ctx, command);
                // case MAKE_MOVE -> handleMove(ctx, command);
                // case LEAVE -> handleLeave(ctx, command);
                // case RESIGN -> handleResign(ctx, command);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
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
