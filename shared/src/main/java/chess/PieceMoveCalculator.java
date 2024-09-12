package chess;

import java.util.List;
import java.util.ArrayList;

abstract class PieceMoveCalculator {
  public abstract List<ChessMove> validMoves(ChessBoard board, ChessPosition myPosition, ChessGame.TeamColor myColor);

  protected List<ChessPosition> exploreDirection(ChessBoard board, ChessPosition myPosition, ChessGame.TeamColor myColor, int rowDir, int colDir) {
    ChessPosition newPosition = new ChessPosition(myPosition.getRow() + rowDir, myPosition.getColumn() + colDir);
    var moves = new ArrayList<ChessPosition>();
    if (isInBounds(newPosition.getRow(), newPosition.getColumn())) {
      if (board.getPiece(newPosition) == null) {
        moves.add(newPosition);
        moves.addAll(exploreDirection(board, newPosition, myColor, rowDir, colDir));
      }
      else if (board.getPiece(newPosition).getTeamColor() != myColor) {
        moves.add(newPosition);
      }
    }

    return moves;
  }

  protected boolean isInBounds(int row, int col) {
    return row <= 8 && row >= 1 && col <= 8 && col >= 1;
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
    List<ChessMove> moves = new ArrayList<>();
    moves.addAll(bishopMoves.validMoves(board, myPosition, myColor));
    moves.addAll(rookMoves.validMoves(board, myPosition, myColor));
    return moves;
  }
}

class KnightMoveCalculator extends PieceMoveCalculator {
  public List<ChessMove> validMoves(ChessBoard board, ChessPosition myPosition, ChessGame.TeamColor myColor) {
    List<ChessMove> moves = new ArrayList<ChessMove>();

    return moves;
  }
}

class KingMoveCalculator extends PieceMoveCalculator {
  public List<ChessMove> validMoves(ChessBoard board, ChessPosition myPosition, ChessGame.TeamColor myColor) {
    List<ChessMove> moves = new ArrayList<ChessMove>();

    return moves;
  }
}

class PawnMoveCalculator extends PieceMoveCalculator {
  public List<ChessMove> validMoves(ChessBoard board, ChessPosition myPosition, ChessGame.TeamColor myColor) {
    List<ChessPosition> validPositions = new ArrayList<ChessPosition>();
    List<ChessMove> moves = new ArrayList<ChessMove>();
    if (myColor == ChessGame.TeamColor.WHITE) {
      //white moves upward
      if (myPosition.getRow() < 8) {
        boolean isBlocked=false;
        ChessPosition front=new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn());
        if (board.getPiece(front) == null) {
          validPositions.add(front);
        } else isBlocked=true;
        if (myPosition.getRow() == 2 && !isBlocked) {
          ChessPosition checkPosition=new ChessPosition(myPosition.getRow() + 2, myPosition.getColumn());
          if (board.getPiece(checkPosition) == null) {
            validPositions.add(checkPosition);
          }
        }
        if (myPosition.getColumn() < 8) {
          ChessPosition rightDiagonal=new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn() + 1);
          if (board.getPiece(rightDiagonal) != null && board.getPiece(rightDiagonal).getTeamColor() != myColor) {
            validPositions.add(rightDiagonal);
          }
        }
        if (myPosition.getColumn() > 1) {
          ChessPosition leftDiagonal=new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn() - 1);
          if (board.getPiece(leftDiagonal) != null && board.getPiece(leftDiagonal).getTeamColor() != myColor) {
            validPositions.add(leftDiagonal);
          }
        }
      }
      if (myPosition.getRow() + 1 == 8) {
        for (ChessPosition endPostion : validPositions) {
          moves.add(new ChessMove(myPosition, endPostion, ChessPiece.PieceType.BISHOP));
          moves.add(new ChessMove(myPosition, endPostion, ChessPiece.PieceType.QUEEN));
          moves.add(new ChessMove(myPosition, endPostion, ChessPiece.PieceType.ROOK));
          moves.add(new ChessMove(myPosition, endPostion, ChessPiece.PieceType.KNIGHT));
        }
      }
      else {
        for (ChessPosition endPostion : validPositions) {
          moves.add(new ChessMove(myPosition, endPostion, null));
        }
      }
    }
    if (myColor == ChessGame.TeamColor.BLACK) {
      //black moves downward
      if (myPosition.getRow() > 1) {
        boolean isBlocked=false;
        ChessPosition front = new ChessPosition(myPosition.getRow() - 1, myPosition.getColumn());
        if (board.getPiece(front) == null) {
          validPositions.add(front);
        } else isBlocked=true;
        if (myPosition.getRow() == 7 && !isBlocked) {
          ChessPosition checkPosition=new ChessPosition(myPosition.getRow() - 2, myPosition.getColumn());
          if (board.getPiece(checkPosition) == null) {
            validPositions.add(checkPosition);
          }
        }
        if (myPosition.getColumn() < 8) {
          ChessPosition rightDiagonal=new ChessPosition(myPosition.getRow() - 1, myPosition.getColumn() + 1);
          if (board.getPiece(rightDiagonal) != null && board.getPiece(rightDiagonal).getTeamColor() != myColor) {
            validPositions.add(rightDiagonal);
          }
        }
        if (myPosition.getColumn() > 1) {
          ChessPosition leftDiagonal=new ChessPosition(myPosition.getRow() - 1, myPosition.getColumn() - 1);
          if (board.getPiece(leftDiagonal) != null && board.getPiece(leftDiagonal).getTeamColor() != myColor) {
            validPositions.add(leftDiagonal);
          }
        }
      }
      if (myPosition.getRow() - 1 == 1) {
        for (ChessPosition endPostion : validPositions) {
          moves.add(new ChessMove(myPosition, endPostion, ChessPiece.PieceType.BISHOP));
          moves.add(new ChessMove(myPosition, endPostion, ChessPiece.PieceType.QUEEN));
          moves.add(new ChessMove(myPosition, endPostion, ChessPiece.PieceType.ROOK));
          moves.add(new ChessMove(myPosition, endPostion, ChessPiece.PieceType.KNIGHT));
        }
      }
      else {
        for (ChessPosition endPostion : validPositions) {
          moves.add(new ChessMove(myPosition, endPostion, null));
        }
      }
    }

    return moves;
  }
}
