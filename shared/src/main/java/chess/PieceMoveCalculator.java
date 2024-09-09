package chess;

import java.util.List;
import java.util.ArrayList;

abstract class PieceMoveCalculator {
  public abstract List<ChessMove> validMoves(ChessBoard board, ChessPosition myPosition, ChessGame.TeamColor myColor);
}

class BishopMoveCalculator extends PieceMoveCalculator {
  public List<ChessMove> validMoves(ChessBoard board, ChessPosition myPosition, ChessGame.TeamColor myColor) {
    List<ChessMove> moves = new ArrayList<ChessMove>();
//moves up to the right
    int rowIter = myPosition.getRow() + 1;
    int colIter = myPosition.getColumn() + 1;
    for (rowIter = myPosition.getRow() + 1; rowIter <= 8; rowIter++) {
      if (colIter > 8) break;
      ChessPosition checkPosition=new ChessPosition(rowIter, colIter);
      if (board.getPiece(checkPosition) == null) {
        ChessMove newMove=new ChessMove(myPosition, checkPosition, null);
        moves.add(newMove);
      } else {
        //allow capture of opposing team
        if (board.getPiece(checkPosition).getTeamColor() != myColor) {
          ChessMove newMove=new ChessMove(myPosition, checkPosition, null);
          moves.add(newMove);
        }
          break;
      }
      colIter++;
    }
    //moves down to the left
    colIter = myPosition.getColumn() - 1;
    for (rowIter = myPosition.getRow() - 1; rowIter > 0; rowIter--) {
      if (colIter <= 0) break;
      ChessPosition checkPosition=new ChessPosition(rowIter, colIter);
      if (board.getPiece(checkPosition) == null) {
        ChessMove newMove=new ChessMove(myPosition, checkPosition, null);
        moves.add(newMove);
      } else {
        //allow capture of opposing team
        if (board.getPiece(checkPosition).getTeamColor() != myColor) {
          ChessMove newMove=new ChessMove(myPosition, checkPosition, null);
          moves.add(newMove);
        }
        break;
      }
      colIter--;
    }
    //moves up to the left
    colIter = myPosition.getColumn() - 1;
    for (rowIter = myPosition.getRow() + 1; rowIter <= 8; rowIter++) {
      if (colIter <= 0) break;
      ChessPosition checkPosition=new ChessPosition(rowIter, colIter);
      if (board.getPiece(checkPosition) == null) {
        ChessMove newMove=new ChessMove(myPosition, checkPosition, null);
        moves.add(newMove);
      } else {
        //allow capture of opposing team
        if (board.getPiece(checkPosition).getTeamColor() != myColor) {
          ChessMove newMove=new ChessMove(myPosition, checkPosition, null);
          moves.add(newMove);
        }
        break;
      }
      colIter--;
    }
    //moves down to the right
    colIter = myPosition.getColumn() + 1;
    for (rowIter = myPosition.getRow() - 1; rowIter > 0; rowIter--) {
      if (colIter > 8) break;
      ChessPosition checkPosition=new ChessPosition(rowIter, colIter);
      if (board.getPiece(checkPosition) == null) {
        ChessMove newMove=new ChessMove(myPosition, checkPosition, null);
        moves.add(newMove);
      } else {
        //allow capture of opposing team
        if (board.getPiece(checkPosition).getTeamColor() != myColor) {
          ChessMove newMove=new ChessMove(myPosition, checkPosition, null);
          moves.add(newMove);
        }
        break;
      }
      colIter++;
    }
    return moves;
  }
}

class RookMoveCalculator extends PieceMoveCalculator {
  public List<ChessMove> validMoves(ChessBoard board, ChessPosition myPosition, ChessGame.TeamColor myColor) {
    List<ChessMove> moves = new ArrayList<ChessMove>();
    for (int i = myPosition.getRow()+1; i < 9; i++) {
      ChessPosition checkPosition = new ChessPosition(i, myPosition.getColumn());
      if (board.getPiece(checkPosition) == null) {
        ChessMove newMove = new ChessMove(myPosition, checkPosition, null);
        moves.add(newMove);
      }
      else {
        //allow capture of opposing team
        if (board.getPiece(checkPosition).getTeamColor() != myColor) {
          ChessMove newMove=new ChessMove(myPosition, checkPosition, null);
          moves.add(newMove);
        }
        break;
      }
    }
    for (int i = myPosition.getRow() - 1; i > 0; i--) {
      ChessPosition checkPosition = new ChessPosition(i, myPosition.getColumn());
      if (board.getPiece(checkPosition) == null) {
        ChessMove newMove = new ChessMove(myPosition, checkPosition, null);
        moves.add(newMove);
      }
      else {
        //allow capture of opposing team
        if (board.getPiece(checkPosition).getTeamColor() != myColor) {
          ChessMove newMove=new ChessMove(myPosition, checkPosition, null);
          moves.add(newMove);
        }
        break;
      }
    }
    for (int i = myPosition.getColumn()+1; i < 9; i++) {
      ChessPosition checkPosition = new ChessPosition(myPosition.getRow(), i);
      if (board.getPiece(checkPosition) == null) {
        ChessMove newMove = new ChessMove(myPosition, checkPosition, null);
        moves.add(newMove);
      }
      else {
        //allow capture of opposing team
        if (board.getPiece(checkPosition).getTeamColor() != myColor) {
          ChessMove newMove=new ChessMove(myPosition, checkPosition, null);
          moves.add(newMove);
        }
        break;
      }
    }
    for (int i = myPosition.getColumn()-1; i > 0; i--) {
      ChessPosition checkPosition = new ChessPosition(myPosition.getRow(), i);
      if (board.getPiece(checkPosition) == null) {
        ChessMove newMove = new ChessMove(myPosition, checkPosition, null);
        moves.add(newMove);
      }
      else {
        //allow capture of opposing team
        if (board.getPiece(checkPosition).getTeamColor() != myColor) {
          ChessMove newMove=new ChessMove(myPosition, checkPosition, null);
          moves.add(newMove);
        }
        break;
      }
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
