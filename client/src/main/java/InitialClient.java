import java.util.Arrays;
import java.util.Locale;

public class InitialClient implements Client{
  @Override
  public String eval(String input) {
    var tokens = input.toLowerCase().split(" ");
    var cmd = (tokens.length > 0) ? tokens[0] : "help";
    var params = Arrays.copyOfRange(tokens, 1, tokens.length);
    return switch (cmd){
      case "register" -> register(params);
      case "login" -> login(params);
      case "quit" -> "quit";
      case "help" -> help();
      default -> invalidInput();
    };
  }

  private String invalidInput() {
    System.out.println("Invalid input. For a list of valid commands, type help");
    return "";
  }

  private String register(String[] params) {
    System.out.println("NOT IMPLEMENTED");
    return "quit";
  }

  private String help() {
    System.out.println("NOT IMPLEMENTED");
    return "quit";
  }

  private String login(String[] params) {
    System.out.println("NOT IMPLEMENTED");
    return "quit";
  }
}
