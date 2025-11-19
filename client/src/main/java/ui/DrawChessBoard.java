package ui;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;

import java.util.ArrayList;
import java.util.List;

import ui.EscapeSequences.*;

import static ui.EscapeSequences.*;


public class DrawChessBoard {

    private final String color;

    public DrawChessBoard(String color) {
        this.color = color;
    }


    public void printChessBoard(ChessBoard board) {
        var board1 = board.getBoard();
        String[] numbersWhite = {" 1 ", " 2 ", " 3 ", " 4 ", " 5 ", " 6 ", " 7 ", " 8 "};
        String[] numbersBlack = {" 8 ", " 7 ", " 6 ", " 5 ", " 4 ", " 3 ", " 2 ", " 1 "};
        boolean chessSide;
        if (color.equalsIgnoreCase("white")) {
            chessSide = true;
        } else {
            chessSide = false;
        }

        if (!chessSide) {
            board1 = flipBoard(board1);
        }
        boolean white = false;
        StringBuilder colorBoard = new StringBuilder();
        if (chessSide) {
            colorBoard.append("   " + " a " + "  b " + " c " + "  d " + "  e " + " f  " + " g " + "  h " + "\n");
        } else {
            colorBoard.append("   " + " h " + " g " + "  f " + "  e " + " d " + "  c " + " b  " + " a " + "\n");
        }
        for (int row = 7; row >= 0; row--) {
            if (chessSide) {
                colorBoard.append(numbersWhite[row]);
            } else {
                colorBoard.append(numbersBlack[row]);
            }
            for (int col = 0; col < board1.length; col++) {
                ChessPiece piece = board1[row][col];
                white = (!white);
                if (white) {
                    colorBoard.append(SET_BG_COLOR_LIGHT_GREY);
                } else {
                    colorBoard.append(SET_BG_COLOR_BLACK);
                }
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
            if (chessSide) {
                colorBoard.append(numbersBlack[row]);
            } else {
                colorBoard.append(numbersBlack[row]);
            }
            colorBoard.append("\n");
        }
        if (chessSide) {
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
