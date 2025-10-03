package chess;

import java.util.*;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {
    private ChessBoard board;
    private TeamColor currentTurn;

    public ChessGame() {
        this.board = new ChessBoard();
        this.board.resetBoard();
        this.currentTurn = TeamColor.WHITE;
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return currentTurn;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        this.currentTurn = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK,


    }



    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        ChessPiece piece = board.getPiece(startPosition);
        if (piece == null) {
            return null;
        }
        Collection<ChessMove> possibleMoves = piece.pieceMoves(board, startPosition);
        Collection<ChessMove> legalMoves = new HashSet<>();

        for (ChessMove move : possibleMoves) {
            if (kingSafe(move, piece.getTeamColor())) {
                legalMoves.add(move);
            }
        }
        // if king isnt in check mate add it to legal moves
        return legalMoves;
    }

    private boolean kingSafe(ChessMove move, TeamColor myColor) {
        ChessPosition from = move.getStartPosition();
        ChessPosition to = move.getEndPosition();
        ChessPiece moveFrom = board.getPiece(from);
        ChessPiece enemy = board.getPiece(to);

        board.addPiece(from, null);
        if (move.getPromotionPiece() != null) {
            board.addPiece(to, new ChessPiece(moveFrom.getTeamColor(), move.getPromotionPiece()));
        }
        else {
            board.addPiece(to, moveFrom);
        }

        boolean inCheck = isInCheck(myColor);

        board.addPiece(to, enemy);
        board.addPiece(from, moveFrom);

        return !inCheck;
    }
    /**
     * Makes a move in a chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        ChessPosition start = move.getStartPosition();
        ChessPosition end = move.getEndPosition();
        ChessPiece piece = board.getPiece(start);

        if (piece == null) {
            throw new InvalidMoveException("No piece at start");
        }

        if (piece.getTeamColor() != currentTurn) {
            throw new InvalidMoveException("Not your turn");
        }

        if (isInCheck(piece.getTeamColor())) {

        }

        // start exceipton if its null and on the right piece color so they aren't moving pieces that aren't their own.
        // if there is a piece that is empty do no moves.
        // added a piece at the end position and set the start position to null
        // if i have a king move to end position and the start position is null.
        // its not the teams turn if you pick on a piece that isn't theirs and make a move
        // if you deal with a promotion of a pawn promote then switch turns after each move.
    }

    /**
     * Determines if the given team is in check
     *
     * @param myTeam which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor myTeam) {
        ChessPosition kingPos = findKing(myTeam);
        ChessPiece king = board.getPiece(kingPos);
        if (king == null) return false;

        TeamColor enemyTeam = (myTeam == TeamColor.WHITE) ? TeamColor.BLACK : TeamColor.WHITE;
        for (int x = 1; x <= 8; x++) {
            for (int y = 1; y <= 8; y++) {
                ChessPosition pos = new ChessPosition(x, y);
                ChessPiece piece = board.getPiece(pos);

                if (piece == null || piece.getTeamColor() != enemyTeam) continue;

                for (ChessMove attack : piece.pieceMoves(board, pos)) {
                    ChessPosition end = attack.getEndPosition();
                    if (end.getRow() == kingPos.getRow() && end.getColumn() == kingPos.getColumn()) {
                        return true;
                    }

                }
            }
        }
        return false;
        //checking every piece to see if it attacks the king
        //know where the king is and is there another piece attacking the king say true
    }

    private ChessPosition findKing(TeamColor myTeam) {
        for (int x = 1; x <= 8; x++) {
            for (int y = 1; y <= 8; y++) {
                ChessPosition pos = new ChessPosition(x, y);
                ChessPiece piece = board.getPiece(pos);
                if (piece != null && piece.getTeamColor() == myTeam && piece.getPieceType() == ChessPiece.PieceType.KING) {
                    return pos;
                }
            }
        }
        return null;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param myTeam which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor myTeam) {
        ChessPosition kingPos = findKing(myTeam);
        ChessPiece king = board.getPiece(kingPos);
        if (king == null) return false;
        TeamColor enemyTeam = (myTeam == TeamColor.WHITE) ? TeamColor.BLACK : TeamColor.WHITE;

        if (isInCheck(myTeam)) {
            for (int x = 1; x <= 8; x++) {
                for (int y = 1; y <= 8; y++) {
                    ChessPosition pos = new ChessPosition(x, y);
                    ChessPiece piece = board.getPiece(pos);

                    if (piece == null || piece.getTeamColor() != enemyTeam) continue;

                    for (ChessMove kingMove : king.pieceMoves(board, kingPos)) {
                        for (ChessMove attack : piece.pieceMoves(board, pos)) {
                            ChessPosition end = attack.getEndPosition();
                            ChessPosition kingMoveEnd = kingMove.getEndPosition();
                            if (end.getRow() == kingMoveEnd.getRow() && end.getColumn() == kingMoveEnd.getColumn()) {
                                return true;
                            }
                        }
                    }
                }
            }
        }
        return false;
        // if king is not in check return false
        // if king is in check can he move? can king escape or king can kill them. valid moves from others cannot ==
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves while not in check.
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
        // check if king is check return false
        // not being attacked but cant move anywhere.
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.board = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return board;
    }

    @Override
    public String toString() {
        return "ChessGame{" +
                "board=" + board +
                ", turn=" + currentTurn +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessGame chessGame = (ChessGame) o;
        return Objects.equals(board, chessGame.board) && currentTurn == chessGame.currentTurn;
    }

    @Override
    public int hashCode() {
        return Objects.hash(board, currentTurn);
    }
}
