package servicehelpers;

import chess.ChessGame;

public record JoinGameRequest(String authToken, ChessGame.TeamColor playerColor, String gameID) {
}
