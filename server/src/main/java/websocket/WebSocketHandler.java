package websocket;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;
import chess.InvalidMoveException;
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
      case MAKE_MOVE -> makeMove(session, moveCommand);
      case RESIGN -> resign(session, command);
    }
  }

  private void resign(Session session, UserGameCommand command) throws IOException {
    try {
      verifyAuthToken(command.getAuthToken());
      verifyGame(command.getGameID());
      var gameData=dataAccess.getGame(String.valueOf(command.getGameID()));
      var game=gameData.game();
      var authData=dataAccess.getAuth(command.getAuthToken());

      if (!(authData.username().equals(gameData.whiteUsername()) || authData.username().equals(gameData.blackUsername()))) {
        throw new ResponseException(500, "Unable to resign game as an observer");
      }
      var userColor=(Objects.equals(authData.username(), gameData.whiteUsername()))
              ? ChessGame.TeamColor.WHITE : ChessGame.TeamColor.BLACK;

      game.setGameOver(true);
      game.setWinner(userColor == ChessGame.TeamColor.WHITE ? ChessGame.TeamColor.BLACK : ChessGame.TeamColor.WHITE);
      var newGameData = new GameData(gameData.gameID(), gameData.whiteUsername(), gameData.blackUsername(), gameData.gameName(), game);
      dataAccess.updateGame(newGameData.gameID(), newGameData);

      var notification = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION,
              authData.username() + " has resigned the game");
      connectionHandler.notification(notification, command.getGameID());

    } catch (ResponseException e) {
      ErrorMessage errorMessage =
              new ErrorMessage(ServerMessage.ServerMessageType.ERROR, e.getMessage());
      connectionHandler.errorMessage(errorMessage, session);    }
  }

  private void makeMove(Session session, MakeMoveCommand moveCommand) throws IOException {
    try {
      verifyAuthToken(moveCommand.getAuthToken());
      verifyGame(moveCommand.getGameID());
      var gameData = dataAccess.getGame(String.valueOf(moveCommand.getGameID()));
      var game = gameData.game();
      var authData = dataAccess.getAuth(moveCommand.getAuthToken());
      if (!(authData.username().equals(gameData.whiteUsername()) || authData.username().equals(gameData.blackUsername()))) {
        throw new ResponseException(500, "Unable to resign game as an observer");
      }
      var userColor =(Objects.equals(authData.username(), gameData.whiteUsername()))
              ? ChessGame.TeamColor.WHITE : ChessGame.TeamColor.BLACK;
      if (userColor != game.getTeamTurn()) {
        throw new ResponseException(500, "It is not your turn");
      }

      game.makeMove(moveCommand.getMove());
      var newGameData = new GameData(gameData.gameID(), gameData.whiteUsername(), gameData.blackUsername(), gameData.gameName(), game);
      dataAccess.updateGame(newGameData.gameID(), newGameData);
      var loadGameMessage = new LoadGameMessage(ServerMessage.ServerMessageType.LOAD_GAME, newGameData.game());
      connectionHandler.loadGame(loadGameMessage, Integer.valueOf(newGameData.gameID()));
      var moveMadeMessage = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION,
              authData.username() + " moved " + moveToString(moveCommand.getMove()));
      connectionHandler.notification(moveMadeMessage, moveCommand.getGameID(), authData.username());
    } catch (ResponseException e) {
      ErrorMessage errorMessage =
              new ErrorMessage(ServerMessage.ServerMessageType.ERROR, e.getMessage());
      connectionHandler.errorMessage(errorMessage, session);    }
    catch (InvalidMoveException e) {
      ErrorMessage errorMessage =
              new ErrorMessage(ServerMessage.ServerMessageType.ERROR, "Invalid move: To highlight valid moves type 'highlight' <START POSITION>");
      connectionHandler.errorMessage(errorMessage, session);
    }
  }

  private String moveToString(ChessMove move) {
    var start = getPositionFromInput(move.getStartPosition());
    var end = getPositionFromInput(move.getEndPosition());
    return " " + start + " to " + end;
  }

  private String getPositionFromInput(ChessPosition position) {
    StringBuilder result = new StringBuilder();
    result.append((char) (position.getColumn() + 'a' - 1));
    result.append(position.getRow());
    return result.toString();
  }

  private void connect(Session session, UserGameCommand command) throws IOException {
    try {
      verifyAuthToken(command.getAuthToken());
      verifyGame(command.getGameID());
      var authData= dataAccess.getAuth(command.getAuthToken());
      String message=String.format("%s has joined the game as %s", authData.username(), role);
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
      String message=String.format("%s has left the game.", user.username());
      connectionHandler.notification(new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, message),
              command.getGameID(), user.username());
      connectionHandler.remove(command.getGameID(), user.username());

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
      throw new ResponseException(401, "Game doesn't exist");
    }
    if (gameData.game().isGameOver()) {
      throw new ResponseException(401, "The game is over");
    }
  }

}
