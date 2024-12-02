import chess.ChessGame;
import chess.ChessMove;
import com.google.gson.Gson;
import exception.ResponseException;
import servicehelpers.JoinGameRequest;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;

import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class WebSocketFacade extends Endpoint {

  private final Session session;
  private final NotificationHandler notificationHandler;

  public WebSocketFacade(String url, Client client) throws ResponseException {
    this.notificationHandler = new NotificationHandler(client);
    try {
      url=url.replace("http", "ws");
      URI socketURI=new URI(url + "/ws");
      //notification handler

      WebSocketContainer container = ContainerProvider.getWebSocketContainer();
      this.session = container.connectToServer(this, socketURI);

      this.session.addMessageHandler(new MessageHandler.Whole<String>() {
        @Override
        public void onMessage(String message) {
          notificationHandler.handleNotification(message);
        }
      });
    } catch (DeploymentException | IOException | URISyntaxException ex) {
        throw new ResponseException(500, ex.getMessage());
    }
  }

  @Override
  public void onOpen(Session session, EndpointConfig endpointConfig) {}

  public void joinGame(JoinGameRequest joinGameRequest) throws ResponseException {
    try {
      UserGameCommand command = new UserGameCommand(UserGameCommand.CommandType.CONNECT,
              joinGameRequest.authToken(), Integer.parseInt(joinGameRequest.gameID()));
      this.session.getBasicRemote().sendText(new Gson().toJson(command));
    } catch (Exception e) {
        throw new ResponseException(500, e.getMessage());
    }
  }

  public void leaveGame(String authToken, String currentGameId) throws ResponseException {
    try {
      UserGameCommand command = new UserGameCommand(UserGameCommand.CommandType.LEAVE,
              authToken, Integer.parseInt(currentGameId));
      this.session.getBasicRemote().sendText(new Gson().toJson(command));
    } catch (Exception e) {
      throw new ResponseException(500, e.getMessage());
    }
  }

  public void makeMove(String authToken, ChessMove move, String currentGameId) throws ResponseException {
    try {
      MakeMoveCommand command = new MakeMoveCommand(UserGameCommand.CommandType.MAKE_MOVE, authToken, Integer.parseInt(currentGameId), move);
      this.session.getBasicRemote().sendText(new Gson().toJson(command));
    } catch (IOException e) {
      throw new ResponseException(500, e.getMessage());
    }
  }

  public void resign(String authToken, String currentGameId) throws ResponseException {
    try {
      UserGameCommand command = new UserGameCommand(UserGameCommand.CommandType.RESIGN, authToken, Integer.parseInt(currentGameId));
      this.session.getBasicRemote().sendText(new Gson().toJson(command));
    } catch (IOException e) {
      throw new ResponseException(500, e.getMessage());
    }
  }
}
