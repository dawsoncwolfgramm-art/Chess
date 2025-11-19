import chess.*;
import ui.ChessClient;
import ui.DrawChessBoard;

public class ClientMain {
    public static void main(String[] args) {
        var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        ClientMain clientMain = new ClientMain();
        clientMain.practiceDrawBoard();
        System.out.println("♕ 240 Chess Client: " + piece);
//        if (args.length == 2) {
//            new ChessClient("http://localhost:" + args[1]);
//        } else {
//            new ChessClient("http://localhost:8080").run();
//        }

    }

    public void practiceDrawBoard() {
        DrawChessBoard drawBoard = new DrawChessBoard("white");
        ChessBoard board = new ChessBoard();
        board.resetBoard();
        drawBoard.printChessBoard(board);
    }
}