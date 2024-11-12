import java.util.*;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPosition;
import exception.ResponseException;
import servicehelpers.*;
import ui.EscapeSequences;

public class Client {

  private State state = State.LoggedOut;
  private String authToken = null;
  private Map<Integer, GameListElement> gameList = null;
  private String currentGameId = null;

  private final ServerFacade server = new ServerFacade("http://localhost:8080");

  public String eval(String input) {
    var tokens = input.toLowerCase().split(" ");
    var cmd = (tokens.length > 0) ? tokens[0] : "help";
    var params = Arrays.copyOfRange(tokens, 1, tokens.length);
    return switch (state) {
      case LoggedOut -> loggedOutMenu(cmd, params);
      case LoggedIn -> loggedInMenu(cmd, params);
      case InGame -> gameMenu(cmd, params);
    };
  }

  private String gameMenu(String cmd, String[] params) {
    return switch (cmd) {
      case "move" -> makeMove(params);
      case "leave" -> leaveGame();
      case "resign" -> resignGame();
      case "repeat" -> repeatLastMove();
      case "help" -> help();
      default -> invalidInput("");
    };
  }

  private String loggedInMenu(String cmd, String[] params) {
    return switch (cmd) {
      case "join" -> joinGame(params);
      case "observe" -> observeGame(params);
      case "list" -> listGames();
      case "logout" -> logout();
      case "create" -> createGame(params);
      case "help" -> help();
      default -> invalidInput("");
    };
  }

  private String loggedOutMenu(String cmd, String[] params) {
    return switch (cmd) {
      case "register" -> register(params);
      case "login" -> login(params);
      case "quit" -> "quit";
      case "help" -> help();
      default -> invalidInput("");
    };
  }

  private String makeMove(String[] params) {
    System.out.println("NOT IMPLEMENTED");
    return "";
  }

  private String leaveGame() {
    try {
      server.leaveGame(currentGameId, authToken);
      System.out.println("You have left the game.");
      state = State.LoggedIn;
    } catch (Exception exception) {
      System.out.println(exception.getMessage());
    }
    return "";
  }

  private String resignGame() {
    System.out.println("NOT IMPLEMENTED");
    return "";
  }

  private String repeatLastMove() {
    System.out.println("NOT IMPLEMENTED");
    return "";
  }

  private String joinGame(String[] params) {
    try {
      var joinRequest = parseJoinParams(params);
      server.joinGame(joinRequest);
      state=State.InGame;
      printGameBoard(new ChessGame().getBoard(), ChessGame.TeamColor.BLACK);
      printGameBoard(new ChessGame().getBoard(), ChessGame.TeamColor.WHITE);
    } catch (NumberFormatException e) {
        System.out.println("Invalid game id, please verify input.");
      } catch (Exception exception) {
        System.out.println(exception.getMessage());
      }
    return "";
  }

  private void printGameBoard(ChessBoard board, ChessGame.TeamColor BottomColor) {
    var bottomColorPositions = board.getPositions(BottomColor);
    var topColorPositions = board.getPositions(ChessGame.TeamColor.WHITE);
    String[] rowLabels = {" 8 ", " 7 ", " 6 ", " 5 ", " 4 ", " 3 ", " 2 ", " 1 "};
    String colLabels ="    h  g  f  e  d  c  b  a    ";
    int startingIndex = 0;
    int finalIndex = 8;
    int offset = 1;
    int bgColor = 0;
    if (BottomColor == ChessGame.TeamColor.WHITE) {
      rowLabels =new String[]{" 1 ", " 2 ", " 3 ", " 4 ", " 5 ", " 6 ", " 7 ", " 8 "};
      colLabels ="    a  b  c  d  e  f  g  h    ";
      topColorPositions = board.getPositions(ChessGame.TeamColor.BLACK);
      startingIndex = 7;
      finalIndex = -1;
    }
    printColLabels(colLabels);
    for (int row = startingIndex; iterationCheck(row, finalIndex, BottomColor);) {
      printRowLabels(rowLabels, row);
      for (int col = startingIndex; iterationCheck(col, finalIndex, BottomColor);) {
        bgColor = setBgColor(bgColor);
        var topIndex = topColorPositions.indexOf(new ChessPosition(row + offset, col + offset));
        var bottomIndex = bottomColorPositions.indexOf(new ChessPosition(row + offset, col + offset));
        if (topIndex != -1) {
          setTopColor(BottomColor);
          System.out.print(" " + board.getPiece(topColorPositions.get(topIndex)) + " ");
          System.out.print(EscapeSequences.RESET_TEXT_COLOR + EscapeSequences.RESET_TEXT_BOLD_FAINT);
        } else if (bottomIndex != -1) {
          setBottomColor(BottomColor);
          System.out.print(" " + board.getPiece(bottomColorPositions.get(bottomIndex)) + " ");
          System.out.print(EscapeSequences.RESET_TEXT_COLOR + EscapeSequences.RESET_TEXT_BOLD_FAINT);
        } else {
          System.out.print("   ");
        }
        col = iterate(BottomColor, col);
      }
      printRowLabels(rowLabels, row);
      System.out.println();
      bgColor++;
      row = iterate(BottomColor, row);
    }
    printColLabels(colLabels);
    System.out.println("");
  }

