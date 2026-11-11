package io.github.hakanmtn.roborally.model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class BoardTest {

  @Test
  void findsCellByEqualCoordinates() {
    Board board = new Board();

    Cell cell = new Cell(new Position(2, 3));

    board.addCell(cell);

    assertSame(cell, board.getCell(new Position(2, 3)));
  }

  @Test
  void addingCellAtOccupiedPositionThrowsException() {
    Board board = new Board();
    Cell originalCell = new Cell(new Position(2, 3));
    Cell otherCell = new Cell(new Position(2, 3));

    board.addCell(originalCell);

    assertThrows(IllegalArgumentException.class, () -> board.addCell(otherCell));

    assertSame(originalCell, board.getCell(new Position(2, 3)));
  }

  @Test
  void lookingUpMissingPositionReturnsNull() {

    Board board = new Board();

    assertNull(board.getCell(new Position(2, 3)));
  }

  @Test
  void findsEachCellAtItsOwnPosition() {
    Board board = new Board();
    Position position = new Position(2, 3);
    Position otherPosition = new Position(4, 5);
    Cell originalCell = new Cell(position);
    Cell otherCell = new Cell(otherPosition);

    board.addCell(originalCell);
    board.addCell(otherCell);

    assertSame(originalCell, board.getCell(position));
    assertSame(otherCell, board.getCell(otherPosition));
  }

  @Test
  void addingCellToOneBoardLeavesTheOtherBoardUnchanged() {

    Board boardA = new Board();
    Board boardB = new Board();

    Cell originalCell = new Cell(new Position(2, 3));

    boardA.addCell(originalCell);

    assertSame(originalCell, boardA.getCell(new Position(2, 3)));

    assertNull(boardB.getCell(new Position(2, 3)));
  }

    @Test
    void lookingUpNullPositionThrowsException() {
        Board board = new Board();

        assertThrows(
                NullPointerException.class,
                () -> board.getCell(null));
    }

    @Test
    void addingNullCellThrowsException() {
        Board board = new Board();
        assertThrows(NullPointerException.class, () -> board.addCell(null));
    }
}
