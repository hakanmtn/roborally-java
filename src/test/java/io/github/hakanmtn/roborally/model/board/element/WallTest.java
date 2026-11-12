package io.github.hakanmtn.roborally.model.board.element;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.HashSet;
import java.util.Set;

import io.github.hakanmtn.roborally.model.Direction;
import org.junit.jupiter.api.Test;

class WallTest {

  @Test
  void changingOriginalSetDoesNotChangeBlockedEdges() {
    Set<Direction> edges = new HashSet<>();
    edges.add(Direction.NORTH);
    Wall wall = new Wall(edges);

    edges.add(Direction.EAST);

    assertEquals(Set.of(Direction.NORTH), wall.getBlockedEdges());
  }

  @Test
  void blockedEdgesCannotBeModifiedThroughGetter() {
    Wall wall = new Wall(Set.of(Direction.NORTH));

    assertThrows(
        UnsupportedOperationException.class, () -> wall.getBlockedEdges().add(Direction.EAST));
  }

  @Test
  void wallRetainsAllGivenBlockedEdges() {
    Wall wall = new Wall(Set.of(Direction.NORTH, Direction.EAST));

    assertEquals(Set.of(Direction.NORTH, Direction.EAST), wall.getBlockedEdges());
  }

  @Test
  void creatingWallWithoutBlockedEdgesThrowsException() {
    assertThrows(IllegalArgumentException.class, () -> new Wall(Set.of()));
  }

  @Test
  void creatingWallWithNullBlockedEdgesThrowsException() {
    assertThrows(NullPointerException.class, () -> new Wall(null));
  }

  @Test
  void creatingWallWithNullEdgeThrowsException() {
    Set<Direction> edges = new HashSet<>();
    edges.add(null);
    assertThrows(NullPointerException.class, () -> new Wall(edges));
  }
}
