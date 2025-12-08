package ui;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import ui.EscapeSequences.*;

import static ui.EscapeSequences.*;


public class DrawChessBoard {
    private final String color;

    public DrawChessBoard(String color) {
        this.color = color;
    }

    public void printChessBoard(ChessBoard board) {
        var board1 = board.getBoard();
        boolean whiteSide = color.equalsIgnoreCase("white");
        if (!whiteSide) {
            board1 = flipBoard(board1);
        }
        StringBuilder colorBoard = new StringBuilder();
        colorBoard.append("\n");
        colorBoard.append(SET_BG_COLOR_LIGHT_GREY);
        boolean white = false;
        if (whiteSide) {
            colorBoard.append("   " + " a " + "  b " + " c " + "  d " + "  e " + " f  " + " g " + "  h " + "\n");
        } else {
            colorBoard.append("   " + " h " + " g " + "  f " + "  e " + " d " + "  c " + " b  " + " a " + "\n");
        }
        for (int row = 7; row >= 0; row--) {
            int rank = whiteSide ? row + 1 : 8 - row;
            colorBoard.append(" ").append(rank).append(" ");
            for (int col = 0; col < board1.length; col++) {
                ChessPiece piece = board1[row][col];
                boolean lightSquare = (row + col) % 2 == 0;
                colorBoard.append(lightSquare ? SET_BG_COLOR_DARK_GREEN : SET_BG_COLOR_LIGHT_GREY);
                colorBoard.append(symbolForChess(piece));
            }
            white = (!white);
            colorBoard.append(" ").append(rank).append("\n");
        }
        if (whiteSide) {
            colorBoard.append("   " + " a " + "  b " + " c " + "  d " + "  e " + " f  " + " g " + "  h " + "\n");
        } else {
            colorBoard.append("   " + " h " + " g " + "  f " + "  e " + " d " + "  c " + " b  " + " a " + "\n");
        }
        System.out.println(colorBoard.toString());
    }

    private ChessPiece[][] flipBoard(ChessPiece[][] original) {
        int size = original.length;
        ChessPiece[][] flipped = new ChessPiece[size][size];
        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                flipped[row][col] = original[size - 1 - row][size - 1 - col];
            }
        }
        return flipped;
    }

    private String symbolForChess(ChessPiece piece) {
        if (piece == null) {
            return EMPTY;
        }

        boolean isWhite = piece.getTeamColor() == ChessGame.TeamColor.WHITE;
        return switch (piece.getPieceType()) {
            case BISHOP -> isWhite ? WHITE_BISHOP : BLACK_BISHOP;
            case PAWN -> isWhite ? WHITE_PAWN : BLACK_PAWN;
            case KING -> isWhite ? WHITE_KING : BLACK_KING;
            case QUEEN -> isWhite ? WHITE_QUEEN : BLACK_QUEEN;
            case ROOK -> isWhite ? WHITE_ROOK : BLACK_ROOK;
            case KNIGHT -> isWhite ? WHITE_KNIGHT : BLACK_KNIGHT;
        };

    }
}
