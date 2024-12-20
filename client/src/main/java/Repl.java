import java.util.Scanner;

public class Repl {

  Client client = new Client();

  public void run() {

    var scanner = new Scanner(System.in);
    String result = "";
    while(!result.equals("quit")) {
      if (!result.equals("join")) {
        client.printPrompt();
      }
      String line = scanner.nextLine();

      try {
        result = client.eval(line);
      } catch (Exception exception) {
          System.out.println("Error, please try again" );
      }
    }
  }
}
