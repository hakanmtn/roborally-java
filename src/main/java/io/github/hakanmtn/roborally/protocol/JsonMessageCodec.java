package io.github.hakanmtn.roborally.protocol;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class JsonMessageCodec {

  private final Gson gson = new Gson();

  public String encode(ProtocolMessage<?> message) {
    return gson.toJson(message);
  }

  public ProtocolMessage<JsonObject> decode(String json) {
    JsonObject root = JsonParser.parseString(json).getAsJsonObject();

    if (!root.has("messageType") || root.get("messageType").isJsonNull()) {
      throw new IllegalArgumentException("Message type is required");
    }
    if (!root.get("messageType").isJsonPrimitive() || !root.getAsJsonPrimitive("messageType").isString()) {
      throw new IllegalArgumentException("Message type must be a JSON object");
    }

    if (!root.has("messageBody") || root.get("messageBody").isJsonNull()) {
      throw new IllegalArgumentException("Message body is required");
    }

    if (!root.get("messageBody").isJsonObject()) {
      throw new IllegalArgumentException("Message body must be a JSON object");
    }




    String messageType = root.get("messageType").getAsString();

    if(messageType.isBlank()) {
      throw new IllegalArgumentException("Message type muss not be blank");
    }

    JsonObject messageBody = root.getAsJsonObject("messageBody");

    return new ProtocolMessage<>(messageType, messageBody);
  }

  public <T> T decodeBody(JsonObject body, Class<T> bodyType) {
    return gson.fromJson(body, bodyType);
  }
}
