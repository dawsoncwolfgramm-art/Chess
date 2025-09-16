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
        switch (getPieceType()) {
            case BISHOP -> movesFrom(board, myPosition, true, new int[][]{{1, 1}, {1, -1}, {-1, 1}, {-1, -1}});
//            case ROOK ->
//            case QUEEN ->
//            case KING ->
//            case KNIGHT ->
//            case PAWN ->
        };



//        HashSet<ChessMove> moves = new HashSet<ChessMove>();
//        ChessPosition start = new ChessPosition(5, 4);
//        ChessPosition end = new ChessPosition(6, 5);
//        ChessMove move = new ChessMove(start, end, null);
//        moves.add(move);
//        return moves;
        return new HashSet<ChessMove>();
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

    private Collection<ChessMove> movesFrom(ChessBoard board, ChessPosition from, boolean slide, int[][] direction) {
        Collection<ChessMove> moves = new HashSet<>();
    }

}
