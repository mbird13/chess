package chess;

import java.util.List;
import java.util.ArrayList;

abstract class PieceMoveCalculator {
  public abstract List<ChessMove> validMoves(ChessBoard board, ChessPosition myPosition, ChessGame.TeamColor myColor);

  protected List<ChessPosition> exploreDirection(ChessBoard board, ChessPosition myPosition, ChessGame.TeamColor myColor, int rowDir, int colDir) {
    ChessPosition newPosition = new ChessPosition(myPosition.getRow() + rowDir, myPosition.getColumn() + colDir);
    var moves = new ArrayList<ChessPosition>();
    if (isValidMoveNoCapture(board, newPosition)) {
      moves.add(newPosition);
      moves.addAll(exploreDirection(board, newPosition, myColor, rowDir, colDir));
    }
    else if(isValidCapture(board, newPosition, myColor)) {
      moves.add(newPosition);
    }

    return moves;
  }

  protected boolean isInBounds(int row, int col) {
    return row <= 8 && row >= 1 && col <= 8 && col >= 1;
  }

  protected boolean isValidCapture(ChessBoard board, ChessPosition position, ChessGame.TeamColor myColor) {
    if (isInBounds(position.getRow(), position.getColumn()) && board.getPiece(position) != null) {
      return board.getPiece(position).getTeamColor() != myColor;
    }
    return false;
  }

  protected boolean isValidMoveNoCapture(ChessBoard board, ChessPosition position) {
    if (isInBounds(position.getRow(), position.getColumn())) {
      return board.getPiece(position) == null;
    }
    return false;
  }
}

class BishopMoveCalculator extends PieceMoveCalculator {
  public List<ChessMove> validMoves(ChessBoard board, ChessPosition myPosition, ChessGame.TeamColor myColor) {
    List<ChessPosition> validPositions = new ArrayList<ChessPosition>();
    validPositions.addAll(exploreDirection(board, myPosition, myColor, 1, 1));
    validPositions.addAll(exploreDirection(board, myPosition, myColor, -1, 1));
    validPositions.addAll(exploreDirection(board, myPosition, myColor, -1, -1));
    validPositions.addAll(exploreDirection(board, myPosition, myColor, 1, -1));

    List<ChessMove> moves = new ArrayList<ChessMove>();
    for (var validPosition : validPositions) {
      moves.add(new ChessMove(myPosition, validPosition, null));
    }
    return moves;
  }
}

class RookMoveCalculator extends PieceMoveCalculator {
  public List<ChessMove> validMoves(ChessBoard board, ChessPosition myPosition, ChessGame.TeamColor myColor) {
    List<ChessPosition> validPositions = new ArrayList<ChessPosition>();

    validPositions.addAll(exploreDirection(board, myPosition, myColor, 0, 1));
    validPositions.addAll(exploreDirection(board, myPosition, myColor, 1, 0));
    validPositions.addAll(exploreDirection(board, myPosition, myColor, 0, -1));
    validPositions.addAll(exploreDirection(board, myPosition, myColor, -1, 0));

    List<ChessMove> moves = new ArrayList<ChessMove>();
    for (var validPosition : validPositions) {
      moves.add(new ChessMove(myPosition, validPosition, null));
    }
    return moves;
  }
}

class QueenMoveCalculator extends PieceMoveCalculator {
  public List<ChessMove> validMoves(ChessBoard board, ChessPosition myPosition, ChessGame.TeamColor myColor) {
    BishopMoveCalculator bishopMoves = new BishopMoveCalculator();
    RookMoveCalculator rookMoves = new RookMoveCalculator();
    List<ChessMove> moves = bishopMoves.validMoves(board, myPosition, myColor);
    moves.addAll(rookMoves.validMoves(board, myPosition, myColor));
    return moves;
  }
}

