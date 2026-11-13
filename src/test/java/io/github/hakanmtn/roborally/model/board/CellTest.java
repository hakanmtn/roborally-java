package io.github.hakanmtn.roborally.model.board;

import static org.junit.jupiter.api.Assertions.*;

import io.github.hakanmtn.roborally.model.Position;
import io.github.hakanmtn.roborally.model.board.element.BoardElement;
import org.junit.jupiter.api.Test;

class CellTest {
  @Test
  void creatingCellWithoutPositionThrowsException() {
    assertThrows(NullPointerException.class, () -> new Cell(null));
  }

  @Test
  void cellRetainsItsGivenPosition() {

    Position position = new Position(2, 3);
    Cell cell = new Cell(position);

    assertEquals(position, cell.getPosition());
  }

  @Test
  void newCellHasNoElements() {
    Position position = new Position(2, 3);
    Cell cell = new Cell(position);

    assertTrue(cell.getElements().isEmpty());
  }

  @Test
  void addingNullElementThrowsException() {
    Cell cell = new Cell(new Position(2, 3));

    assertThrows(NullPointerException.class, () -> cell.addElement(null));
  }

  @Test
  void addingElementStoresItInCell() {
    Cell cell = new Cell(new Position(2, 3));
    BoardElement element = new BoardElement() {};

    cell.addElement(element);

    assertEquals(1, cell.getElements().size());
    assertSame(element, cell.getElements().getFirst());
  }

  @Test
  void addingAnotherElementPreservesTheExistingElement() {

    Cell cell = new Cell(new Position(2, 3));
    BoardElement element = new BoardElement() {};
    BoardElement otherElement = new BoardElement() {};

    cell.addElement(element);
    cell.addElement(otherElement);

    assertEquals(2, cell.getElements().size());
    assertSame(element, cell.getElements().getFirst());
    assertSame(otherElement, cell.getElements().get(1));
  }

  @Test
  void addingElementToOneCellLeavesTheOtherCellEmpty() {

    Cell cell = new Cell(new Position(2, 3));
    Cell otherCell = new Cell(new Position(4, 5));
    BoardElement element = new BoardElement() {};

    cell.addElement(element);

    assertEquals(1, cell.getElements().size());
    assertSame(element, cell.getElements().getFirst());
    assertTrue(otherCell.getElements().isEmpty());
  }

  @Test
  void elementsReturnedByCellCannotBeModified() {

    Cell cell = new Cell(new Position(2, 3));
    BoardElement element = new BoardElement() {};

    assertThrows(UnsupportedOperationException.class, () -> cell.getElements().add(element));
  }
}
