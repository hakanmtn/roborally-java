package io.github.hakanmtn.roborally.model.board.element;

import io.github.hakanmtn.roborally.model.Direction;

import java.util.Objects;

public class Antenna implements BoardElement {

    private final Direction facing;

    public Antenna(Direction facing) {
    this.facing = Objects.requireNonNull(facing, "Facing must not be null");
  }

  public Direction getFacing() {
        return facing;
  }
}
