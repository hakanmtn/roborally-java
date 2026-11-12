package io.github.hakanmtn.roborally.model.board.element;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CheckpointTest {

    @Test
    void checkpointRetainsItsGivenNumber() {
        Checkpoint checkPoint = new Checkpoint(2);

        assertEquals(2,  checkPoint.getNumber());
    }

    @Test
    void creatingCheckpointWithZeroNumberThrowsException() {

       assertThrows(IllegalArgumentException.class, () -> new Checkpoint(0));

    }

    @Test
    void creatingCheckpointWithNegativeNumberThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> new Checkpoint(-2));
    }

    @Test
    void creatingCheckpointWithNumberOneIsAllowed() {
        Checkpoint checkpoint = new Checkpoint(1);

        assertEquals(1, checkpoint.getNumber());
    }
}
