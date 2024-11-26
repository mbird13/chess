package websocket;

import com.google.gson.Gson;
import dataaccess.DataAccess;
import dataaccess.MemoryDataAccess;
import dataaccess.SqlDataAccess;
import exception.ResponseException;
import model.AuthData;
import model.GameData;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.util.Objects;

@WebSocket
public class WebSocketHandler {

  private final ConnectionHandler connectionHandler;
  private final DataAccess dataAccess;

  public WebSocketHandler() {
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
  public void onMessage(Session session, String message) throws ResponseException, IOException {
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

  private void connect(Session session, UserGameCommand command) throws IOException {
    try {
      verifyAuthToken(command.getAuthToken());
      verifyGame(command.getGameID());
      var authData= dataAccess.getAuth(command.getAuthToken());
      String message=String.format("%s has joined the game.", authData.username());
      connectionHandler.notification(new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, message), command.getGameID());
      connectionHandler.add(command.getGameID(), authData.username(), session);

      var gameData = dataAccess.getGame(String.valueOf(command.getGameID()));
      var loadGameMessage = new LoadGameMessage(ServerMessage.ServerMessageType.LOAD_GAME, gameData.game());
      connectionHandler.loadGame(loadGameMessage, session);
    } catch (IOException | ResponseException e) {
      ErrorMessage errorMessage =
              new ErrorMessage(ServerMessage.ServerMessageType.ERROR, "Error: Unable to join game.");
      connectionHandler.errorMessage(errorMessage, session);
    }
  }

  private void leave(UserGameCommand command) throws ResponseException {
    try {
      verifyAuthToken(command.getAuthToken());
      var user= dataAccess.getAuth(command.getAuthToken());

      GameData oldGameData;
      try {
        oldGameData = dataAccess.getGame(String.valueOf(command.getGameID()));
      } catch (ResponseException e) {
        throw new ResponseException(400, "Error: bad request");
      }
      if (oldGameData == null) {
        throw new ResponseException(400, "Error: bad request");
      }
      GameData newGameData;
      if (Objects.equals(oldGameData.whiteUsername(), user.username())) {
        newGameData = new GameData(oldGameData.gameID(),
                null, oldGameData.blackUsername(), oldGameData.gameName(), oldGameData.game());
      } else if (Objects.equals(oldGameData.blackUsername(), user.username())) {
        newGameData = new GameData(oldGameData.gameID(),
                oldGameData.whiteUsername(), null, oldGameData.gameName(), oldGameData.game());
      } else {
        throw new ResponseException(400, "Error: bad request");
      }

      dataAccess.updateGame(String.valueOf(command.getGameID()), newGameData);

      connectionHandler.remove(command.getGameID(), user.username());
      String message=String.format("%s has left the game.", user.username());
      connectionHandler.notification(new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, message), command.getGameID());
    } catch (IOException e) {
      throw new ResponseException(500, "Unable to leave game");
    }
  }

  private void verifyAuthToken(String token) throws ResponseException {
    AuthData authData = dataAccess.getAuth(token);
    if (authData == null) {
      throw new ResponseException(401, "Error: unauthorized");
    }
  }

  private void verifyGame(Integer gameID) throws ResponseException {
    GameData gameData = dataAccess.getGame(String.valueOf(gameID));
    if (gameData == null) {
      throw new ResponseException(401, "Error: game doesn't exist");
    }
  }

}
