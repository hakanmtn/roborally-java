package io.github.hakanmtn.roborally.model.board.element;

import io.github.hakanmtn.roborally.model.Direction;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RestartPointTest {

    @Test
    void restartPointStoresDirection(){
    RestartPoint restartPoint = new RestartPoint(Direction.EAST);

    assertEquals(Direction.EAST, restartPoint.getDirection());
    }


}
