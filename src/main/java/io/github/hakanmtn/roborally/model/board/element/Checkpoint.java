package io.github.hakanmtn.roborally.model.board.element;

public class Checkpoint implements BoardElement {

    private final int number;

    public Checkpoint(int number) {
        if(number <= 0 ) throw new IllegalArgumentException("number must be positive");
        this.number = number;
    }

    public int getNumber() {
        return number;
    }
}
