package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {

    private final ChessGame.TeamColor pieceColor;
    private final PieceType type;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.pieceColor=pieceColor;
        this.type=type;
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
        throw new RuntimeException("Not implemented");
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        throw new RuntimeException("Not implemented");
    }


    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        if (type == PieceType.BISHOP) {
            List<ChessMove> moves = new ArrayList<ChessMove>();
            //moves up to the right
            int rowIter = myPosition.getRow() + 1;
            int colIter = myPosition.getColumn() + 1;
            for (rowIter = myPosition.getRow() + 1; rowIter <= 8; rowIter++) {
                ChessPosition checkPosition=new ChessPosition(rowIter, colIter);
                if (board.getPiece(checkPosition) == null) {
                    ChessMove newMove=new ChessMove(myPosition, checkPosition, null);
                    moves.add(newMove);
                } else break;
                colIter++;
                if (colIter > 8) break;
            }
            //moves down to the left
            colIter = myPosition.getColumn() - 1;
            for (rowIter = myPosition.getRow() - 1; rowIter > 0; rowIter--) {
                ChessPosition checkPosition=new ChessPosition(rowIter, colIter);
                if (board.getPiece(checkPosition) == null) {
                    ChessMove newMove=new ChessMove(myPosition, checkPosition, null);
                    moves.add(newMove);
                } else break;
                colIter--;
                if (colIter <= 0) break;
            }
            //moves up to the left
            colIter = myPosition.getColumn() - 1;
            for (rowIter = myPosition.getRow() + 1; rowIter <= 8; rowIter++) {
                ChessPosition checkPosition=new ChessPosition(rowIter, colIter);
                if (board.getPiece(checkPosition) == null) {
                    ChessMove newMove=new ChessMove(myPosition, checkPosition, null);
                    moves.add(newMove);
                } else break;
                colIter--;
                if (colIter <= 0) break;
            }
            //moves down to the right
            colIter = myPosition.getColumn() + 1;
            for (rowIter = myPosition.getRow() - 1; rowIter > 0; rowIter--) {
                ChessPosition checkPosition=new ChessPosition(rowIter, colIter);
                if (board.getPiece(checkPosition) == null) {
                    ChessMove newMove=new ChessMove(myPosition, checkPosition, null);
                    moves.add(newMove);
                } else break;
                colIter++;
                if (colIter > 8) break;
            }
            return moves;
            }
        else {
            throw new RuntimeException("Not implemented");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChessPiece that=(ChessPiece) o;
        return pieceColor == that.pieceColor && type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(pieceColor, type);
    }
}
