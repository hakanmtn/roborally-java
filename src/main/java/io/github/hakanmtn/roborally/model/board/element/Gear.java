package io.github.hakanmtn.roborally.model.board.element;

import io.github.hakanmtn.roborally.model.RotationDirection;

import java.util.Objects;

public class Gear implements BoardElement {

    private final RotationDirection rotationDirection;

    public Gear(RotationDirection rotationDirection) {
        this.rotationDirection = Objects.requireNonNull(rotationDirection, "Rotation direction must not be null");
    }

    public RotationDirection getRotationDirection() {
        return rotationDirection;
    }
}
