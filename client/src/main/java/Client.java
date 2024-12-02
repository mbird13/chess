import java.util.*;

import chess.*;
import serverfacade.ServerFacade;
import exception.ResponseException;
import servicehelpers.*;
import ui.EscapeSequences;

public class Client {

  private State state = State.LoggedOut;
  private String authToken = null;
  private Map<Integer, GameListElement> gameList = null;
  private String currentGameId = null;
  private WebSocketFacade webSocketFacade = null;
  private ChessGame.TeamColor myColor = null;
  private ChessGame currentGame = null;

  private final ServerFacade server = new ServerFacade("http://localhost:8080");

  public String eval(String input) {
    var tokens = input.toLowerCase().split(" ");
    var cmd = (tokens.length > 0) ? tokens[0] : "no";
    var params = Arrays.copyOfRange(tokens, 1, tokens.length);
    return switch (state) {
      case LoggedOut -> loggedOutMenu(cmd, params);
      case LoggedIn -> loggedInMenu(cmd, params);
      case InGame -> gameMenu(cmd, params);
      case Observer -> observerMenu(cmd, params);
    };
  }

  private String observerMenu(String cmd, String[] params) {
    return switch (cmd) {
      case "board" -> redraw();
      case "leave" -> leaveGame();
      case "help" -> help();
      default -> invalidInput("");
    };
  }

  private String redraw() {
    printGameBoard(currentGame.getBoard(), myColor);
    return "";
  }

