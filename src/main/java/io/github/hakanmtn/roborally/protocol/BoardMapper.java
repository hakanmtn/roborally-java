package io.github.hakanmtn.roborally.protocol;

import com.google.gson.JsonArray;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import io.github.hakanmtn.roborally.model.Direction;
import io.github.hakanmtn.roborally.model.Position;
import io.github.hakanmtn.roborally.model.board.Board;
import io.github.hakanmtn.roborally.model.board.Cell;
import io.github.hakanmtn.roborally.model.board.element.*;

public class BoardMapper {

  public JsonArray mapCell(Cell cell) {
    JsonArray elements = new JsonArray();

    if (cell.getElements().isEmpty()) {
      JsonObject empty = new JsonObject();
      empty.addProperty("type", "Empty");
      empty.addProperty("isOnBoard", cell.getBoardName());

      elements.add(empty);
      return elements;
    }

    for (BoardElement element : cell.getElements()) {
      if (element instanceof Wall wall) {
        JsonObject json = new JsonObject();
        json.addProperty("type", "Wall");
        json.addProperty("isOnBoard", cell.getBoardName());

        JsonArray orientations = new JsonArray();

        for (Direction direction : wall.getBlockedEdges()) {
          orientations.add(mapDirection(direction));
        }
        json.add("orientations", orientations);
        elements.add(json);
      } else if (element instanceof Laser laser) {
        JsonObject json = new JsonObject();
        json.addProperty("type", "Laser");
        json.addProperty("isOnBoard", cell.getBoardName());
        json.addProperty("count", laser.getBeamCount());

        JsonArray orientations = new JsonArray();
        orientations.add(mapDirection(laser.getFiringDirection()));

        json.add("orientations", orientations);
        elements.add(json);

      } else if (element instanceof StartPoint) {
        JsonObject json = new JsonObject();
        json.addProperty("type", "StartPoint");
        json.addProperty("isOnBoard", cell.getBoardName());

        elements.add(json);

      } else if (element instanceof RestartPoint restartPoint) {
        JsonObject json = new JsonObject();
        json.addProperty("type", "RestartPoint");
        json.addProperty("isOnBoard", cell.getBoardName());

        JsonArray orientations = new JsonArray();
        orientations.add(mapDirection(restartPoint.getDirection()));
        json.add("orientations", orientations);
        elements.add(json);

      } else if (element instanceof Checkpoint checkpoint) {
        JsonObject json = new JsonObject();
        json.addProperty("type", "CheckPoint");
        json.addProperty("isOnBoard", cell.getBoardName());
        json.addProperty("count", checkpoint.getNumber());

        elements.add(json);

      } else if (element instanceof Gear gear) {
        JsonObject json = new JsonObject();
        json.addProperty("type", "Gear");
        json.addProperty("isOnBoard", cell.getBoardName());

        String rotation =
            switch (gear.getRotationDirection()) {
              case CLOCKWISE -> "clockwise";
              case COUNTERCLOCKWISE -> "counterclockwise";
            };

        JsonArray orientations = new JsonArray();
        orientations.add(rotation);
        json.add("orientations", orientations);

        elements.add(json);

      } else if (element instanceof Antenna antenna) {
        JsonObject json = new JsonObject();
        json.addProperty("type", "Antenna");
        json.addProperty("isOnBoard", cell.getBoardName());

        JsonArray orientations = new JsonArray();
        orientations.add(mapDirection(antenna.getFacing()));
        json.add("orientations", orientations);

        elements.add(json);

      } else if (element instanceof Pit pit) {
        JsonObject json = new JsonObject();
        json.addProperty("type", "Pit");
        json.addProperty("isOnBoard", cell.getBoardName());

        elements.add(json);
      } else if (element instanceof EnergySpace energySpace) {
        JsonObject json = new JsonObject();
        json.addProperty("type", "EnergySpace");
        json.addProperty("isOnBoard", cell.getBoardName());
        json.addProperty("count", energySpace.getCount());

        elements.add(json);

      } else if (element instanceof PushPanel pushPanel) {
        JsonObject json = new JsonObject();
        json.addProperty("type", "PushPanel");
        json.addProperty("isOnBoard", cell.getBoardName());

        JsonArray orientations = new JsonArray();
        orientations.add(mapDirection(pushPanel.getDirection()));
        json.add("orientations", orientations);

        JsonArray registers = new JsonArray();

        for (int register : pushPanel.getActiveRegisters()) {
          registers.add(register);
        }

        json.add("registers", registers);
        elements.add(json);

      } else if (element instanceof ConveyorBelt conveyorBelt) {
        JsonObject json = new JsonObject();
        json.addProperty("type", "ConveyorBelt");
        json.addProperty("isOnBoard", cell.getBoardName());
        json.addProperty("speed", conveyorBelt.getSpeed());

        JsonArray orientations = new JsonArray();
        orientations.add(mapDirection(conveyorBelt.getExitDirection()));

        for (Direction direction : conveyorBelt.getEntryDirections()) {
          orientations.add(mapDirection(direction));
        }

        json.add("orientations", orientations);
        elements.add(json);
      } else {
        throw new IllegalArgumentException(
            "Unsupported board element: " + element.getClass().getSimpleName());
      }
    }

    return elements;
  }

  private String mapDirection(Direction direction) {

    return switch (direction) {
      case NORTH -> "top";
      case EAST -> "right";
      case SOUTH -> "bottom";
      case WEST -> "left";
    };
  }

  public JsonArray mapBoard(Board board, int width, int height) {
    JsonArray gameMap = new JsonArray();

    for (int x = 0; x < width; x++) {
      JsonArray column = new JsonArray();

      for (int y = 0; y < height; y++) {
        Cell cell = board.getCell(new Position(x, y));

        if (cell == null) {
          JsonArray missingCell = new JsonArray();
          missingCell.add(JsonNull.INSTANCE);
          column.add(missingCell);
        } else {
          column.add(mapCell(cell));
        }
      }
      gameMap.add(column);
    }

    return gameMap;
  }
}