  private void printRowLabels(String[] labels, int row) {
    System.out.print(EscapeSequences.SET_BG_COLOR_BLACK + EscapeSequences.SET_TEXT_COLOR_WHITE);
    System.out.print(labels[row]);
    System.out.print(EscapeSequences.RESET_BG_COLOR + EscapeSequences.RESET_TEXT_COLOR);
  }

  private void printColLabels(String label) {
    System.out.print(EscapeSequences.SET_BG_COLOR_BLACK + EscapeSequences.SET_TEXT_COLOR_WHITE);
    System.out.print(label);
    System.out.println(EscapeSequences.RESET_BG_COLOR + EscapeSequences.RESET_TEXT_COLOR);
  }

  private int setBgColor(int bgColor) {
    if (bgColor % 2 == 0) {
      System.out.print("\u001B[102m");
    }
    else {
      System.out.print(EscapeSequences.SET_BG_COLOR_DARK_GREEN);
    }
    return bgColor+1;
  }

  private void setBottomColor(ChessGame.TeamColor teamColor) {
    System.out.print(EscapeSequences.SET_TEXT_BOLD);
    if (teamColor == ChessGame.TeamColor.WHITE) {
      System.out.print(EscapeSequences.SET_TEXT_COLOR_WHITE);
    }
    else {
      System.out.print(EscapeSequences.SET_TEXT_COLOR_BLACK);
    }
  }

  private void setTopColor(ChessGame.TeamColor teamColor) {
    System.out.print(EscapeSequences.SET_TEXT_BOLD);
    if (teamColor == ChessGame.TeamColor.BLACK) {
      System.out.print(EscapeSequences.SET_TEXT_COLOR_WHITE);
    }
    else {
      System.out.print(EscapeSequences.SET_TEXT_COLOR_BLACK);
    }
  }

  private boolean iterationCheck(int index, int finalIndex, ChessGame.TeamColor teamColor) {
    if (teamColor == ChessGame.TeamColor.WHITE) {
      return index > finalIndex;
    }
    return index < finalIndex;
  }

  private int iterate(ChessGame.TeamColor teamColor, int index) {
    if (teamColor == ChessGame.TeamColor.WHITE) {
      return index-1;
    }
    return index+1;
  }

  private String observeGame(String[] params) {
    System.out.println("NOT IMPLEMENTED");
    return "";
  }

  private String listGames() {
    try {
      gameList = new HashMap<>();
      var response = server.listGames(new ListGamesRequest(authToken)).games;

      for (int i = 0; i < response.size(); i++) {
        System.out.println(i+1 + ": " + response.get(i).gameName());
        gameList.put(i+1, response.get(i));
      }
      if (response.isEmpty()) {
        System.out.println("There are no current games. Create One!!");
      }
    } catch (ResponseException exception) {
      System.out.println(exception.getMessage());
    }
    return "";
  }

  private String logout() {
    try {
      server.logout(new LogoutRequest(authToken));
      state = State.LoggedOut;
      authToken = null;
      System.out.println("You have logged out");
    } catch (ResponseException exception) {
      System.out.println(exception.getMessage());
    }
    return "";
  }

  private String createGame(String[] params) {
    if (params.length != 1) {
      invalidInput("To create a new game: 'create' <GAME NAME>");
      return "";
    }
    try {
      server.createGame(new CreateGameRequest(authToken, params[0]));
    } catch (ResponseException e) {
      System.out.println(e.getMessage());
    }
    return "";
  }

