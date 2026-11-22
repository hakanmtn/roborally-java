package io.github.hakanmtn.roborally.model.board.element;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import io.github.hakanmtn.roborally.model.Direction;
import java.util.Set;
import org.junit.jupiter.api.Test;

class PushPanelTest {
  @Test
  void pushPanelStoresDirectionAndActiveRegisters() {
    PushPanel panel = new PushPanel(Direction.WEST, Set.of(2, 4));

    assertEquals(Direction.WEST, panel.getDirection());
    assertEquals(Set.of(2, 4), panel.getActiveRegisters());
  }

  @Test
  void pushPanelRejectsRegisterZero() {

    assertThrows(IllegalArgumentException.class, () -> new PushPanel(Direction.WEST, Set.of(0, 2)));
  }

  @Test
  void pushPanelRejectsRegisterAboveFive() {
    assertThrows(
            IllegalArgumentException.class,
            () -> new PushPanel(Direction.WEST, Set.of(2, 6)));
  }

}
