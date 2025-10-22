package datamodel;

public class JoinGameRequest {
    public String playerColor;
    public Integer gameID;

    public String playerColor() {
        return playerColor;
    }

    public Integer getGameID() {
        return gameID;
    }
}
