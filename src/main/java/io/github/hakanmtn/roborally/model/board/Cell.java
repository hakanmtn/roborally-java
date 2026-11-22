package io.github.hakanmtn.roborally.model.board;

import io.github.hakanmtn.roborally.model.Position;
import io.github.hakanmtn.roborally.model.board.element.BoardElement;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Cell {

  private final Position position;
  private final List<BoardElement> elements = new ArrayList<>();
  private final String boardName;

  public Cell(Position position) {

    this(position, "unknown");
  }

  public Cell(Position position, String boardName) {
    this.position = Objects.requireNonNull(position, "Position must not be null");
    this.boardName = Objects.requireNonNull(boardName, "Board name must not be null");
  }

  public Position getPosition() {
    return position;
  }

  public List<BoardElement> getElements() {
    return List.copyOf(elements);
  }

  public void addElement(BoardElement element) {
    elements.add(Objects.requireNonNull(element, "Element must not be null"));
  }

  public String getBoardName() {
    return boardName;
  }
}
