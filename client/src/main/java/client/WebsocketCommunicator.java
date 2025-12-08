package client;

import chess.ChessMove;
import com.google.gson.Gson;
import jakarta.websocket.*;

import ui.NotificationHandler;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.net.URI;

public class WebsocketCommunicator extends Endpoint {

    private final NotificationHandler observer;
    private final Gson gson = new Gson();
    private Session session;

    public WebsocketCommunicator(String url, NotificationHandler observer) throws Exception {
        try {
            url = url.replace("http", "ws");
            URI socketURI = new URI(url + "/ws");
            this.observer = observer;
            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, socketURI);
            this.session.addMessageHandler(new MessageHandler.Whole<String>() {
                @Override
                public void onMessage(String message) {
                    handleIncomingMessage(message);
                }
            });
        } catch (Exception ex) {
            throw new Exception("WebSocket connection failed: " + ex.getMessage(), ex);
        }
    }

    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {
        System.out.println("WebSocket opened: " + session.getId());

    }

    private void handleIncomingMessage(String incomeMessage) {
        try {
            ServerMessage base = gson.fromJson(incomeMessage, ServerMessage.class);

            switch (base.getServerMessageType()) {
                case LOAD_GAME -> {
                    var msg = gson.fromJson(incomeMessage, websocket.messages.LoadGameMessage.class);
                    observer.notify(msg);
                }
                case NOTIFICATION -> {
                    var msg = gson.fromJson(incomeMessage, websocket.messages.NotificationMessage.class);
                    observer.notify(msg);
                }
                case ERROR -> {
                    // If you later create ErrorMessage
                    observer.notify(base); // or cast to your ErrorMessage subclass
                }
            }
        } catch (Exception ex) {
            System.out.println("Failed to handle WebSocket message: " + ex.getMessage());
        }
    }

    public void send(UserGameCommand command) throws IOException {
        String json = gson.toJson(command);
        session.getBasicRemote().sendText(json);
    }


    public void sendConnect(String authToken, int gameId) throws IOException {
        UserGameCommand cmd = new UserGameCommand(
                UserGameCommand.CommandType.CONNECT,
                authToken,
                gameId
        );
        send(cmd);
    }

    public void sendLeave(String authToken, int gameId) throws IOException {
        UserGameCommand cmd = new UserGameCommand(
                UserGameCommand.CommandType.LEAVE,
                authToken,
                gameId
        );
        send(cmd);
    }

    public void join(String authToken, Integer gameID) throws IOException {
        var command = new UserGameCommand(
                UserGameCommand.CommandType.CONNECT,
                authToken,
                gameID
        );
        session.getBasicRemote().sendText(gson.toJson(command)); // reuse gson field
    }

    public void sendMakeMove(String authToken, int gameId, ChessMove move) throws IOException {
        UserGameCommand.Move cmd = new UserGameCommand.Move(
                UserGameCommand.CommandType.MAKE_MOVE,
                authToken,
                gameId,
                move
        );
        send(cmd);
    }
}
