package ui;

import websocket.messages.*;

public class ConsoleNotificationHandler implements NotificationHandler {

    @Override
    public void notify(ServerMessage message) {
        switch (message.getServerMessageType()) {
            case LOAD_GAME -> {
                LoadGameMessage m = (LoadGameMessage) message;
                System.out.println("LOAD_GAME received from server");
                System.out.println("Game object: " + m.game);
                // later: DrawChessBoard.draw(m.game, ...);
            }
            case NOTIFICATION -> {
                NotificationMessage n = (NotificationMessage) message;
                System.out.println("NOTIFICATION: " + n.message);
            }
            case ERROR -> {
                System.out.println("ERROR message from server");
            }
        }
    }
}
