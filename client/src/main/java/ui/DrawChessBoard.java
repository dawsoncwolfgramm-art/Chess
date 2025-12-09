package ui;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;
import ui.EscapeSequences.*;

import java.util.Set;

import static ui.EscapeSequences.*;


public class DrawChessBoard {
    private final String color;

    public DrawChessBoard(String color) {
        this.color = color;
    }

    public void printChessBoard(ChessBoard board) {
        printChessBoard(board, null);
    }

    public void printChessBoard(ChessBoard board, Set<ChessPosition> highlightSquares) {
        boolean whiteSide = color.equalsIgnoreCase("white");
        StringBuilder colorBoard = new StringBuilder();
        colorBoard.append("\n").append(SET_BG_COLOR_LIGHT_GREY);
        if (whiteSide) {
            colorBoard.append("   " + " a " + "  b " + " c " + "  d " + "  e " + " f  " + " g " + "  h " + "\n");
        } else {
            colorBoard.append("   " + " h " + " g " + "  f " + "  e " + " d " + "  c " + " b  " + " a " + "\n");
        }
        for (int row = 7; row >= 0; row--) {

            int rank = whiteSide ? row + 1 : 8 - row;
            colorBoard.append(" ").append(rank).append(" ");
            for (int col = 0; col < 8; col++) {

                int realRow, realCol;
                if (whiteSide) {
                    realRow = row + 1;
                    realCol = col + 1;
                } else {
                    realRow = 8 - row;
                    realCol = 8 - col;
                }

                ChessPosition realPos = new ChessPosition(realRow, realCol);
                ChessPiece piece = board.getPiece(realPos);

                boolean lightSquare = (realRow + realCol) % 2 == 0;
                boolean isHighlighted = highlightSquares != null && highlightSquares.contains(realPos);

                if (isHighlighted) {
                    colorBoard.append(SET_BG_COLOR_MAGENTA);
                } else {
                    colorBoard.append(lightSquare ? SET_BG_COLOR_DARK_GREEN : SET_BG_COLOR_LIGHT_GREY);
                }
                colorBoard.append(symbolForChess(piece));
            }
            colorBoard.append(" ").append(rank).append("\n");
        }
        colorBoard.append(SET_BG_COLOR_LIGHT_GREY);
        if (whiteSide) {
            colorBoard.append("   " + " a " + "  b " + " c " + "  d " + "  e " + " f  " + " g " + "  h " + "\n");
        } else {
            colorBoard.append("   " + " h " + " g " + "  f " + "  e " + " d " + "  c " + " b  " + " a " + "\n");
        }
        System.out.println(colorBoard);
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
