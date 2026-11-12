package io.github.hakanmtn.roborally.model;

import io.github.hakanmtn.roborally.model.program.Program;

import java.util.Objects;

public class Player {
  private final Robot robot;
  private final Program program = new Program();

  public Player(Robot robot) {
    this.robot = Objects.requireNonNull(robot, "Robot must not be null");
    this.robot.assignOwner(this);
  }

  public Robot getRobot() {
    return robot;
  }

  public Program getProgram() {
    return program;
  }


}
