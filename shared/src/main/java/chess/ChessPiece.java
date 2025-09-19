package chess;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {

    private final ChessGame.TeamColor pieceColor;
    private final ChessPiece.PieceType type;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.pieceColor = pieceColor;
        this.type = type;
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return pieceColor;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return type;
    }


    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        return switch (getPieceType()) {
            case BISHOP -> movesFrom(board, myPosition, true, new int[][]{{1, 1}, {1, -1}, {-1, 1}, {-1, -1}});
            case ROOK -> movesFrom(board, myPosition, true, new int[][]{{0, 1}, {0, -1}, {-1, 0}, {1, 0}});
            case QUEEN -> movesFrom(board, myPosition, true, new int[][]{{1, 1}, {1, -1}, {-1, 1}, {-1, -1}, {0, 1}, {0, -1}, {-1, 0}, {1, 0}});
            case KING -> movesFrom(board, myPosition, false, new int[][]{{1, 1}, {1, -1}, {-1, 1}, {-1, -1}, {0, 1}, {0, -1}, {-1, 0}, {1, 0}});
            case KNIGHT -> movesFrom(board, myPosition, false, new int[][]{{2, 1}, {1, 2}, {-1, 2}, {-2, 1}, {2, -1}, {1, -2}, {-1, -2}, {-2, -1}});
            case PAWN -> movesPawn(board, myPosition);
//            default:
//                new HashSet<>();
        };

    }

    @Override
    public String toString() {
        return "ChessPiece{" +
                "pieceColor=" + pieceColor +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessPiece that = (ChessPiece) o;
        return pieceColor == that.pieceColor;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(pieceColor);
    }

    private Collection<ChessMove> movesFrom(ChessBoard board, ChessPosition from, boolean slide, int[][] directions) {
        Collection<ChessMove> moves = new HashSet<>();
        int myRow = from.getRow();
        int myCol = from.getColumn();

        for (int[] d : directions) {
            int row = myRow + d[0];
            int col = myCol + d[1];

            while (inBounds(row, col)) {
                ChessPosition to = new ChessPosition(row, col);
                ChessPiece occupied = board.getPiece(to);

                if (occupied == null) {
                    moves.add(new ChessMove(from, to, null));
                }

                else {
                    if (occupied.getTeamColor() != getTeamColor()) {
                        moves.add(new ChessMove(from, to, null));
                    }
                    break;
                }

                if (slide) {
                    row = row + d[0];
                    col = col + d[1];

                }else {
                    break;
                }
            }
        }
        return moves;
    }

    private Collection<ChessMove> movesPawn(ChessBoard board, ChessPosition from) {
        Collection<ChessMove> moves = new HashSet<>();
        int direction = (getTeamColor() == ChessGame.TeamColor.WHITE) ? 1 : -1;
        int myRow = from.getRow();
        int myCol = from.getColumn();


        ChessPosition moveOne = new ChessPosition(myRow + direction, myCol); // needs movement on both sides.
        if (inBounds(moveOne) && board.getPiece(moveOne) == null) {
            addMovePromote(moves, from, moveOne);
        }

        if (myRow == 2) {  //this is at the start
            for (int i = 1; i <= 2; i++) {
                int row = myRow + i;
                int col = myCol + 0;
                while (inBounds(row, col)) { // copy from top
                    ChessPosition to = new ChessPosition(row, col);
                    ChessPosition pawnKillRight = new ChessPosition(myRow + 1, myCol + 1);
                    ChessPosition pawnKillLeft = new ChessPosition(myRow + 1, myCol - 1);
                    ChessPiece occupied = board.getPiece(to);
                    ChessPiece enemey1 = board.getPiece(pawnKillRight);
                    ChessPiece enemey2 = board.getPiece(pawnKillLeft);


                    if (occupied == null) {
                        moves.add(new ChessMove(from, to, null));
                    } else {
                        if (enemey1.getTeamColor() != getTeamColor()) {
                            moves.add(new ChessMove(from, pawnKillLeft, null));
                        } else if (enemey2.getTeamColor() != getTeamColor()) {
                            moves.add(new ChessMove(from, pawnKillright, null));
                        }
                    }

                    else{
                        if (occupied.getTeamColor() != getTeamColor()) {
                            moves.add(new ChessMove(from, to, null));
                        }
                        break;
                    }
                }
            }
        }
        return moves;
    }


    private boolean arrivePromote (int row) {
        return row = 8;
    }

    private void addMovePromote(Collection<ChessMove> moves, ChessPosition from, ChessPosition to) {
        int arrivePromote;
        if (getTeamColor() == ChessGame.TeamColor.WHITE) {
            arrivePromote = 8;
        }
        else {
            arrivePromote = 1;
        }
        if (to.getRow() == arrivePromote) {
            moves.add(new ChessMove(from, to, ChessPiece.PieceType.QUEEN));
        }

    }

    private boolean inBounds (int row, int col) {
        return row >= 1 && row <= 8 && col >= 1 && col <= 8;
    }

    private boolean inBounds (ChessPosition position) {
        return inBounds(position.getRow(), position.getColumn());
    }


}
