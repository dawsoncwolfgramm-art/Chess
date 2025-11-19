package datamodel;

public class JoinGameRequest {
    public String playerColor;
    public Integer gameID;

    public JoinGameRequest(String color, int gameID) {
        this.playerColor = color;
        this.gameID = gameID;
    }
}
