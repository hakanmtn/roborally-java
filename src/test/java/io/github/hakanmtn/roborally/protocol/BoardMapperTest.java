package io.github.hakanmtn.roborally.protocol;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import io.github.hakanmtn.roborally.model.Direction;
import io.github.hakanmtn.roborally.model.Position;
import io.github.hakanmtn.roborally.model.RotationDirection;
import io.github.hakanmtn.roborally.model.board.Cell;
import io.github.hakanmtn.roborally.model.board.element.*;

import java.util.Set;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

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
  void mapsCounterclockwiseGearRotation(){
    Cell cell = new Cell(new Position(2, 3), "4A");
    cell.addElement(new Gear(RotationDirection.COUNTERCLOCKWISE));

    JsonArray elements = new BoardMapper().mapCell(cell);
    JsonObject gear = elements.get(0).getAsJsonObject();

    JsonArray orientations = gear.getAsJsonArray("orientations");
    assertEquals(1, orientations.size());
    assertEquals("counterclockwise", orientations.get(0).getAsString());
  }

  @Test
  void mapsAntennaWithBoardNameAndFacing(){
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
  void mapsPitWithBoardName(){
    Cell cell = new Cell(new Position(2, 3), "4A");
    cell.addElement(new Pit());

    JsonArray elements = new BoardMapper().mapCell(cell);
    assertEquals(1, elements.size());
    JsonObject pit = elements.get(0).getAsJsonObject();
    assertEquals("Pit", pit.get("type").getAsString());
    assertEquals("4A", pit.get("isOnBoard").getAsString());
    assertFalse(pit.has("orientations"));
  }
}
