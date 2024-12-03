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
      verifyGameOver(command.getGameID());
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
              authData.username() + " (" + userColor + ") has resigned the game");
      connectionHandler.notification(notification, command.getGameID());

    } catch (ResponseException e) {
      ErrorMessage errorMessage =
              new ErrorMessage(ServerMessage.ServerMessageType.ERROR, e.getMessage());
      connectionHandler.errorMessage(errorMessage, session);    }
  }

  private void makeMove(Session session, MakeMoveCommand moveCommand) throws IOException {
    try {
      verifyAuthToken(moveCommand.getAuthToken());
      verifyGameOver(moveCommand.getGameID());
      var gameData = dataAccess.getGame(String.valueOf(moveCommand.getGameID()));
      var game = gameData.game();
      var authData = dataAccess.getAuth(moveCommand.getAuthToken());
      if (!(authData.username().equals(gameData.whiteUsername()) || authData.username().equals(gameData.blackUsername()))) {
        throw new ResponseException(500, "Unable to make move as an observer");
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

      checkGameStatus(newGameData, userColor);

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

  private void checkGameStatus(GameData game, ChessGame.TeamColor userColor) throws ResponseException, IOException {
    if (game.game().isInStalemate(userColor)) {
      game.game().setGameOver(true);
      dataAccess.updateGame(game.gameID(), game);
      var stalemateMessage = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION,
              "Stalemate: the game ended in a draw");
      connectionHandler.notification(stalemateMessage, Integer.valueOf(game.gameID()));
      return;
    }
    if (game.game().isInCheckmate(ChessGame.TeamColor.WHITE)) {
      game.game().setGameOver(true);
      game.game().setWinner(ChessGame.TeamColor.BLACK);
      dataAccess.updateGame(game.gameID(), game);
      var checkMateMessage = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION,
              "Checkmate: Black has won the game");
      connectionHandler.notification(checkMateMessage, Integer.valueOf(game.gameID()));
      return;
    }
    if (game.game().isInCheckmate(ChessGame.TeamColor.BLACK)) {
      game.game().setGameOver(true);
      game.game().setWinner(ChessGame.TeamColor.WHITE);
      dataAccess.updateGame(game.gameID(), game);
      var checkMateMessage = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION,
              "Checkmate: White has won the game");
      connectionHandler.notification(checkMateMessage, Integer.valueOf(game.gameID()));
      return;
    }
    if (game.game().isInCheck(ChessGame.TeamColor.WHITE)) {
      var checkMessage = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION,
              "White is in check");
      connectionHandler.notification(checkMessage, Integer.valueOf(game.gameID()));
      return;
    }
    if (game.game().isInCheck(ChessGame.TeamColor.BLACK)) {
      var checkMessage = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION,
              "Black is in check");
      connectionHandler.notification(checkMessage, Integer.valueOf(game.gameID()));
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
      var gameData = dataAccess.getGame(String.valueOf(command.getGameID()));
      String role = getPlayerRole(gameData, authData);

      String message=String.format("%s has joined the game as %s", authData.username(), role);
      connectionHandler.notification(new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, message), command.getGameID());
      connectionHandler.add(command.getGameID(), authData.username(), session);

      var loadGameMessage = new LoadGameMessage(ServerMessage.ServerMessageType.LOAD_GAME, gameData.game());
      connectionHandler.loadGame(loadGameMessage, session);
    } catch (ResponseException e) {
      ErrorMessage errorMessage =
              new ErrorMessage(ServerMessage.ServerMessageType.ERROR, e.getMessage());
      connectionHandler.errorMessage(errorMessage, session);
    } catch (IOException e) {
      ErrorMessage errorMessage =
              new ErrorMessage(ServerMessage.ServerMessageType.ERROR, "Error: unable to join game");
      connectionHandler.errorMessage(errorMessage, session);
    }
  }

  private String getPlayerRole(GameData gameData, AuthData authData) {
    if (authData.username().equals(gameData.whiteUsername())) {
      return "white";
    }
    if (authData.username().equals(gameData.blackUsername())) {
      return "black";
    }
    return "observer";
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
      GameData newGameData = oldGameData;
      if (Objects.equals(oldGameData.whiteUsername(), user.username())) {
        newGameData = new GameData(oldGameData.gameID(),
                null, oldGameData.blackUsername(), oldGameData.gameName(), oldGameData.game());
      } else if (Objects.equals(oldGameData.blackUsername(), user.username())) {
        newGameData = new GameData(oldGameData.gameID(),
                oldGameData.whiteUsername(), null, oldGameData.gameName(), oldGameData.game());
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
  }

  private void verifyGameOver(Integer gameID) throws ResponseException {
    GameData gameData = dataAccess.getGame(String.valueOf(gameID));
    if (gameData == null) {
      throw new ResponseException(401, "Game doesn't exist");
    }
    if (gameData.game().isGameOver()) {
      throw new ResponseException(401, "The game is over");
    }
  }

}
