package websocket;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Objects;

import org.eclipse.jetty.websocket.api.Session;

public class ConnectionHandler {

  private HashMap<String, ArrayList<Connection>> connections;

  public void add(String gameId, String username, Session session) {
    var currentConnections = connections.getOrDefault(gameId, new ArrayList<>());
    currentConnections.add(new Connection(username, session));
    connections.put(gameId, currentConnections);
  }

  public void remove(String gameId, String username) {
    var currentConnections = connections.getOrDefault(gameId, new ArrayList<>());
    currentConnections.removeIf(connection -> Objects.equals(connection.username, username));
    connections.put(gameId, currentConnections);
  }
}
