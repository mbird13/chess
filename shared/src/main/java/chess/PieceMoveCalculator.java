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
      if (colIter > 8) break;
    }
    //moves down to the left
    colIter = myPosition.getColumn() - 1;
    for (rowIter = myPosition.getRow() - 1; rowIter > 0; rowIter--) {
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
      if (colIter <= 0) break;
    }
    //moves up to the left
    colIter = myPosition.getColumn() - 1;
    for (rowIter = myPosition.getRow() + 1; rowIter <= 8; rowIter++) {
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
      if (colIter <= 0) break;
    }
    //moves down to the right
    colIter = myPosition.getColumn() + 1;
    for (rowIter = myPosition.getRow() - 1; rowIter > 0; rowIter--) {
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
      if (colIter > 8) break;
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
    List<ChessMove> moves = new ArrayList<ChessMove>();

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
    List<ChessMove> moves = new ArrayList<ChessMove>();

    return moves;
  }
}
