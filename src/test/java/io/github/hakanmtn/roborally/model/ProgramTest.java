package io.github.hakanmtn.roborally.model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class ProgramTest {

  @Test
  void placingACardInOneRegisterLeavesTheOthersEmpty() {
    Program program = new Program();

    program.getRegister(0).setCard(CardType.MOVE_I);

    assertEquals(CardType.MOVE_I, program.getRegister(0).getCard());

    for (int index = 1; index < 5; index++) {
      assertTrue(program.getRegister(index).isEmpty());
    }
  }

  @Test
  void placingACardInOneProgramLeavesTheOtherProgramEmpty() {
    Program programA = new Program();
    Program programB = new Program();

    programA.getRegister(0).setCard(CardType.MOVE_I);

    assertEquals(CardType.MOVE_I, programA.getRegister(0).getCard());
    for (int index = 0; index < 5; index++) {
      assertTrue(programB.getRegister(index).isEmpty());
    }
  }

  @Test
  void accessingRegisterBeyondProgramSizeThrowsException() {
    Program program = new Program();

    assertThrows(IndexOutOfBoundsException.class, () -> program.getRegister(5));
  }

  @Test
  void accessingRegisterWithNegativeIndexThrowsException() {
    Program program = new Program();

    assertThrows(IndexOutOfBoundsException.class, () -> program.getRegister(-1));
  }
}
