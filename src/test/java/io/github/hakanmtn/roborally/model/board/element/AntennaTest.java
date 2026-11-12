package io.github.hakanmtn.roborally.model.board.element;

import io.github.hakanmtn.roborally.model.Direction;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class AntennaTest {

    @Test
    void creatingAntennaWithoutFacingThrowsException() {
        assertThrows(NullPointerException.class, () -> new Antenna(null));
    }

    @Test
    void antennaRetainsItsGivenFacing(){

        Antenna antenna = new Antenna(Direction.EAST);

        assertEquals(Direction.EAST, antenna.getFacing());

    }
}
