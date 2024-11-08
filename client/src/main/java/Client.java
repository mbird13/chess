import java.util.Arrays;

import ui.EscapeSequences;

public class Client {

  private State state = State.LoggedOut;

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
      default -> invalidInput();
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
      default -> invalidInput();
    };
  }

  private String loggedOutMenu(String cmd, String[] params) {
    return switch (cmd) {
      case "register" -> register(params);
      case "login" -> login(params);
      case "quit" -> "quit";
      case "help" -> help();
      default -> invalidInput();
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
    state = State.InGame;
    System.out.println("NOT IMPLEMENTED: setting state in game");
    return "";
  }

  private String observeGame(String[] params) {
    System.out.println("NOT IMPLEMENTED");
    return "";
  }

  private String listGames() {
    System.out.println("NOT IMPLEMENTED");
    return "";
  }

  private String logout() {
    System.out.println("NOT IMPLEMENTED");
    state = State.LoggedOut;
    return "";
  }

  private String createGame(String[] params) {
    System.out.println("NOT IMPLEMENTED");
    return "";
  }

  private String invalidInput() {
    System.out.println(EscapeSequences.SET_TEXT_COLOR_RED + "Invalid input. For a list of valid commands, type help" + EscapeSequences.RESET_TEXT_COLOR + "\n");
    return "";
  }

  private String register(String[] params) {
    System.out.println("NOT IMPLEMENTED");
    return "";
  }

  private String login(String[] params) {
    System.out.println("NOT IMPLEMENTED: setting state logged in");
    state = State.LoggedIn;
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
        System.out.println("Join an existing game: 'join' <GAME ID>");
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
}
