package pawn;

import chess.*;
import org.junit.jupiter.api.Test;

import java.util.HashSet;

import static passoff.chess.TestUtilities.loadBoard;
import static passoff.chess.TestUtilities.validateMoves;

public class PawnTest {

    @Test
    public void pawnPromotion() throws InvalidMoveException {
        ChessGame game=new ChessGame();

        game.makeMove(new ChessMove(new ChessPosition(2, 3), new ChessPosition(4, 3), null));
        game.makeMove(new ChessMove(new ChessPosition(7, 4), new ChessPosition(5, 4), null));
        game.makeMove(new ChessMove(new ChessPosition(4, 3), new ChessPosition(5, 4), null));
        game.makeMove(new ChessMove(new ChessPosition(7, 3), new ChessPosition(6, 3), null));
        game.makeMove(new ChessMove(new ChessPosition(5, 4), new ChessPosition(6, 3), null));
        game.makeMove(new ChessMove(new ChessPosition(7, 1), new ChessPosition(6, 1), null));
        game.makeMove(new ChessMove(new ChessPosition(6, 3), new ChessPosition(7, 3), null));
        game.makeMove(new ChessMove(new ChessPosition(6, 1), new ChessPosition(5, 1), null));
        assert !game.isInCheck(ChessGame.TeamColor.BLACK);
        game.makeMove(new ChessMove(new ChessPosition(7, 3), new ChessPosition(8, 2), ChessPiece.PieceType.QUEEN));
        assert !game.isInCheck(ChessGame.TeamColor.BLACK);

    }

}