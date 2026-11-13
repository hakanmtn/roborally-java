package io.github.hakanmtn.roborally.model.board;

import io.github.hakanmtn.roborally.model.Position;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Board {

  private final Map<Position, Cell> cells = new HashMap<>();

  public void addCell(Cell cell) {
    Objects.requireNonNull(cell, " Cell must not be null");
    Position position = cell.getPosition();
    if (cells.containsKey(position)) {
      throw new IllegalArgumentException("A cell already exists at position: " + position);
    }

    cells.put(position, cell);
  }

  public Cell getCell(Position position) {
    Objects.requireNonNull(position, "Position must not be null");
    return cells.get(position);
  }
}
