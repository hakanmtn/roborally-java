package io.github.hakanmtn.roborally.model.board.element;

import io.github.hakanmtn.roborally.model.RotationDirection;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class GearTest {

    @Test
    void creatingGearWithoutRotationDirectionThrowsException() {
        assertThrows(NullPointerException.class, () -> new Gear(null));
    }

    @Test
    void gearRetainsItsGivenRotationDirection() {
        Gear gear = new Gear(RotationDirection.COUNTERCLOCKWISE);

        assertEquals(RotationDirection.COUNTERCLOCKWISE, gear.getRotationDirection());
    }

    @Test
    void gearCanRotateClockwise() {
        Gear gear = new Gear(RotationDirection.CLOCKWISE);

        assertEquals(
                RotationDirection.CLOCKWISE,
                gear.getRotationDirection());
    }
}