  private String invalidInput(String message) {
    System.out.println(
            EscapeSequences.SET_TEXT_COLOR_RED + "Invalid input. " + message
                    + "\nFor a list of valid commands, type help"
                    + EscapeSequences.RESET_TEXT_COLOR + "\n");
    return "";
  }

  private String register(String[] params) {
    if (params.length != 3) {
      invalidInput("To register a new user: 'register' <USERNAME> <PASSWORD> <EMAIL>");
    }
    else {
      try {
        LoginResult response = server.register(new RegisterRequest(params[0], params[1], params[2]));
        authToken = response.authToken();
        state = State.LoggedIn;
        System.out.println("You are now logged in as " + response.username());
      } catch (ResponseException exception) {
        System.out.println(exception.getMessage());
      }
    }
    return "";
  }

  private String login(String[] params) {
    if (params.length != 2) {
      invalidInput("To log in as existing user: 'login' <USERNAME> <PASSWORD>");
    }
    else {
      try {
        LoginResult response = server.login(new LoginRequest(params[0], params[1]));
        authToken = response.authToken();
        state = State.LoggedIn;
        System.out.println("You are now logged in as " + response.username());
      } catch (ResponseException exception) {
        System.out.println(exception.getMessage());
      }
    }
    return "";
  }

  private String help() {
    switch (state) {
      case LoggedOut -> {
        System.out.println("Options:");
        System.out.println("Login as existing user: 'login' <USERNAME> <PASSWORD>");
        System.out.println("Register a new user: 'register' <USERNAME> <PASSWORD> <EMAIL>");
        System.out.println("Exit the program: 'quit'");
        System.out.println("See options: 'help'");
      }
      case LoggedIn -> {
        System.out.println("Options:");
        System.out.println("Logout: 'logout'");
        System.out.println("Create a new game: 'create' <GAME NAME>");
        System.out.println("List existing games: 'list'");
        System.out.println("Join an existing game: 'join' <GAME ID> <COLOR>");
        System.out.println("Observe an ongoing game: 'observe' <GAME ID>");
        System.out.println("See options: 'help'");
      }
      case InGame -> {
        System.out.println("Instructions for playing a game:");
        System.out.println("Make a move during your turn: 'move' <STARTING POSITION> <FINAL POSITION>");
        System.out.println("Leave Game: 'leave' ");
        System.out.println("Resign the game: 'resign'");
        System.out.println("See instructions: 'help'");
      }
    };
    printGameBoard(new ChessGame().getBoard(), ChessGame.TeamColor.BLACK);
    printGameBoard(new ChessGame().getBoard(), ChessGame.TeamColor.WHITE);
    return "";
  }

  public void printPrompt() {
    switch (state) {
      case LoggedOut -> {
        System.out.print("\nInput Command:");
      }
      case LoggedIn -> {
        System.out.println("\nLogged in");
        System.out.print("Input Command:");

      }
      case InGame -> {
        System.out.println("\n");
        System.out.print("Make a move:");
      }
    };
  }

  private JoinGameRequest parseJoinParams(String[] params) throws Exception {
    if (params.length != 2) {
      invalidInput("To join an existing game: 'join' <GAME ID> <COLOR>");
      throw new Exception();
    }
    if (gameList == null) {
      gameList=new HashMap<>();
      var response=server.listGames(new ListGamesRequest(authToken)).games;
      for (int i=0; i < response.size(); i++) {
        gameList.put(i + 1, response.get(i));
      }
    }

    var joinId = Integer.parseInt(params[0]);
    if (joinId < 1 | joinId > gameList.size()) {
      throw new Exception("Invalid game Id. Please verify information.");
    }

    ChessGame.TeamColor joinColor;
    if (params[1].equalsIgnoreCase("white") | params[1].equalsIgnoreCase("w")) {
      joinColor =ChessGame.TeamColor.WHITE;
    } else if (params[1].equalsIgnoreCase("black") | params[1].equalsIgnoreCase("b")) {
      joinColor =ChessGame.TeamColor.BLACK;
    }
    else {throw new Exception("Invalid team color");}

    currentGameId = params[0];
    return new JoinGameRequest(authToken, joinColor, gameList.get(joinId).gameID());
  }
}
