package servicehelpers;

import model.GameData;

import java.util.ArrayList;
import java.util.Collection;

public class GameListWrapper {

  public final ArrayList<GameListElement> games;

  public GameListWrapper(Collection<GameData> games) {
    this.games = new ArrayList<>();
    for (GameData game : games) {
      this.games.add(new GameListElement(game.gameID(), game.whiteUsername(), game.blackUsername(), game.gameName()));
    }
  }
}

