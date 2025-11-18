import chess.*;
import ui.ChessClient;

public class ClientMain {
    public static void main(String[] args) {
        var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        System.out.println("♕ 240 Chess Client: " + piece);
        if (args.length == 2) {
            new ChessClient("http://localhost:" + args[1]);
        } else {
            new ChessClient("http://localhost:8080").run();
        }
    }
}