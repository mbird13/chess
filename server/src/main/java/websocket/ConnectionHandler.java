package websocket;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.Session;
import org.junit.jupiter.params.ParameterizedTest;
import org.slf4j.helpers.FormattingTuple;
import websocket.messages.ErrorMessage;
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
    var removalList = new ArrayList<Connection>();
    for (var user : usersToNotify) {
      if (user.session.isOpen()) {
        user.send(new Gson().toJson(message));
      }
      else {
        removalList.add(user);
      }
    }
    for (var user : removalList) {
      remove(gameID, user.username);
    }
  }
  public void notification(NotificationMessage message, Integer gameID, String currentUser) throws IOException {
    var usersToNotify = connections.getOrDefault(gameID, new ArrayList<>());
    var removalList = new ArrayList<Connection>();
    for (var user : usersToNotify) {
      if (user.session.isOpen()) {
        if (!Objects.equals(user.username, currentUser)) {
          user.send(new Gson().toJson(message));
        }
      }
      else {
        removalList.add(user);
      }
    }
    for (var user : removalList) {
      remove(gameID, user.username);
    }
  }

  public void errorMessage(ErrorMessage message, Session session) throws IOException {
    session.getRemote().sendString(new Gson().toJson(message));
  }

  public void loadGame(LoadGameMessage message, Session session) throws IOException {
    if (session.isOpen()) {
      session.getRemote().sendString(new Gson().toJson(message));
    }
  }

  public void loadGame(LoadGameMessage message, Integer gameID) throws IOException {
    var users= connections.getOrDefault(gameID, new ArrayList<>());
    var removalList = new ArrayList<Connection>();
    for (var user : users) {
      if (user.session.isOpen()) {
          user.send(new Gson().toJson(message));
      } else {
        removalList.add(user);
      }
    }
    for (var user : removalList) {
      remove(gameID, user.username);
    }
  }

  public Map<Integer, String> remove(Session session) {
    for (Map.Entry<Integer, ArrayList<Connection>> entry : connections.entrySet()) {
      for (var connection : entry.getValue()) {
        remove(entry.getKey(), connection.username);
        var result = new HashMap<Integer, String>();
        result.put(entry.getKey(), connection.username);
        return result;
      }
    }
    return null;
  }
}
