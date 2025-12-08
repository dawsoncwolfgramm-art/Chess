package websocket.messages;

public class ErrorMessage extends ServerMessage {
    public String errorMessage;   // required field name for the spec

    public ErrorMessage(String errorMessage) {
        super(ServerMessageType.ERROR);
        this.errorMessage = errorMessage;
    }
}