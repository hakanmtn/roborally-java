package io.github.hakanmtn.roborally.model.board.element;

import io.github.hakanmtn.roborally.model.Direction;

import java.util.Objects;
import java.util.Set;

public class Wall implements BoardElement {

  private final Set<Direction> blockedEdges;

  public Wall(Set<Direction> blockedEdges) {
    Objects.requireNonNull(blockedEdges, "Blocked edges must not be null");

    if (blockedEdges.isEmpty())
      throw new IllegalArgumentException("Wall must block at least one edge");
    this.blockedEdges = Set.copyOf(blockedEdges);
  }

  public Set<Direction> getBlockedEdges() {
    return blockedEdges;
  }
}
