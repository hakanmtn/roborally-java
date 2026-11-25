package io.github.hakanmtn.roborally.protocol;

import static org.junit.jupiter.api.Assertions.*;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import io.github.hakanmtn.roborally.model.Direction;
import io.github.hakanmtn.roborally.model.Position;
import io.github.hakanmtn.roborally.model.RotationDirection;
import io.github.hakanmtn.roborally.model.board.Board;
import io.github.hakanmtn.roborally.model.board.Cell;
import io.github.hakanmtn.roborally.model.board.element.*;
import java.util.Set;
import org.junit.jupiter.api.Test;

class BoardMapperTest {

  @Test
  void mapsWallWithBoardNameAndOrientation() {
    Cell cell = new Cell(new Position(2, 3), "4A");
    cell.addElement(new Wall(Set.of(Direction.NORTH)));

    BoardMapper mapper = new BoardMapper();
    JsonArray elements = mapper.mapCell(cell);

    assertEquals(1, elements.size());

    JsonObject wall = elements.get(0).getAsJsonObject();
    assertEquals("Wall", wall.get("type").getAsString());
    assertEquals("4A", wall.get("isOnBoard").getAsString());

    JsonArray orientations = wall.getAsJsonArray("orientations");
    assertEquals(1, orientations.size());
    assertEquals("top", orientations.get(0).getAsString());
  }

  @Test
  void mapsWallAndLaserInTheSameCell() {
    Cell cell = new Cell(new Position(2, 3), "4A");
    cell.addElement(new Wall(Set.of(Direction.NORTH)));
    cell.addElement(new Laser(Direction.SOUTH, 2));

    BoardMapper mapper = new BoardMapper();
    JsonArray elements = mapper.mapCell(cell);

    assertEquals(2, elements.size());

    JsonObject wall = elements.get(0).getAsJsonObject();
    assertEquals("Wall", wall.get("type").getAsString());

    JsonObject laser = elements.get(1).getAsJsonObject();
    assertEquals("Laser", laser.get("type").getAsString());
    assertEquals("4A", laser.get("isOnBoard").getAsString());
    assertEquals(2, laser.get("count").getAsInt());

    JsonArray orientations = laser.getAsJsonArray("orientations");
    assertEquals(1, orientations.size());
    assertEquals("bottom", orientations.get(0).getAsString());
  }

  @Test
  void mapsCellWithoutElementsAsEmptyField() {
    Cell cell = new Cell(new Position(2, 3), "4A");

    BoardMapper mapper = new BoardMapper();
    JsonArray elements = mapper.mapCell(cell);

    assertEquals(1, elements.size());

    JsonObject empty = elements.get(0).getAsJsonObject();
    assertEquals("Empty", empty.get("type").getAsString());
    assertEquals("4A", empty.get("isOnBoard").getAsString());
    assertFalse(empty.has("orientations"));
  }

  @Test
  void mapsAllBlockedEdgesOfWall() {
    Cell cell = new Cell(new Position(2, 3), "4A");
    cell.addElement(new Wall(Set.of(Direction.NORTH, Direction.EAST)));

    JsonArray elements = new BoardMapper().mapCell(cell);
    JsonObject wall = elements.get(0).getAsJsonObject();
    JsonArray orientations = wall.getAsJsonArray("orientations");

    assertEquals(2, orientations.size());
    assertTrue(orientations.contains(new JsonPrimitive("top")));
    assertTrue(orientations.contains(new JsonPrimitive("right")));
  }

  @Test
  void mapsStartPointWithBoardName() {
    Cell cell = new Cell(new Position(0, 0), "4A");
    cell.addElement(new StartPoint());

    JsonArray elements = new BoardMapper().mapCell(cell);

    assertEquals(1, elements.size());

    JsonObject startPoint = elements.get(0).getAsJsonObject();
    assertEquals("StartPoint", startPoint.get("type").getAsString());
    assertEquals("4A", startPoint.get("isOnBoard").getAsString());
    assertFalse(startPoint.has("orientations"));
  }

  @Test
  void mappingStartPointPreservesFollowingElements() {
    Cell cell = new Cell(new Position(0, 0), "4A");
    cell.addElement(new StartPoint());
    cell.addElement(new Wall(Set.of(Direction.NORTH)));

    JsonArray elements = new BoardMapper().mapCell(cell);

    assertEquals(2, elements.size());
    assertEquals("StartPoint", elements.get(0).getAsJsonObject().get("type").getAsString());
    assertEquals("Wall", elements.get(1).getAsJsonObject().get("type").getAsString());
  }

  @Test
  void mapsRestartPointWithBoardNameAndDirection() {
    Cell cell = new Cell(new Position(2, 3), "4A");
    cell.addElement(new RestartPoint(Direction.EAST));

    JsonArray elements = new BoardMapper().mapCell(cell);
    assertEquals(1, elements.size());
    JsonObject restartPoint = elements.get(0).getAsJsonObject();
    assertEquals("RestartPoint", restartPoint.get("type").getAsString());
    assertEquals("4A", restartPoint.get("isOnBoard").getAsString());

    JsonArray orientations = restartPoint.getAsJsonArray("orientations");
    assertEquals(1, orientations.size());
    assertEquals("right", orientations.get(0).getAsString());
  }

