package io.github.hakanmtn.roborally.model.board.element;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class EnergySpaceTest {

  @Test
  void energySpaceStoresEnergyCount() {
    EnergySpace energySpace = new EnergySpace(1);

    assertEquals(1, energySpace.getCount());
  }

  @Test
  void energySpaceRejectsNegativeCount() {

    assertThrows(IllegalArgumentException.class, () -> new EnergySpace(-1));
  }

  @Test
  void energySpaceCanHaveZeroEnergy() {
    EnergySpace energySpace = new EnergySpace(0);

    assertEquals(0, energySpace.getCount());
  }
}
