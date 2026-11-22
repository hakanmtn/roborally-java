package io.github.hakanmtn.roborally.model.board.element;

public class EnergySpace implements BoardElement {

  private final int count;

  public EnergySpace(int count) {

    if (count < 0) throw new IllegalArgumentException("count cannot be negative");
    this.count = count;
  }

  public int getCount() {
    return count;
  }
}
