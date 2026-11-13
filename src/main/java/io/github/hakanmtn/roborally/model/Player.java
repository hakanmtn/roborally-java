package io.github.hakanmtn.roborally.model;

import io.github.hakanmtn.roborally.model.program.CardType;
import io.github.hakanmtn.roborally.model.program.Program;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Player {
  private final Robot robot;
  private final Program program = new Program();

  private final List<CardType> hand = new ArrayList<>();
  private final List<CardType> programmingDeck = new ArrayList<>();
  private final List<CardType> discardPile = new ArrayList<>();

  public Player(Robot robot) {
    this.robot = Objects.requireNonNull(robot, "Robot must not be null");
    this.robot.assignOwner(this);
  }

  public Robot getRobot() {
    return robot;
  }

  public Program getProgram() {
    return program;
  }

  public List<CardType> getHand() {
    return List.copyOf(hand);
  }

  public List<CardType> getProgrammingDeck() {
    return List.copyOf(programmingDeck);
  }

  public List<CardType> getDiscardPile() {
    return List.copyOf(discardPile);
  }
}
