import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import chess.ChessGame;
import exception.ResponseException;
import servicehelpers.*;
import ui.EscapeSequences;

public class Client {

  private State state = State.LoggedOut;
  private String authToken = null;
  private Map<Integer, GameListElement> gameList = null;

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
    System.out.println("NOT IMPLEMENTED: setting state logged in");
    state = State.LoggedIn;
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
    } catch (NumberFormatException e) {
        System.out.println("Invalid game id, please verify input.");
      } catch (Exception exception) {
        System.out.println(exception.getMessage());
      }
    return "";
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

    return new JoinGameRequest(authToken, joinColor, gameList.get(joinId).gameID());
  }
}
