package websocket;

import com.google.gson.Gson;
import dataaccess.DataAccess;
import dataaccess.MemoryDataAccess;
import dataaccess.SqlDataAccess;
import exception.ResponseException;
import jdk.jshell.spi.ExecutionControl;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import service.ServiceHandler;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import java.io.IOException;

@WebSocket
public class WebSocketHandler {

  private final ConnectionHandler connectionHandler;
  private final DataAccess dataAccess;

  public WebSocketHandler(ServiceHandler chessHandler) {
    DataAccess dataAccess1;
    try {
      dataAccess1 = new SqlDataAccess();
    } catch (Exception e) {
      dataAccess1 = new MemoryDataAccess();
    }
    dataAccess = dataAccess1;
    this.connectionHandler = new ConnectionHandler();
  }

  @OnWebSocketMessage
  private void onMessage(Session session, String message) throws ResponseException {
    UserGameCommand command = new Gson().fromJson(message, UserGameCommand.class);
    MakeMoveCommand moveCommand = null;
    if (command.getCommandType() == UserGameCommand.CommandType.MAKE_MOVE) {
      moveCommand = new Gson().fromJson(message, MakeMoveCommand.class);
    }
    switch (command.getCommandType()) {
      case CONNECT -> connect(session, command);
      case LEAVE -> leave(command);
      case MAKE_MOVE -> makeMove(moveCommand);
      case RESIGN -> resign(command);
    }
  }

  private void resign(UserGameCommand command) {
  }

  private void makeMove(MakeMoveCommand moveCommand) {
  }

  private void connect(Session session, UserGameCommand command) throws ResponseException {
    try {
      var authData=dataAccess.getAuth(command.getAuthToken());
      String message=String.format("%s has joined the game.", authData.username());
      connectionHandler.notification(new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, message), command.getGameID());
      connectionHandler.add(command.getGameID(), authData.username(), session);
    } catch (IOException e) {
      throw new ResponseException(500, "Unable to join game");
    }
  }

  private void leave(UserGameCommand command) throws ResponseException {
    try {
      var authData=dataAccess.getAuth(command.getAuthToken());
      connectionHandler.remove(command.getGameID(), authData.username());
      String message=String.format("%s has left the game.", authData.username());
      connectionHandler.notification(new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, message), command.getGameID());
    } catch (IOException e) {
      throw new ResponseException(500, "Unable to join game");
    }
  }



}
