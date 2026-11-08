package io.github.hakanmtn.roborally.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DirectionTest {



    @Test
    void turningRightFromNorthReturnsEast(){
        assertEquals(Direction.EAST, Direction.NORTH.turnRight());
    }
}
