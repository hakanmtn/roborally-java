package io.github.hakanmtn.roborally.model.board.element;

import io.github.hakanmtn.roborally.model.Direction;

import java.util.Objects;

public class RestartPoint implements BoardElement {

    private final Direction direction;

    public RestartPoint(Direction direction) {
        this.direction = Objects.requireNonNull(direction, "Restart direction must not be null");
    }

    public Direction getDirection() {
        return direction;
    }
}
