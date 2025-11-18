import chess.*;
import ui.ChessClient;
import server.ServerFacade;

public class Main {
    public static void main(String[] args) {
        var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        System.out.println("♕ 240 Chess Client: " + piece);
        int port = 8080;
        ServerFacade facade = new ServerFacade(port);
        ChessClient app = new ChessClient(facade);
        app.run();

    }
}