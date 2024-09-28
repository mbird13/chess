package chess;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard {

    private ChessPiece[][] squares = new ChessPiece[8][8];

    public ChessBoard() {
        
    }

    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {
        squares[position.getRow() - 1][position.getColumn() - 1] = piece;
    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {
        return squares[position.getRow() - 1][position.getColumn() - 1];
    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {
        for (ChessPiece[] row : squares) {
            for (ChessPiece square: row) {
                square = null;
            }
        }
        ChessPiece.PieceType[] pieceOrder = {
                ChessPiece.PieceType.ROOK, ChessPiece.PieceType.KNIGHT, ChessPiece.PieceType.BISHOP,
                ChessPiece.PieceType.QUEEN, ChessPiece.PieceType.KING, ChessPiece.PieceType.BISHOP,
                ChessPiece.PieceType.KNIGHT, ChessPiece.PieceType.ROOK
        };

        for (int i = 1; i <= 8; i++) {
            addPiece(new ChessPosition(2, i), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN));
            addPiece(new ChessPosition(7, i), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN));
            addPiece(new ChessPosition(1, i), new ChessPiece(ChessGame.TeamColor.WHITE, pieceOrder[i-1]));
            addPiece(new ChessPosition(8, i), new ChessPiece(ChessGame.TeamColor.BLACK, pieceOrder[i-1]));
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChessBoard that=(ChessBoard) o;
        return Arrays.deepEquals(squares, that.squares);
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(squares);
    }

    @Override
    public String toString() {
        StringBuilder r =new StringBuilder();
        for (ChessPiece[] rows : squares) {
            for (ChessPiece square : rows) {
                r.append("|");
                if (square != null) {
                    r.append(square.toString());
                }
                else r.append(" ");
            }
        }
        return r.toString();
    }

    public List<ChessPosition> getPositions(ChessGame.TeamColor teamColor) {
        List<ChessPosition> positions = new ArrayList<>();
        for (int row = 1; row <=8; row++) {
            for (int col = 1; col <=8; col++) {
                ChessPosition position = new ChessPosition(row,col);
                if (getPiece(position) != null && getPiece(position).getTeamColor() == teamColor) {
                    positions.add(position);
                }
            }
        }
        return positions;
    }

    public ChessPosition findPiece(ChessGame.TeamColor teamColor, ChessPiece.PieceType pieceType) {
        for (int row = 1; row <=8; row++) {
            for (int col = 1; col <=8; col++) {
                var checkPosition = new ChessPosition(row,col);
                if (getPiece(checkPosition) != null && getPiece(checkPosition).equals(new ChessPiece(teamColor, pieceType))) {
                    return checkPosition;
                }
            }
        }
        return null;
    }

    public void makeMove(ChessMove move) {
        ChessPiece piece = getPiece(move.getStartPosition());
        squares[move.getStartPosition().getRow() - 1][move.getStartPosition().getColumn() - 1] = null;
        squares[move.getEndPosition().getRow() - 1][move.getEndPosition().getColumn() - 1] = piece;
    }
}
