import java.util.Scanner;

public class Repl {

  public void run() {

    var scanner = new Scanner(System.in);
    String result = "";
    while(!result.equals("quit")) {
      printPrompt();
      result = scanner.next();
    }
  }

  private void printPrompt() {
    System.out.print("Input Command: ");
  }
}
