import com.google.gson.Gson;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

public class NotificationHandler {

  private final Client client;

  public NotificationHandler(Client client) {
    this.client = client;
  }

  public void handleNotification(String message) {
    ServerMessage serverMessage = new Gson().fromJson(message, ServerMessage.class);
    switch (serverMessage.getServerMessageType()) {
      case NOTIFICATION -> notification(message);
      case LOAD_GAME ->  loadGame(message);
      case ERROR -> error(message);
    }
  }

  private void error(String message) {
    NotificationMessage errorMessage = new Gson().fromJson(message, NotificationMessage.class);
    client.printErrorMessage(errorMessage.message);
    client.printPrompt();
  }

  private void loadGame(String message) {
    var loadGameMessage = new Gson().fromJson(message, LoadGameMessage.class);
    client.printStatusMessage("");
    client.printGameBoard(loadGameMessage.game);
    client.printPrompt();
  }

  private void notification(String message) {
    NotificationMessage notificationMessage = new Gson().fromJson(message, NotificationMessage.class);
    client.printStatusMessage(notificationMessage.message);
    client.printPrompt();
  }
}