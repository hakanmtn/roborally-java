package io.github.hakanmtn.roborally.model.program;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class RegisterTest {

  @Test
  void newRegisterIsEmpty() {
    Register register = new Register();

    assertTrue(register.isEmpty());
  }

  @Test
  void settingACardMakesRegisterNonEmptyAndStoresThatCard() {
    Register register = new Register();

    register.setCard(CardType.MOVE_I);

    assertFalse(register.isEmpty());
    assertEquals(CardType.MOVE_I, register.getCard());
  }

  @Test
  void settingNullClearsAnOccupiedRegister() {
    Register register = new Register();
    register.setCard(CardType.MOVE_I);

    register.setCard(null);

    assertTrue(register.isEmpty());
    assertNull(register.getCard());
  }

  @Test
  void settingAnotherCardReplacesThePreviousCard() {
    Register register = new Register();
    register.setCard(CardType.MOVE_I);

    register.setCard(CardType.TURN_RIGHT);

    assertFalse(register.isEmpty());
    assertEquals(CardType.TURN_RIGHT, register.getCard());
  }
}