class KnightMoveCalculator extends PieceMoveCalculator {
  public List<ChessMove> validMoves(ChessBoard board, ChessPosition myPosition, ChessGame.TeamColor myColor) {
    int[][] knightMoves = {
            {2, 1}, {2, -1}, {-2, 1}, {-2, -1},
            {1, 2}, {1, -2}, {-1, 2}, {-1, -2}
    };
    List<ChessMove> moves = new ArrayList<ChessMove>();
    for (int[] knightMove : knightMoves) {
      var newPosition = new ChessPosition(myPosition.getRow() + knightMove[0], myPosition.getColumn() + knightMove[1]);
      if (isValidMoveNoCapture(board, newPosition) || isValidCapture(board, newPosition, myColor)) {
        moves.add(new ChessMove(myPosition, newPosition, null));
      }
    }
    return moves;
  }
}

class KingMoveCalculator extends PieceMoveCalculator {
  public List<ChessMove> validMoves(ChessBoard board, ChessPosition myPosition, ChessGame.TeamColor myColor) {
    int[][] kingMoves = {
            {1,0}, {1,1}, {1,-1}, {0,1}, {0,-1}, {-1,-1}, {-1,0}, {-1,1}
    };
    List<ChessMove> moves = new ArrayList<ChessMove>();
    for (int[] kingMove : kingMoves) {
      var newPosition = new ChessPosition(myPosition.getRow() + kingMove[0], myPosition.getColumn() + kingMove[1]);
      if (isValidMoveNoCapture(board, newPosition) || isValidCapture(board, newPosition, myColor)) {
        moves.add(new ChessMove(myPosition, newPosition, null));
      }
    }
    return moves;
  }
}

class PawnMoveCalculator extends PieceMoveCalculator {
  public List<ChessMove> validMoves(ChessBoard board, ChessPosition myPosition, ChessGame.TeamColor myColor) {

    List<ChessPosition> validPositions = new ArrayList<ChessPosition>();
    List<ChessMove> moves = new ArrayList<ChessMove>();

    int direction = 1;
    int firstRow = 2;
    int lastRow = 8;
    if (myColor == ChessGame.TeamColor.BLACK) {
      direction = -1;
      firstRow = 7;
      lastRow = 1;
    }
    ChessPosition oneForward = new ChessPosition(myPosition.getRow() + direction, myPosition.getColumn());
    ChessPosition twoForward = new ChessPosition(myPosition.getRow() + 2 * direction, myPosition.getColumn());
    ChessPosition diagonalLeft = new ChessPosition(myPosition.getRow() + direction, myPosition.getColumn() - 1);
    ChessPosition diagonalRight = new ChessPosition(myPosition.getRow() + direction, myPosition.getColumn() + 1);
    ChessPosition left = new ChessPosition(myPosition.getRow(), myPosition.getColumn()-1);
    ChessPosition right = new ChessPosition(myPosition.getRow(), myPosition.getColumn()+1);

    if (isValidMoveNoCapture(board, oneForward)) {
      validPositions.add(oneForward);
      if (myPosition.getRow() == firstRow && isValidMoveNoCapture(board, twoForward)) {
        validPositions.add(twoForward);
      }
    }

    if (isValidCapture(board, diagonalLeft, myColor)) {
      validPositions.add(diagonalLeft);
    }
    if (isValidCapture(board, diagonalRight, myColor)) {
      validPositions.add(diagonalRight);
    }

    //En Passante
    if (isValidCapture(board, left, myColor) && board.getPiece(left).getEnPassantStatus()) {
      validPositions.add(diagonalLeft);
    }
    if (isValidCapture(board, right, myColor) && board.getPiece(right).getEnPassantStatus()) {
      validPositions.add(diagonalRight);
    }

    for (ChessPosition endPosition : validPositions) {
      if (endPosition.getRow() == lastRow) {
        moves.add(new ChessMove(myPosition, endPosition, ChessPiece.PieceType.BISHOP));
        moves.add(new ChessMove(myPosition, endPosition, ChessPiece.PieceType.QUEEN));
        moves.add(new ChessMove(myPosition, endPosition, ChessPiece.PieceType.ROOK));
        moves.add(new ChessMove(myPosition, endPosition, ChessPiece.PieceType.KNIGHT));
      }
      else {
        moves.add(new ChessMove(myPosition, endPosition, null));
      }
    }

    return moves;
  }
}
