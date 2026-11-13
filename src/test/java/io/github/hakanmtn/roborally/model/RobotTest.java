package io.github.hakanmtn.roborally.model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class RobotTest {

  @Test
  void fourRightTurnsRestoreFacingAndPreservePosition() {
    Position startingPosition = new Position(2, 3);
    Robot robot = new Robot(startingPosition, Direction.NORTH);

    for (int turn = 0; turn < 4; turn++) {
      robot.turnRight();
    }

    assertEquals(Direction.NORTH, robot.getFacing());
    assertEquals(startingPosition, robot.getPosition());
  }

  @Test
  void turningRightFromNorthFacesEastAndPreservePosition() {
    Position startingPosition = new Position(2, 3);

    Robot robot = new Robot(startingPosition, Direction.NORTH);

    robot.turnRight();

    assertEquals(Direction.EAST, robot.getFacing());
    assertEquals(startingPosition, robot.getPosition());
  }

  @Test
  void creatingRobotWithNullFacingThrowsException() {
    assertThrows(NullPointerException.class, () -> new Robot(new Position(2, 3), null));
  }

  @Test
  void creatingRobotWithNullPositionThrowsException() {
    assertThrows(NullPointerException.class, () -> new Robot(null, Direction.NORTH));
  }

  @Test
  void newRobotHasNoCompletedCheckPoints() {
    Position startingPosition = new Position(2, 3);
    Robot robot = new Robot(startingPosition, Direction.NORTH);

    assertEquals(0, robot.getLastCompletedCheckpoint());
  }

  @Test
  void newRobotHasNotRebootedThisRound() {

    Position startingPosition = new Position(2, 3);
    Robot robot = new Robot(startingPosition, Direction.NORTH);

    assertFalse(robot.hasRebootedThisRound());
  }
}
