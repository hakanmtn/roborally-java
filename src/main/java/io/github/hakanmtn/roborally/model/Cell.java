package io.github.hakanmtn.roborally.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Cell {

    private final Position position;
    private final List<BoardElement> elements = new ArrayList<>();

    public Cell(Position position) {
        this.position = Objects.requireNonNull(position, "Position must not be null");
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
}
