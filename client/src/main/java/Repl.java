import java.util.Scanner;

public class Repl {

  Client client = new InitialClient();

  public void run() {

    var scanner = new Scanner(System.in);
    String result = "";
    while(!result.equals("quit")) {
      printPrompt();
      String line = scanner.nextLine();

      try {
        result = client.eval(line);
      } catch (Exception ignore) {}
    }
  }

  private void printPrompt() {
    System.out.print("Input Command: ");
  }
}