  @Test
  void mapsCheckpointWithBoardNameAndNumber() {
    Cell cell = new Cell(new Position(2, 3), "4A");
    cell.addElement(new Checkpoint(2));

    JsonArray elements = new BoardMapper().mapCell(cell);

    assertEquals(1, elements.size());

    JsonObject checkpoint = elements.get(0).getAsJsonObject();
    assertEquals("CheckPoint", checkpoint.get("type").getAsString());
    assertEquals("4A", checkpoint.get("isOnBoard").getAsString());
    assertEquals(2, checkpoint.get("count").getAsInt());
    assertFalse(checkpoint.has("orientations"));
  }

  @Test
  void mapsClockwiseGearWithBoardNameAndRotation() {
    Cell cell = new Cell(new Position(2, 3), "4A");
    cell.addElement(new Gear(RotationDirection.CLOCKWISE));

    JsonArray elements = new BoardMapper().mapCell(cell);

    assertEquals(1, elements.size());

    JsonObject gear = elements.get(0).getAsJsonObject();
    assertEquals("Gear", gear.get("type").getAsString());
    assertEquals("4A", gear.get("isOnBoard").getAsString());

    JsonArray orientations = gear.getAsJsonArray("orientations");
    assertEquals(1, orientations.size());
    assertEquals("clockwise", orientations.get(0).getAsString());
  }

  @Test
  void mapsCounterclockwiseGearRotation() {
    Cell cell = new Cell(new Position(2, 3), "4A");
    cell.addElement(new Gear(RotationDirection.COUNTERCLOCKWISE));

    JsonArray elements = new BoardMapper().mapCell(cell);
    JsonObject gear = elements.get(0).getAsJsonObject();

    JsonArray orientations = gear.getAsJsonArray("orientations");
    assertEquals(1, orientations.size());
    assertEquals("counterclockwise", orientations.get(0).getAsString());
  }

  @Test
  void mapsAntennaWithBoardNameAndFacing() {
    Cell cell = new Cell(new Position(2, 3), "4A");
    cell.addElement(new Antenna(Direction.WEST));

    JsonArray elements = new BoardMapper().mapCell(cell);

    assertEquals(1, elements.size());

    JsonObject antenna = elements.get(0).getAsJsonObject();
    assertEquals("Antenna", antenna.get("type").getAsString());
    assertEquals("4A", antenna.get("isOnBoard").getAsString());

    JsonArray orientations = antenna.getAsJsonArray("orientations");
    assertEquals(1, orientations.size());
    assertEquals("left", orientations.get(0).getAsString());
  }

  @Test
  void mapsPitWithBoardName() {
    Cell cell = new Cell(new Position(2, 3), "4A");
    cell.addElement(new Pit());

    JsonArray elements = new BoardMapper().mapCell(cell);
    assertEquals(1, elements.size());
    JsonObject pit = elements.get(0).getAsJsonObject();
    assertEquals("Pit", pit.get("type").getAsString());
    assertEquals("4A", pit.get("isOnBoard").getAsString());
    assertFalse(pit.has("orientations"));
  }

  @Test
  void mapsEnergySpaceWithBoardNameAndCount() {
    Cell cell = new Cell(new Position(2, 3), "4A");
    cell.addElement(new EnergySpace(1));

    JsonArray elements = new BoardMapper().mapCell(cell);
    assertEquals(1, elements.size());

    JsonObject energy = elements.get(0).getAsJsonObject();
    assertEquals("EnergySpace", energy.get("type").getAsString());
    assertEquals("4A", energy.get("isOnBoard").getAsString());

    JsonPrimitive count = energy.get("count").getAsJsonPrimitive();
    assertTrue(count.isNumber());
    assertEquals(1, count.getAsInt());

    assertFalse(energy.has("orientations"));
  }

  @Test
  void mapsPushPanelWithDirectionAndActiveRegisters() {
    Cell cell = new Cell(new Position(2, 3), "4A");
    cell.addElement(new PushPanel(Direction.WEST, Set.of(2, 4)));

    JsonArray elements = new BoardMapper().mapCell(cell);
    assertEquals(1, elements.size());

    JsonObject pushPanel = elements.get(0).getAsJsonObject();
    assertEquals("PushPanel", pushPanel.get("type").getAsString());
    assertEquals("4A", pushPanel.get("isOnBoard").getAsString());

    JsonArray orientations = pushPanel.getAsJsonArray("orientations");
    assertEquals(1, orientations.size());
    assertEquals("left", orientations.get(0).getAsString());

    JsonArray registers = pushPanel.getAsJsonArray("registers");
    assertEquals(2, registers.size());
    assertTrue(registers.contains(new JsonPrimitive(2)));
    assertTrue(registers.contains(new JsonPrimitive(4)));
  }

