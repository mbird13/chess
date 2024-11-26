package websocket;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

import com.google.gson.Gson;
import model.GameData;
import org.eclipse.jetty.websocket.api.Session;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;

public class ConnectionHandler {

  private final HashMap<Integer, ArrayList<Connection>> connections = new HashMap<>();

  public void add(Integer gameId, String username, Session session) {
    var currentConnections = connections.getOrDefault(gameId, new ArrayList<>());
    currentConnections.add(new Connection(username, session));
    connections.put(gameId, currentConnections);
  }

  public void remove(Integer gameId, String username) {
    var currentConnections = connections.getOrDefault(gameId, new ArrayList<>());
    currentConnections.removeIf(connection -> Objects.equals(connection.username, username));
    connections.put(gameId, currentConnections);
  }

  public void notification(NotificationMessage message, Integer gameID) throws IOException {
    var usersToNotify = connections.getOrDefault(gameID, new ArrayList<>());
    for (var user : usersToNotify) {
      if (user.session.isOpen()) {
        user.send(new Gson().toJson(message));
      }
      else {
        remove(gameID, user.username);
      }
    }
  }

  public void notification(NotificationMessage message, Session session) throws IOException {
    if (session.isOpen()) {
      session.getRemote().sendString(new Gson().toJson(message));
    }
  }

  public void loadGame(LoadGameMessage message, Session session) throws IOException {
    if (session.isOpen()) {
      session.getRemote().sendString(new Gson().toJson(message));
    }
  }
}
