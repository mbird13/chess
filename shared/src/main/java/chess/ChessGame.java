package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {

    ChessBoard board = new ChessBoard();
    TeamColor teamTurn;

    public ChessGame() {
        board.resetBoard();
        teamTurn = TeamColor.WHITE;
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return teamTurn;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        teamTurn = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        ChessPiece myPiece = board.getPiece(startPosition);
        List<ChessMove> moves = new ArrayList<>();
        if (myPiece == null) {return moves;}

        moves.addAll(myPiece.pieceMoves(board, startPosition));
        List<ChessMove> invalidMoves = new ArrayList<>();
        for (ChessMove move : moves) {
            ChessPiece capturedPiece = board.getPiece(move.getEndPosition());
            board.makeMove(move);
            if (isInCheck(myPiece.getTeamColor())) {
                invalidMoves.add(move);
            }
            board.makeMove(new ChessMove(move.getEndPosition(), move.getStartPosition(), null));
            if (capturedPiece != null) {
                board.addPiece(move.getEndPosition(), capturedPiece);
            }
        }
        moves.removeAll(invalidMoves);
        return moves;
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to preform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        ChessPosition kingPosition = board.findPiece(teamColor, ChessPiece.PieceType.KING);
        List<ChessPosition> enemyPositions=board.getPositions(TeamColor.BLACK);
        if (teamColor == TeamColor.BLACK) {
            enemyPositions = board.getPositions(TeamColor.WHITE);
        }

        for (ChessPosition enemyPosition : enemyPositions) {
            Collection<ChessMove> enemyMoves = board.getPiece(enemyPosition).pieceMoves(board, enemyPosition);

            for (ChessMove enemyMove : enemyMoves) {
                if (enemyMove.getEndPosition().equals(kingPosition)) {
                    return true;
                }
            }
        }
        return false;
    }



    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        return teamColor == teamTurn && isInCheck(teamColor) && noValidMoves(teamColor);
    }

    private boolean noValidMoves(TeamColor teamColor) {
        List<ChessPosition> myPositions = board.getPositions(teamColor);
        List<ChessMove> validMoves = new ArrayList<>();
        for (ChessPosition position : myPositions) {
            validMoves.addAll(validMoves(position));
        }
        return validMoves.isEmpty();
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        if (isInCheck(teamColor) || teamColor != teamTurn) {return false;}
        return noValidMoves(teamColor);

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
}
