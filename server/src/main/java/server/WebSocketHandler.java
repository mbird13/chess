package server;

import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;

import java.io.IOException;

@WebSocket
public class WebSocketHandler {

  @OnWebSocketMessage
  private void onMessage(Session session,String message) throws IOException {
    UserGameCommand command = new Gson().fromJson(message, UserGameCommand.class);
    if (command.getCommandType() == UserGameCommand.CommandType.MAKE_MOVE) {
      MakeMoveCommand moveCommand = new Gson().fromJson(message, MakeMoveCommand.class);
    }
  }


}
