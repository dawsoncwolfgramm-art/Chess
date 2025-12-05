package server.websocket;

import com.google.gson.Gson;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;
import io.javalin.websocket.WsMessageContext;
import io.javalin.websocket.WsConnectContext;
import io.javalin.websocket.WsCloseContext;

import java.util.HashSet;
import java.util.Set;

public class WebSocketHandler {

    private static final Set<WsConnectContext> connections = new HashSet<>();
    private static final Gson gson = new Gson();

    public void connect(WsConnectContext ctx) {
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

            ServerMessage response =
                    new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION);

            ctx.send(gson.toJson(response));

        } catch (Exception ex) {
            ServerMessage error =
                    new ServerMessage(ServerMessage.ServerMessageType.ERROR);
            ctx.send(gson.toJson(error));
        }
    }
}