  @Test
  void mapsConveyorBeltWithExitDirectionFirst() {
    Cell cell = new Cell(new Position(2, 3), "4A");
    cell.addElement(new ConveyorBelt(2, Direction.NORTH, Set.of(Direction.SOUTH, Direction.EAST)));

    JsonArray elements = new BoardMapper().mapCell(cell);
    assertEquals(1, elements.size());

    JsonObject conveyorBelt = elements.get(0).getAsJsonObject();
    assertEquals("ConveyorBelt", conveyorBelt.get("type").getAsString());
    assertEquals("4A", conveyorBelt.get("isOnBoard").getAsString());
    assertEquals(2, conveyorBelt.get("speed").getAsInt());

    JsonArray orientations = conveyorBelt.getAsJsonArray("orientations");
    assertEquals(3, orientations.size());
    assertEquals("top", orientations.get(0).getAsString());
    assertTrue(orientations.contains(new JsonPrimitive("bottom")));
    assertTrue(orientations.contains(new JsonPrimitive("right")));
  }

  @Test
  void mapsSingleCellBoardIntoCoordinateArrays() {
    Board board = new Board();
    Cell cell = new Cell(new Position(0, 0), "4A");
    cell.addElement(new StartPoint());
    board.addCell(cell);

    JsonArray gameMap = new BoardMapper().mapBoard(board, 1, 1);

    assertEquals(1, gameMap.size());

    JsonArray column = gameMap.get(0).getAsJsonArray();
    assertEquals(1, column.size());

    JsonArray elements = column.get(0).getAsJsonArray();
    assertEquals(1, elements.size());

    JsonObject startPoint = elements.get(0).getAsJsonObject();
    assertEquals("StartPoint", startPoint.get("type").getAsString());
    assertEquals("4A", startPoint.get("isOnBoard").getAsString());
  }

  @Test
  void mapsMissingCellAsNullElement() {
    Board board = new Board();

    JsonArray gameMap = new BoardMapper().mapBoard(board, 1, 1);

    assertEquals(1, gameMap.size());

    JsonArray column = gameMap.get(0).getAsJsonArray();
    assertEquals(1, column.size());

    JsonArray elements = column.get(0).getAsJsonArray();
    assertEquals(1, elements.size());
    assertTrue(elements.get(0).isJsonNull());
  }

  @Test
  void mapsCellsByCoordinatesRegardlessOfInsertionOrder() {
    Board board = new Board();

    Cell pitCell = new Cell(new Position(1, 0), "4A");
    pitCell.addElement(new Pit());
    board.addCell(pitCell);

    Cell startCell = new Cell(new Position(0, 0), "4A");
    startCell.addElement(new StartPoint());
    board.addCell(startCell);

    JsonArray gameMap = new BoardMapper().mapBoard(board, 2, 1);

    assertEquals(2, gameMap.size());

    JsonArray firstColumn = gameMap.get(0).getAsJsonArray();
    JsonArray secondColumn = gameMap.get(1).getAsJsonArray();

    assertEquals(1, firstColumn.size());
    assertEquals(1, secondColumn.size());

    JsonObject startPoint = firstColumn.get(0).getAsJsonArray().get(0).getAsJsonObject();

    JsonObject pit = secondColumn.get(0).getAsJsonArray().get(0).getAsJsonObject();

    assertEquals("StartPoint", startPoint.get("type").getAsString());
    assertEquals("Pit", pit.get("type").getAsString());
  }

  @Test
  void mapsCellsIntoRowsWithinSameColumn() {
    Board board = new Board();

    Cell startCell = new Cell(new Position(0, 0), "4A");
    startCell.addElement(new StartPoint());
    board.addCell(startCell);

    Cell pitCell = new Cell(new Position(0, 1), "4A");
    pitCell.addElement(new Pit());
    board.addCell(pitCell);

    JsonArray gameMap = new BoardMapper().mapBoard(board, 1, 2);

    assertEquals(1, gameMap.size());

    JsonArray column = gameMap.get(0).getAsJsonArray();
    assertEquals(2, column.size());

    JsonArray firstCellElements = column.get(0).getAsJsonArray();
    JsonArray secondCellElements = column.get(1).getAsJsonArray();

    assertEquals(
        "StartPoint", firstCellElements.get(0).getAsJsonObject().get("type").getAsString());
    assertEquals("Pit", secondCellElements.get(0).getAsJsonObject().get("type").getAsString());
  }

  @Test
  void rejectsNonPositiveBoardDimensions() {
    BoardMapper mapper = new BoardMapper();
    Board board = new Board();

    assertThrows(IllegalArgumentException.class, () -> mapper.mapBoard(board, 0, 1));

    assertThrows(IllegalArgumentException.class, () -> mapper.mapBoard(board, -1, 1));

    assertThrows(IllegalArgumentException.class, () -> mapper.mapBoard(board, 1, 0));

    assertThrows(IllegalArgumentException.class, () -> mapper.mapBoard(board, 1, -1));
  }
}
