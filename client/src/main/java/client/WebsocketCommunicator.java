package client;

import com.google.gson.Gson;
import jakarta.websocket.*;

import ui.ServerMessageObserver;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.net.URI;

public class WebsocketCommunicator extends Endpoint {

    private final ServerMessageObserver observer;
    private final Gson gson = new Gson();
    private Session session;

    public WebsocketCommunicator(String url, ServerMessageObserver observer) throws Exception {
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
            observer.notify(base);
        } catch (Exception ex) {
            System.out.println("Failed to handle WebSocket message: " + ex.getMessage());
        }
    }

    public void send(UserGameCommand command) throws IOException {
        String json = gson.toJson(command);
        session.getBasicRemote().sendText(json);
    }

}
