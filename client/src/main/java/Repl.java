import java.util.Scanner;

public class Repl {

  Client client = new Client();

  public void run() {

    var scanner = new Scanner(System.in);
    String result = "";
    while(!result.equals("quit")) {
      client.printPrompt();
      String line = scanner.nextLine();

      try {
        result = client.eval(line);
      } catch (Exception exception) {
          System.out.println("caught exception in REPL: " + exception.getMessage());
      }
    }
  }
}
