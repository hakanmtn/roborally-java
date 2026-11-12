package io.github.hakanmtn.roborally.model.board.element;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import io.github.hakanmtn.roborally.model.Direction;
import org.junit.jupiter.api.Test;

class LaserTest {
  @Test
  void creatingLaserWithoutFiringDirectionThrowsException() {

    assertThrows(NullPointerException.class, () -> new Laser(null, 1));
  }

  @Test
  void laserRetainsItsGivenDirectionAndBeamCount() {

    Laser laser = new Laser(Direction.EAST, 2);

    assertEquals(Direction.EAST, laser.getFiringDirection());
    assertEquals(2, laser.getBeamCount());
  }

  @Test
  void creatingLaserWithZeroBeamsThrowsException() {
    assertThrows(IllegalArgumentException.class, () -> new Laser(Direction.EAST, 0));
  }

  @Test
  void creatingLaserWithNegativeBeamsThrowsException() {
    assertThrows(IllegalArgumentException.class, () -> new Laser(Direction.EAST, -2));
  }

  @Test
  void creatingLaserWithOneBeamIsAllowed() {
    Laser laser = new Laser(Direction.EAST, 1);

    assertEquals(1, laser.getBeamCount());
  }

}