  private String gameMenu(String cmd, String[] params) {
    return switch (cmd) {
      case "move" -> makeMove(params);
      case "board" -> redraw();
      case "leave" -> leaveGame();
      case "resign" -> resignGame();
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
    ChessMove move = parseMoveParams(params);
    if (move == null) {
      return "";
    }
    try {
      if (currentGame.isGameOver()) {
        printErrorMessage("The game is over");
        return "";
      }
      if (currentGame.getTeamTurn() == myColor) {
        currentGame.makeMove(move);
        webSocketFacade.makeMove(authToken, move, currentGameId);
      }
      else {
        printErrorMessage("It is not your turn.");
      }
    } catch (InvalidMoveException e) {
      printErrorMessage("Invalid move: To highlight valid moves type 'highlight' <START POSITION>");
    } catch (ResponseException e) {
      printErrorMessage(e.getMessage());
    }

    return "";
  }

  private ChessMove parseMoveParams(String[] params) {
    if (params.length != 2 && params.length != 3) {
      invalidInput("To move a piece: 'move' <STARTING POSITION> <FINAL POSITION>" +
              "\nTo move a piece and promote it: 'move' <STARTING POSITION> <FINAL POSITION> <PROMOTION PIECE>");
      return null;
    }
    var positions=new ArrayList<ChessPosition>();
    positions.add(getPositionFromInput(params[0]));
    if (positions.get(0) == null) {
      return null;
    }
    positions.add(getPositionFromInput(params[1]));
    if (positions.get(1) == null) {
      return null;
    }
    if (params.length == 2) {
      return new ChessMove(positions.get(0), positions.get(1), null);
    }
    return new ChessMove(positions.get(0), positions.get(1), getPromotionPieceFromInput(params[2]));
  }

  private ChessPiece.PieceType getPromotionPieceFromInput(String param) {
    var pieceInput = param.toLowerCase();
    var promotionPiece = switch (pieceInput) {
      case "queen" -> ChessPiece.PieceType.QUEEN;
      case "bishop" -> ChessPiece.PieceType.BISHOP;
      case "rook" -> ChessPiece.PieceType.ROOK;
      case "knight" -> ChessPiece.PieceType.KNIGHT;
      default -> null;
    };
    if (promotionPiece == null) {
      invalidInput("Invalid promotion piece: " + param);
    }
    return promotionPiece;
  }

  private ChessPosition getPositionFromInput(String param) {
    if (param.length() != 2) {
      printErrorMessage("Invalid position");
      return null;
    }
    var move = param.toLowerCase();
    int column = move.charAt(0) - 'a' + 1;
    int row = Integer.parseInt(move.substring(1));
    if (column < 1 || column > 8 || row < 1 || row > 8) {
      printErrorMessage("Invalid position: " + param);
      return null;
    }
    return new ChessPosition(row, column);
  }

  private String leaveGame() {
    try {
      webSocketFacade.leaveGame(authToken, currentGameId);
      System.out.println("You have left the game.");
      state = State.LoggedIn;
    } catch (Exception exception) {
      System.out.println(exception.getMessage());
    }
    return "";
  }

  private String resignGame() {
    if (currentGame.getTeamTurn() != myColor) {
      printErrorMessage("It is not your turn.");
      return "";
    }
    try {
      System.out.println("\n" + "Are you sure you want to resign the game?");
      var scanner = new Scanner(System.in);
      String line = scanner.nextLine();

      var tokens = line.toLowerCase().split(" ");
      var cmd = (tokens.length > 0) ? tokens[0] : "no";

      if (!cmd.equals("yes")) {
        return "";
      }
      webSocketFacade.resign(authToken, currentGameId);
    } catch (ResponseException e) {
      printErrorMessage("Unable to resign");
    }

    return "";
  }

  private String joinGame(String[] params) {
    try {
      var joinRequest = parseJoinParams(params);
      server.joinGame(joinRequest);
      webSocketFacade = new WebSocketFacade("http://localhost:8080", this);
      webSocketFacade.joinGame(joinRequest);
      state = State.InGame;
      myColor = joinRequest.playerColor();
    } catch (NumberFormatException e) {
        printErrorMessage("Invalid game number, please verify input.");
        return "";
      } catch (ResponseException e) {
        printErrorMessage("That color is already taken.");
        return "";
    }
    catch (Exception exception) {
        printErrorMessage(exception.getMessage());
        return "";
      }
    return "join";
  }

  private String observeGame(String[] params) {
    try {
      if (params.length != 1) {
        invalidInput("To observe a game: 'observe' <GAME NUMBER>");
        throw new Exception("");
      }
      if (gameList == null) {
        gameList=new HashMap<>();
        ArrayList<GameListElement> response;
        response=server.listGames(new ListGamesRequest(authToken)).games;

        for (int i=0; i < response.size(); i++) {
          gameList.put(i + 1, response.get(i));
        }
      }
      var joinId = Integer.parseInt(params[0]);
      if (joinId < 1 | joinId > gameList.size()) {
        throw new Exception("Invalid game number. Please verify information.");
      }
      JoinGameRequest request = new JoinGameRequest(authToken, null, gameList.get(joinId).gameID());
      webSocketFacade = new WebSocketFacade("http://localhost:8080", this);
      webSocketFacade.joinGame(request);
      state = State.Observer;
      myColor = ChessGame.TeamColor.WHITE;
      currentGameId = request.gameID();

    } catch (Exception e) {
      printErrorMessage("Invalid game number. Please verify information.");
    }
    return "join";
  }

  private String listGames() {
    try {
      gameList = new HashMap<>();
      var response = server.listGames(new ListGamesRequest(authToken)).games;

      for (int i = 0; i < response.size(); i++) {
        String whitePlayer = response.get(i).whiteUsername() == null ? "" : ", White Player: " + response.get(i).whiteUsername();
        String blackPlayer = response.get(i).blackUsername() == null ? "" : ", Black Player: " + response.get(i).blackUsername();
        System.out.println(i+1 + ": " + response.get(i).gameName() + whitePlayer + blackPlayer);
        gameList.put(i+1, response.get(i));
      }
      if (response.isEmpty()) {
        System.out.println("There are no current games. Create one!!");
      }
    } catch (ResponseException exception) {
      printErrorMessage(exception.getMessage());
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
      printErrorMessage(exception.getMessage());
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
      System.out.println("New game was created. List games to see it.");
    } catch (ResponseException e) {
      printErrorMessage(e.getMessage());
    }
    return "";
  }

  private String invalidInput(String message) {
    printErrorMessage("Invalid input. " + message
                    + "\nFor a list of valid commands, type help");
    return "";
  }

  public String printErrorMessage(String message) {
    System.out.println(
            EscapeSequences.SET_TEXT_COLOR_RED + message
                    + EscapeSequences.RESET_TEXT_COLOR + "\n");
    return "";
  }

  public void printStatusMessage(String message) {
    System.out.println("\n\n" + message);
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
        printErrorMessage("Username already taken.");
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
        printErrorMessage("The username or password is incorrect. Try again.");
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
        System.out.println("Join an existing game: 'join' <GAME NUMBER> <COLOR>");
        System.out.println("Observe an ongoing game: 'observe' <GAME NUMBER>");
        System.out.println("See options: 'help'");
      }
      case InGame -> {
        System.out.println("Instructions for playing a game:");
        System.out.println("Make a move during your turn: 'move' <STARTING POSITION> <FINAL POSITION>");
        System.out.println("Redraw the current board: 'board'");
        System.out.println("Leave Game: 'leave' ");
        System.out.println("Resign the game: 'resign'");
        System.out.println("See instructions: 'help'");
      }
      case Observer -> {
        System.out.println("Options:");
        System.out.println("Redraw the current board: 'board'");
        System.out.println("Leave Game: 'leave' ");
        System.out.println("See instructions: 'help'");
      }
    }
    return "";
  }

  public void printPrompt() {
    switch (state) {
      case LoggedOut -> {
        System.out.print("\nInput Command: ");
      }
      case LoggedIn -> {
        System.out.println("\nLogged in");
        System.out.print("Input Command: ");

      }
      case InGame -> {
        System.out.println("\n");
        System.out.print("Make a move: ");
      }
      case Observer -> {
        System.out.println("\nObserving game");
        System.out.print("Input Command: ");
      }
    }
  }

  private JoinGameRequest parseJoinParams(String[] params) throws Exception {
    if (params.length != 2) {
      throw new Exception("Invalid input. To join an existing game: " +
              "'join' <GAME NUMBER> <COLOR>\nFor a list of valid commands, type help.");
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
      throw new Exception("Invalid game number. Please verify information.");
    }

    ChessGame.TeamColor joinColor;
    if (params[1].equalsIgnoreCase("white") | params[1].equalsIgnoreCase("w")) {
      joinColor =ChessGame.TeamColor.WHITE;
    } else if (params[1].equalsIgnoreCase("black") | params[1].equalsIgnoreCase("b")) {
      joinColor =ChessGame.TeamColor.BLACK;
    }
    else {throw new Exception("Invalid team color");}

    currentGameId = gameList.get(joinId).gameID();
    return new JoinGameRequest(authToken, joinColor, gameList.get(joinId).gameID());
  }

  public void printGameBoard(ChessBoard board, ChessGame.TeamColor bottomColor) {
    var bottomColorPositions = board.getPositions(bottomColor);
    var topColorPositions = board.getPositions(ChessGame.TeamColor.WHITE);
    String[] rowLabels = {" 1 ", " 2 ", " 3 ", " 4 ", " 5 ", " 6 ", " 7 ", " 8 "};
    String colLabels ="    h  g  f  e  d  c  b  a    ";
    int rowStartingIndex = 0;
    int rowFinalIndex = 8;
    int colStartingIndex = 7;
    int colFinalIndex = -1;
    int offset = 1;
    int bgColor = 0;
    if (bottomColor == ChessGame.TeamColor.WHITE) {
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
        bgColor = setBgColor(bgColor);
        var topIndex = topColorPositions.indexOf(new ChessPosition(row + offset, col + offset));
        var bottomIndex = bottomColorPositions.indexOf(new ChessPosition(row + offset, col + offset));
        if (topIndex != -1) {
          setTopColor(bottomColor);
          System.out.print(" " + board.getPiece(topColorPositions.get(topIndex)) + " ");
          System.out.print(EscapeSequences.RESET_TEXT_COLOR + EscapeSequences.RESET_TEXT_BOLD_FAINT);
        } else if (bottomIndex != -1) {
          setBottomColor(bottomColor);
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

  private void printRowLabels(String[] labels, int row) {
    System.out.print(EscapeSequences.SET_BG_COLOR_LIGHT_GREY + EscapeSequences.SET_TEXT_COLOR_WHITE);
    System.out.print(labels[row]);
    System.out.print(EscapeSequences.RESET_BG_COLOR + EscapeSequences.RESET_TEXT_COLOR);
  }

  private void printColLabels(String label) {
    System.out.print(EscapeSequences.SET_BG_COLOR_LIGHT_GREY + EscapeSequences.SET_TEXT_COLOR_WHITE);
    System.out.print(label);
    System.out.println(EscapeSequences.RESET_BG_COLOR + EscapeSequences.RESET_TEXT_COLOR);
  }

  private int setBgColor(int bgColor) {
    if (bgColor % 2 == 0) {
      System.out.print("\u001B[48;2;184;160;75m");
    }
    else {
      System.out.print("\u001B[48;2;156;123;9m");
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

  private boolean iterationCheck(int index, int finalIndex) {
    if (finalIndex == -1) {
      return index > finalIndex;
    }
    return index < finalIndex;
  }

  private int iterate(int finalIndex, int index) {
    if (finalIndex == -1) {
      return index-1;
    }
    return index+1;
  }

  public void printGameBoard(ChessGame game) {
    printGameBoard(game.getBoard(), myColor);
  }

  public void setGame(ChessGame game) {
    this.currentGame = game;
  }
}
