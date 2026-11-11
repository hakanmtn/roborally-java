package io.github.hakanmtn.roborally.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PlayerTest {

  @Test
  void playerKeepsTheAssignedRobot() {
    Robot robot = new Robot(new Position(2, 3), Direction.NORTH);

    Player player = new Player(robot);

    assertSame(robot, player.getRobot());
  }

  @Test
  void assigningTheSameRobotToAnotherPlayerThrowsException() {
    Robot robot = new Robot(new Position(2, 3), Direction.NORTH);
    Player playerA = new Player(robot);

    assertThrows(IllegalStateException.class, () -> new Player(robot));
    assertSame(robot, playerA.getRobot());
  }

  @Test
    void turningOnePlayersRobotDoesNotChangeTheOtherRobot(){
      Robot robotA = new Robot(new Position(2, 3), Direction.NORTH);
      Robot robotB = new Robot(new Position(2, 3), Direction.NORTH);

      Player playerA = new Player(robotA);
      Player playerB = new Player(robotB);

      playerA.getRobot().turnRight();

      assertEquals(Direction.EAST, playerA.getRobot().getFacing());
      assertEquals(Direction.NORTH, playerB.getRobot().getFacing());

  }
}
