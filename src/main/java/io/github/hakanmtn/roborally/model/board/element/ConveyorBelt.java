package io.github.hakanmtn.roborally.model.board.element;

import io.github.hakanmtn.roborally.model.Direction;
import java.util.Objects;
import java.util.Set;

public class ConveyorBelt implements BoardElement {

  private final int speed;
  private final Direction exitDirection;
  private final Set<Direction> entryDirections;

  public ConveyorBelt(int speed, Direction exitDirection, Set<Direction> entryDirections) {

    if (speed < 1 || speed > 2) {
      throw new IllegalArgumentException("Speed must be between 1 and 2");
    }
    this.speed = speed;
    this.exitDirection = Objects.requireNonNull(exitDirection, "Exit direction must not be null");

    Objects.requireNonNull(entryDirections, "Entry Directions must not be null");

    if (entryDirections.isEmpty()) {
      throw new IllegalArgumentException("Entry Directions must not be empty");
    }
    this.entryDirections = Set.copyOf(entryDirections);
  }

  public int getSpeed() {
    return speed;
  }

  public Direction getExitDirection() {
    return exitDirection;
  }

  public Set<Direction> getEntryDirections() {
    return entryDirections;
  }
}
