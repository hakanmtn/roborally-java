package io.github.hakanmtn.roborally.model;

import io.github.hakanmtn.roborally.model.board.Board;
import io.github.hakanmtn.roborally.model.card.DamageCardType;

import java.util.*;

public class Game {

  private final List<Player> players = new ArrayList<>();
  private Board board;
  private final Map<DamageCardType,Integer> damageSupply = new EnumMap<>(DamageCardType.class);

  public List<Player> getPlayers() {
    return List.copyOf(players);
  }

  public void addPlayer(Player player) {
    Objects.requireNonNull(player, "Player must not be null");

    if (players.contains(player))
      throw new IllegalArgumentException("Player already exists in the game");

    players.add(player);
  }

  public Board getBoard() {
    return board;
  }

  public void setBoard(Board board) {
    Objects.requireNonNull(board, "Board must not be null");
    this.board = board;
  }

  public Map<DamageCardType,Integer> getDamageSupply() {
    return Map.copyOf(damageSupply);
  }
}
