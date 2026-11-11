package io.github.hakanmtn.roborally.model;

import java.util.Objects;

public class Player {
    private final Robot robot;

    public Player(Robot robot) {
        this.robot = Objects.requireNonNull(robot, "Robot must not be null");
        this.robot.assignOwner(this);
    }

    public Robot getRobot() {
        return robot;
    }
}
