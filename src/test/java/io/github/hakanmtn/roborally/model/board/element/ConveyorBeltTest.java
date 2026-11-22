package io.github.hakanmtn.roborally.model.board.element;

import io.github.hakanmtn.roborally.model.Direction;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ConveyorBeltTest {

    @Test
    void conveyorBeltStoresSpeedAndDirections() {
    ConveyorBelt conveyorBelt = new ConveyorBelt(2, Direction.NORTH, Set.of(Direction.SOUTH, Direction.EAST));

    assertEquals(2, conveyorBelt.getSpeed() );
    assertEquals(Direction.NORTH, conveyorBelt.getExitDirection());
    assertEquals(Set.of(Direction.SOUTH, Direction.EAST), conveyorBelt.getEntryDirections());
    }

    @Test
    void conveyorBeltRejectsSpeedAboveTwo(){
        assertThrows(IllegalArgumentException.class, () -> new ConveyorBelt(3,  Direction.NORTH, Set.of(Direction.SOUTH) ));
    }

    @Test
    void conveyorBeltRejectsSpeedZero() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new ConveyorBelt(
                        0, Direction.NORTH, Set.of(Direction.SOUTH)));
    }

    @Test
    void conveyorBeltRejectsEmptyEntryDirections(){
        assertThrows(IllegalArgumentException.class, () -> new ConveyorBelt(1, Direction.EAST, Set.of()));
    }

}
