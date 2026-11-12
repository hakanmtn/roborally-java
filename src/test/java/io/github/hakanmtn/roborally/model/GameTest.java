package io.github.hakanmtn.roborally.model;

import static org.junit.jupiter.api.Assertions.*;

import io.github.hakanmtn.roborally.model.board.Board;
import org.junit.jupiter.api.Test;

public class GameTest {

  @Test
  void newGameHasNoPlayers() {
    Game game = new Game();

    assertTrue(game.getPlayers().isEmpty());
  }

  @Test
  void addingPlayerStoresItInGame() {
    Game game = new Game();
    Player player = new Player(new Robot(new Position(3, 4), Direction.NORTH));
    game.addPlayer(player);

    assertEquals(1, game.getPlayers().size());
    assertSame(player, game.getPlayers().getFirst());
  }

  @Test
  void addingNullPlayerThrowsException() {
    Game game = new Game();
    assertThrows(NullPointerException.class, () -> game.addPlayer(null));
  }

  @Test
  void addingSamePlayerTwiceThrowsException() {
    Game game = new Game();
    Player player = new Player(new Robot(new Position(3, 4), Direction.NORTH));

    game.addPlayer(player);

    assertThrows(IllegalArgumentException.class, () -> game.addPlayer(player));

    assertEquals(1, game.getPlayers().size());
    assertSame(player, game.getPlayers().getFirst());
  }

  @Test
  void addingAnotherPlayerPreservesTheExistingPlayer() {
    Game game = new Game();
    Player player = new Player(new Robot(new Position(3, 4), Direction.NORTH));
    Player player2 = new Player(new Robot(new Position(6, 4), Direction.NORTH));

    game.addPlayer(player);
    game.addPlayer(player2);

    assertEquals(2, game.getPlayers().size());
    assertSame(player, game.getPlayers().getFirst());
    assertSame(player2, game.getPlayers().get(1));
  }

  @Test
  void addingPlayerToOneGameLeavesTheOtherGameEmpty() {
    Game game = new Game();
    Game game2 = new Game();
    Player player = new Player(new Robot(new Position(3, 4), Direction.NORTH));
    game.addPlayer(player);

    assertEquals(1, game.getPlayers().size());
    assertSame(player, game.getPlayers().getFirst());
    assertTrue(game2.getPlayers().isEmpty());
  }

  @Test
  void playersReturnedByGameCannotBeModified() {
    Game game = new Game();
    Player player = new Player(new Robot(new Position(3, 4), Direction.NORTH));

    assertThrows(UnsupportedOperationException.class, () -> game.getPlayers().add(player));
  }

  @Test
  void newGameHasNoBoard() {
    Game game = new Game();

    assertNull(game.getBoard());
  }

  @Test
  void assigningBoardStoresItInGame() {
    Game game = new Game();
    Board board = new Board();

    game.setBoard(board);

    assertSame(board, game.getBoard());
  }

  @Test
  void assigningNullBoardThrowsExceptionAndPreserveExistingBoard() {
    Game game = new Game();
    Board board = new Board();
    game.setBoard(board);

    assertThrows(NullPointerException.class, () -> game.setBoard(null));

    assertSame(board, game.getBoard());
  }

  @Test
  void assigningBoardToOneGameLeavesTheOtherGameWithoutBoard() {

    Game game = new Game();
    Game game2 = new Game();
    Board board = new Board();
    game.setBoard(board);

    assertSame(board, game.getBoard());
    assertNull(game2.getBoard());
  }
}
