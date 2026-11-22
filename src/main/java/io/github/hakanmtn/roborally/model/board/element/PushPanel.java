package io.github.hakanmtn.roborally.model.board.element;

import io.github.hakanmtn.roborally.model.Direction;
import java.util.Objects;
import java.util.Set;

public class PushPanel implements BoardElement {

  private final Direction direction;
  private final Set<Integer> activeRegisters;

  public PushPanel(Direction direction, Set<Integer> activeRegisters) {

    this.direction = Objects.requireNonNull(direction, "Direction must not be null");

    Objects.requireNonNull(activeRegisters, "Active registers must not be null");

    if (activeRegisters.isEmpty()) {
      throw new IllegalArgumentException("Active registers must not be empty");
    }

    Set<Integer> registers = Set.copyOf(activeRegisters);

    if(registers.stream().anyMatch( number -> number < 1 || number > 5)){
      throw new IllegalArgumentException("Active registers must be between 1 and 5");
    }

    this.activeRegisters = registers;
  }

  public Direction getDirection() {
    return direction;
  }

  public Set<Integer> getActiveRegisters() {
    return activeRegisters;
  }
}
