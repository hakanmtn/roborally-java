package io.github.hakanmtn.roborally.model.board.element;

import io.github.hakanmtn.roborally.model.Direction;

import java.util.Objects;

public class Laser implements BoardElement {

    private final Direction firingDirection;
    private final int beamCount;

    public Laser(Direction firingDirection, int beamCount) {
        this.firingDirection = Objects.requireNonNull(firingDirection, "Firing direction must not be null");

        if(beamCount <= 0 ) {
            throw new IllegalArgumentException("Beam count must be positive");
        }
        this.beamCount = beamCount;
    }

    public Direction getFiringDirection() {
        return firingDirection;
    }

    public int getBeamCount() {
        return beamCount;
    }
}
