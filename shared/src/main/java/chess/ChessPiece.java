package chess;

import java.util.Collection;
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

    public boolean getEnPassantStatus() {
        return enPassantStatus;
    }

    public void setEnPassantStatus(boolean enPassantStatus) {
        this.enPassantStatus=enPassantStatus;
    }

    private boolean enPassantStatus= false;

    public boolean isAlreadyMoved() {
        return alreadyMoved;
    }

    public void setAlreadyMoved(boolean alreadyMoved) {
        this.alreadyMoved=alreadyMoved;
    }

    private boolean alreadyMoved = false;

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
        if (type == PieceType.BISHOP) {
            BishopMoveCalculator moveCalculator = new BishopMoveCalculator();
            return moveCalculator.validMoves(board, myPosition, pieceColor);
            }
        else if (type == PieceType.QUEEN){
            QueenMoveCalculator moveCalculator = new QueenMoveCalculator();
            return moveCalculator.validMoves(board, myPosition, pieceColor);
        }
        else if (type == PieceType.KING){
            KingMoveCalculator moveCalculator = new KingMoveCalculator();
            return moveCalculator.validMoves(board, myPosition, pieceColor);
        }
        else if (type == PieceType.PAWN){
            PawnMoveCalculator moveCalculator = new PawnMoveCalculator();
            return moveCalculator.validMoves(board, myPosition, pieceColor);
        }
        else if (type == PieceType.KNIGHT){
            KnightMoveCalculator moveCalculator = new KnightMoveCalculator();
            return moveCalculator.validMoves(board, myPosition, pieceColor);
        }
        else if (type == PieceType.ROOK){
            RookMoveCalculator moveCalculator = new RookMoveCalculator();
            return moveCalculator.validMoves(board, myPosition, pieceColor);
        }
        throw new RuntimeException("Invalid piece type");
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

    @Override
    public String toString() {
        StringBuilder r = new StringBuilder();
        if (pieceColor == ChessGame.TeamColor.WHITE) {
            r.append("w");
        }
        if (type == PieceType.PAWN) {
            r.append("p");
        }
        else if (type == PieceType.ROOK) {
            r.append("r");
        }
        else if (type == PieceType.QUEEN) {
            r.append("q");

        }
        else if (type == PieceType.KNIGHT) {
            r.append("n");
        }
        else if (type == PieceType.KING) {
            r.append("k");
        }
        else if (type == PieceType.BISHOP) {
            r.append("b");
        }
        else return "not valid piece type";
        return r.toString();
    }
}
