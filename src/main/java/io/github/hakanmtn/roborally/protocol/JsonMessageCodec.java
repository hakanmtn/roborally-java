package io.github.hakanmtn.roborally.protocol;

import com.google.gson.*;

public class JsonMessageCodec {

  private final Gson gson = new Gson();

  public String encode(ProtocolMessage<?> message) {
    return gson.toJson(message);
  }

  public ProtocolMessage<JsonObject> decode(String json) {

    JsonElement parsed;

    try {
      parsed = JsonParser.parseString(json);
    } catch (JsonSyntaxException e) {
      throw new IllegalArgumentException("Invalid JSON syntax", e);
    }

    if (!parsed.isJsonObject()) throw new IllegalArgumentException("Message must be a JSON object");

    JsonObject root = parsed.getAsJsonObject();

    if (!root.has("messageType") || root.get("messageType").isJsonNull()) {
      throw new IllegalArgumentException("Message type is required");
    }
    if (!root.get("messageType").isJsonPrimitive()
        || !root.getAsJsonPrimitive("messageType").isString()) {
      throw new IllegalArgumentException("Message type must be a JSON object");
    }

    if (!root.has("messageBody") || root.get("messageBody").isJsonNull()) {
      throw new IllegalArgumentException("Message body is required");
    }

    if (!root.get("messageBody").isJsonObject()) {
      throw new IllegalArgumentException("Message body must be a JSON object");
    }

    String messageType = root.get("messageType").getAsString();

    if (messageType.isBlank()) {
      throw new IllegalArgumentException("Message type muss not be blank");
    }

    JsonObject messageBody = root.getAsJsonObject("messageBody");

    return new ProtocolMessage<>(messageType, messageBody);
  }

  public <T> T decodeBody(JsonObject body, Class<T> bodyType) {
    if (bodyType == SendChatBody.class) {
      JsonElement message = body.get("message");

      if (message == null || message.isJsonNull()) {
        throw new IllegalArgumentException("Chat message is required");
      }

      if (!message.isJsonPrimitive() || !message.getAsJsonPrimitive().isString()) {
        throw new IllegalArgumentException("Chat message must be a JSON string");
      }

      JsonElement recipient = body.get("to");
      if (recipient == null || recipient.isJsonNull()) {
        throw new IllegalArgumentException("Chat recipient is required");
      }

      if (!recipient.isJsonPrimitive() || !recipient.getAsJsonPrimitive().isNumber()) {
        throw new IllegalArgumentException("Chat recipient must be a JSON number");
      }

      int recipientId;
      try {
        recipientId = recipient.getAsBigDecimal().intValueExact();
      } catch (ArithmeticException | NumberFormatException e) {
        throw new IllegalArgumentException(
            "Chat recipient must be an integer within the int range", e);
      }

      if (recipientId != -1 && recipientId <= 0) {
        throw new IllegalArgumentException("Chat recipient must be -1 or a positive client ID");
      }
    }

    if (bodyType == SetStatusBody.class) {
      JsonElement ready = body.get("ready");
      if (ready == null || ready.isJsonNull()) {
        throw new IllegalArgumentException("Ready status is required");
      }
      if (!ready.isJsonPrimitive() || !ready.getAsJsonPrimitive().isBoolean()) {
        throw new IllegalArgumentException("Ready status must be a JSON boolean");
      }
    }

    if (bodyType == PlayerValuesBody.class) {
      JsonElement name = body.get("name");
      if (name == null || name.isJsonNull()) {
        throw new IllegalArgumentException("Player name is required");
      }

      if (!name.isJsonPrimitive() || !name.getAsJsonPrimitive().isString()) {
        throw new IllegalArgumentException("Player name must be a JSON string");
      }

      JsonElement figure = body.get("figure");
      if (figure == null || figure.isJsonNull()) {
        throw new IllegalArgumentException("Player figure is required");
      }

      if (!figure.isJsonPrimitive() || !figure.getAsJsonPrimitive().isNumber()) {
        throw new IllegalArgumentException("Player figure must be a JSON number");
      }

      try {
        figure.getAsBigDecimal().intValueExact();
      } catch (ArithmeticException | NumberFormatException e) {
        throw new IllegalArgumentException(
            "Player figure must be an integer within the int range", e);
      }
    }

    if(bodyType == MapSelectedBody.class) {
      JsonElement map = body.get("map");
      if (map == null || map.isJsonNull()) {
        throw new IllegalArgumentException("Map name is required");
      }
      if (!map.isJsonPrimitive() || !map.getAsJsonPrimitive().isString()) {
        throw new IllegalArgumentException("Map name must be a JSON string");
      }
    }

    return gson.fromJson(body, bodyType);
  }

  public boolean isAliveMessage(String message) {
    try {
      ProtocolMessage<JsonObject> received = decode(message);
      return "Alive".equals(received.messageType()) && received.messageBody().isEmpty();
    } catch (IllegalArgumentException e) {
      return false;
    }
  }
}
