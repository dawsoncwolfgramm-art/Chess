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
                colorBoard.append(lightSquare ? SET_BG_COLOR_LIGHT_GREY : SET_BG_COLOR_DARK_GREEN);
                String piece1 = EMPTY;
                if (piece != null) {
                    if (piece.getTeamColor() == ChessGame.TeamColor.WHITE) {
                        switch (piece.getPieceType()) {
                            case BISHOP: {
                                piece1 = WHITE_BISHOP;
                                break;
                            }
                            case PAWN: {
                                piece1 = WHITE_PAWN;
                                break;
                            }
                            case KING: {
                                piece1 = WHITE_KING;
                                break;
                            }
                            case QUEEN: {
                                piece1 = WHITE_QUEEN;
                                break;
                            }
                            case ROOK: {
                                piece1 = WHITE_ROOK;
                                break;
                            }
                            case KNIGHT: {
                                piece1 = WHITE_KNIGHT;
                            }
                        }
                        colorBoard.append(piece1);
                    }
                    if (piece.getTeamColor() == ChessGame.TeamColor.BLACK) {
                        switch (piece.getPieceType()) {
                            case BISHOP: {
                                piece1 = BLACK_BISHOP;
                                break;
                            }
                            case PAWN: {
                                piece1 = BLACK_PAWN;
                                break;
                            }
                            case KING: {
                                piece1 = BLACK_KING;
                                break;
                            }
                            case QUEEN: {
                                piece1 = BLACK_QUEEN;
                                break;
                            }
                            case ROOK: {
                                piece1 = BLACK_ROOK;
                                break;
                            }
                            case KNIGHT: {
                                piece1 = BLACK_KNIGHT;
                                break;
                            }
                        }
                        colorBoard.append(piece1);
                    }
                } else {
                    piece1 = EMPTY;
                    colorBoard.append(piece1);
                }

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
}
