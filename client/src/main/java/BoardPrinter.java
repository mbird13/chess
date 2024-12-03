import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;
import ui.EscapeSequences;

import java.util.ArrayList;
import java.util.Collection;

public class BoardPrinter {
  private final Client client;

  public BoardPrinter(Client client) {
    this.client = client;
  }

  public void printGameBoard(ChessBoard board, ChessGame.TeamColor myColor, Collection<ChessMove> moves) {
    if (moves == null) {
      moves = new ArrayList<>();
    }
    var bottomColorPositions = board.getPositions(myColor);
    var topColorPositions = board.getPositions(ChessGame.TeamColor.WHITE);
    String[] rowLabels = {" 1 ", " 2 ", " 3 ", " 4 ", " 5 ", " 6 ", " 7 ", " 8 "};
    String colLabels ="    h  g  f  e  d  c  b  a    ";
    int rowStartingIndex = 0;
    int rowFinalIndex = 8;
    int colStartingIndex = 7;
    int colFinalIndex = -1;
    int offset = 1;
    int bgColor = 0;
    if (myColor == ChessGame.TeamColor.WHITE) {
      //rowLabels =new String[] {" 8 ", " 7 ", " 6 ", " 5 ", " 4 ", " 3 ", " 2 ", " 1 "};
      colLabels ="    a  b  c  d  e  f  g  h    ";
      topColorPositions = board.getPositions(ChessGame.TeamColor.BLACK);
      rowStartingIndex = 7;
      rowFinalIndex = -1;
      colStartingIndex = 0;
      colFinalIndex = 8;
    }
    printColLabels(colLabels);
    for (int row = rowStartingIndex; iterationCheck(row, rowFinalIndex);) {
      printRowLabels(rowLabels, row);
      for (int col = colStartingIndex; iterationCheck(col, colFinalIndex);) {
        bgColor = setBgColor(bgColor, moves, row, col);
        var topIndex = topColorPositions.indexOf(new ChessPosition(row + offset, col + offset));
        var bottomIndex = bottomColorPositions.indexOf(new ChessPosition(row + offset, col + offset));
        if (topIndex != -1) {
          setTopColor(myColor);
          System.out.print(" " + board.getPiece(topColorPositions.get(topIndex)) + " ");
          System.out.print(EscapeSequences.RESET_TEXT_COLOR + EscapeSequences.RESET_TEXT_BOLD_FAINT);
        } else if (bottomIndex != -1) {
          setBottomColor(myColor);
          System.out.print(" " + board.getPiece(bottomColorPositions.get(bottomIndex)) + " ");
          System.out.print(EscapeSequences.RESET_TEXT_COLOR + EscapeSequences.RESET_TEXT_BOLD_FAINT);
        } else {
          System.out.print("   ");
        }
        col = iterate(colFinalIndex, col);
      }
      printRowLabels(rowLabels, row);
      System.out.println();
      bgColor++;
      row = iterate(rowFinalIndex, row);
    }
    printColLabels(colLabels);
    System.out.println();
  }

  public int setBgColor(int bgColor, Collection<ChessMove> moves, int row, int col) {
    boolean isHighlighted = false;
    for (var move : moves) {
      if (move.getEndPosition().getRow() == row + 1 && move.getEndPosition().getColumn() == col + 1) {
        isHighlighted=true;
        break;
      }
    }
    if (bgColor % 2 == 0) {
      if (isHighlighted) {
        System.out.print("\u001B[48;2;250;120;55m");
      } else {
        System.out.print("\u001B[48;2;184;160;75m");
      }
    }
    else {
      if (isHighlighted) {
        System.out.print("\u001B[48;2;250;63;0m");
      } else {
        System.out.print("\u001B[48;2;156;123;9m");
      }
    }
    return bgColor+1;
  }

  public void printGameBoard(ChessBoard board, ChessGame.TeamColor bottomColor) {
    printGameBoard(board, bottomColor, new ArrayList<>());
  }

  public void printRowLabels(String[] labels, int row) {
    System.out.print(EscapeSequences.SET_BG_COLOR_LIGHT_GREY + EscapeSequences.SET_TEXT_COLOR_WHITE);
    System.out.print(labels[row]);
    System.out.print(EscapeSequences.RESET_BG_COLOR + EscapeSequences.RESET_TEXT_COLOR);
  }

  public void printColLabels(String label) {
    System.out.print(EscapeSequences.SET_BG_COLOR_LIGHT_GREY + EscapeSequences.SET_TEXT_COLOR_WHITE);
    System.out.print(label);
    System.out.println(EscapeSequences.RESET_BG_COLOR + EscapeSequences.RESET_TEXT_COLOR);
  }

  public void setBottomColor(ChessGame.TeamColor teamColor) {
    System.out.print(EscapeSequences.SET_TEXT_BOLD);
    if (teamColor == ChessGame.TeamColor.WHITE) {
      System.out.print(EscapeSequences.SET_TEXT_COLOR_WHITE);
    }
    else {
      System.out.print(EscapeSequences.SET_TEXT_COLOR_BLACK);
    }
  }

  public void setTopColor(ChessGame.TeamColor teamColor) {
    System.out.print(EscapeSequences.SET_TEXT_BOLD);
    if (teamColor == ChessGame.TeamColor.BLACK) {
      System.out.print(EscapeSequences.SET_TEXT_COLOR_WHITE);
    }
    else {
      System.out.print(EscapeSequences.SET_TEXT_COLOR_BLACK);
    }
  }

  public boolean iterationCheck(int index, int finalIndex) {
    if (finalIndex == -1) {
      return index > finalIndex;
    }
    return index < finalIndex;
  }

  public int iterate(int finalIndex, int index) {
    if (finalIndex == -1) {
      return index-1;
    }
    return index+1;
  }

  public void printGameBoard(ChessGame game) {
    printGameBoard(game.getBoard(), client.getMyColor());
    if (game.isGameOver()) {
      if (game.getWinner() != null) {
        client.printStatusMessage(String.format("%s has won the game", game.getWinner()));
      } else {
        client.printStatusMessage("The game ended in a draw");
      }
    }
  }
}
