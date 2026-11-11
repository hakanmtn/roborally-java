package io.github.hakanmtn.roborally.model;

import java.util.Objects;

public class Robot {

    private Position position;
    private Direction facing;
    private Player owner;


    public Robot(Position position, Direction facing) {
        this.position = Objects.requireNonNull(position, "Position must not be null");
        this.facing = Objects.requireNonNull(facing, "Facing must not be null");
    }

    public void turnRight() {
        facing = facing.turnRight();
    }

    public Position getPosition() {
        return position;
    }

    public Direction getFacing() {
        return facing;
    }

    void assignOwner(Player player) {
        Objects.requireNonNull(player, "Owner must not be null");

        if(owner != null){
            throw new IllegalStateException("Robot already has an owner");
        }

        owner = player;
    }
}
