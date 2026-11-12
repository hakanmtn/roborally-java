package io.github.hakanmtn.roborally.model;

import static org.junit.jupiter.api.Assertions.*;

import io.github.hakanmtn.roborally.model.program.CardType;
import org.junit.jupiter.api.Test;

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
  void turningOnePlayersRobotDoesNotChangeTheOtherRobot() {
    Robot robotA = new Robot(new Position(2, 3), Direction.NORTH);
    Robot robotB = new Robot(new Position(2, 3), Direction.NORTH);

    Player playerA = new Player(robotA);
    Player playerB = new Player(robotB);

    playerA.getRobot().turnRight();

    assertEquals(Direction.EAST, playerA.getRobot().getFacing());
    assertEquals(Direction.NORTH, playerB.getRobot().getFacing());
  }

  @Test
  void placingACardInOnePlayersProgramLeavesTheOtherPlayersProgramEmpty() {

    Player playerA = new Player(new Robot(new Position(0, 0), Direction.NORTH));
    Player playerB = new Player(new Robot(new Position(1, 0), Direction.NORTH));

    playerA.getProgram().getRegister(0).setCard(CardType.MOVE_I);

    assertEquals(CardType.MOVE_I, playerA.getProgram().getRegister(0).getCard());

    for(int index = 0; index < 5 ; index++) {
      assertTrue(playerB.getProgram().getRegister(index).isEmpty());
    }

  }
}
