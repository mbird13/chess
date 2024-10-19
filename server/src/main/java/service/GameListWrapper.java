package service;

import model.GameData;

import java.util.ArrayList;
import java.util.Collection;

public class GameListWrapper {

  private final Collection<GameListElement> games;

  public GameListWrapper(Collection<GameData> games) {
    this.games = new ArrayList<>();
    for (GameData game : games) {
      this.games.add(new GameListElement(game.gameID(), game.whiteUsername(), game.blackUsername(), game.gameName()));
    }
  }
}

record GameListElement(String gameID, String whiteUsername, String blackUsername, String gameName) {}
