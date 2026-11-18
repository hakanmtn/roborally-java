package io.github.hakanmtn.roborally.protocol;

import static org.junit.jupiter.api.Assertions.*;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import org.junit.jupiter.api.Test;

import java.util.List;

class JsonMessageCodecTest {

  @Test
  void encodesHelloClientMessage() {

    JsonMessageCodec codec = new JsonMessageCodec();
    ProtocolMessage<HelloClientBody> message =
        new ProtocolMessage<>("HelloClient", new HelloClientBody("Version 0.1"));

    String json = codec.encode(message);

    JsonObject result = JsonParser.parseString(json).getAsJsonObject();

    assertEquals("HelloClient", result.get("messageType").getAsString());
    assertEquals(
        "Version 0.1", result.getAsJsonObject("messageBody").get("protocol").getAsString());
  }

  @Test
  void decodesHelloClientMessageEnvelope() {
    JsonMessageCodec codec = new JsonMessageCodec();

    String json =
        """
           {
              "messageType": "HelloClient",
              "messageBody": {
                 "protocol": "Version 0.1"
              }
           }

        """;

    ProtocolMessage<JsonObject> message = codec.decode(json);

    assertEquals("HelloClient", message.messageType());
    assertEquals("Version 0.1", message.messageBody().get("protocol").getAsString());
  }

  @Test
  void decodesHelloClientBody() {
    JsonMessageCodec codec = new JsonMessageCodec();
    JsonObject jsonBody = new JsonObject();
    jsonBody.addProperty("protocol", "Version 0.1");

    HelloClientBody body = codec.decodeBody(jsonBody, HelloClientBody.class);

    assertEquals("Version 0.1", body.protocol());
  }

  @Test
  void decodingMessageWithoutTypeThrowsException() {
    JsonMessageCodec codec = new JsonMessageCodec();

    String json =
        """
               {
                  "messageBody": {
                     "protocol": "Version 0.1"
                  }
               }

            """;

    assertThrows(IllegalArgumentException.class, () -> codec.decode(json));
  }

  @Test
  void decodingMessageWithNullTypeThrowsException() {
    JsonMessageCodec codec = new JsonMessageCodec();

    String json =
        """
            {
              "messageType": null,
              "messageBody": {}
            }
            """;

    assertThrows(IllegalArgumentException.class, () -> codec.decode(json));
  }

  @Test
  void decodingMessageWithoutBodyThrowsException() {
    JsonMessageCodec codec = new JsonMessageCodec();

    String json =
        """
            {
              "messageType": "Alive"
            }
            """;

    assertThrows(IllegalArgumentException.class, () -> codec.decode(json));
  }

  @Test
  void decodingMessageWithNullBodyThrowsException() {
    JsonMessageCodec codec = new JsonMessageCodec();

    String json =
        """
            {
              "messageType": "Alive",
              "messageBody": null
            }
            """;
    assertThrows(IllegalArgumentException.class, () -> codec.decode(json));
  }

  @Test
  void decodingAliveMessageAllowsEmptyBody() {
    JsonMessageCodec codec = new JsonMessageCodec();

    String json =
        """
        {
          "messageType": "Alive",
          "messageBody": {}
        }
        """;

    ProtocolMessage<JsonObject> message = codec.decode(json);

    assertEquals("Alive", message.messageType());
    assertTrue(message.messageBody().isEmpty());
  }

  @Test
  void decodingMessageWithArrayBodyThrowsException() {
    JsonMessageCodec codec = new JsonMessageCodec();

    String json =
        """
            {
              "messageType": "Alive",
              "messageBody": []
            }
            """;

    assertThrows(IllegalArgumentException.class, () -> codec.decode(json));
  }

  @Test
  void decodingMessageWithNumericTypeThrowsException() {
    JsonMessageCodec codec = new JsonMessageCodec();

    String json =
        """
            {
              "messageType": 42,
              "messageBody": {}
            }
            """;

    assertThrows(IllegalArgumentException.class, () -> codec.decode(json));
  }

  @Test
  void decodingMessageWithBlankTypeThrowsException() {
    JsonMessageCodec codec = new JsonMessageCodec();

    String json =
        """
            {
              "messageType": "   ",
              "messageBody": {}
            }
            """;

    assertThrows(IllegalArgumentException.class, () -> codec.decode(json));
  }

  @Test
  void decodingTopLevelArrayThrowsException() {
    JsonMessageCodec codec = new JsonMessageCodec();

    String json =
        """
            [{"messageType":"Alive","messageBody":{}}]
            """;

    assertThrows(IllegalArgumentException.class, () -> codec.decode(json));
  }

  @Test
  void decodingIncompleteJsonThrowsException() {
    JsonMessageCodec codec = new JsonMessageCodec();
    String json =
        """
            {"messageType":"Alive","messageBody":{}
            """;

    assertThrows(IllegalArgumentException.class, () -> codec.decode(json));
  }

  @Test
  void helloClientProtocolSurvivesEncodingAndDecoding() {

    JsonMessageCodec codec = new JsonMessageCodec();
    HelloClientBody sentBody = new HelloClientBody("Version 0.1");
    ProtocolMessage<HelloClientBody> sentMessage = new ProtocolMessage<>("HelloClient", sentBody);

    String json = codec.encode(sentMessage);
    ProtocolMessage<JsonObject> receivedMessage = codec.decode(json);
    HelloClientBody receivedBody =
        codec.decodeBody(receivedMessage.messageBody(), HelloClientBody.class);

    assertEquals("HelloClient", receivedMessage.messageType());
    assertEquals("Version 0.1", receivedBody.protocol());
  }

  @Test
  void encodingHelloServerPreservesClientIdentityAndProtocol() {
    JsonMessageCodec codec = new JsonMessageCodec();

    HelloServerBody sentBody = new HelloServerBody("RoboRally", false, "Version 0.1");
    ProtocolMessage<HelloServerBody> sentMessage = new ProtocolMessage<>("HelloServer", sentBody);

    String json = codec.encode(sentMessage);

    JsonObject root = JsonParser.parseString(json).getAsJsonObject();
    JsonObject body = root.getAsJsonObject("messageBody");

    assertEquals("HelloServer", root.get("messageType").getAsString());
    assertEquals("RoboRally", body.get("group").getAsString());
    assertEquals("Version 0.1", body.get("protocol").getAsString());
    assertTrue(body.getAsJsonPrimitive("isAI").isBoolean());
    assertFalse(body.get("isAI").getAsBoolean());
  }

  @Test
  void encodingWelcomePreservesClientIdAsNumber() {
    JsonMessageCodec codec = new JsonMessageCodec();
    WelcomeBody sentBody = new WelcomeBody(42);
    ProtocolMessage<WelcomeBody> sentMessage = new ProtocolMessage<>("Welcome", sentBody);

    String json = codec.encode(sentMessage);
    JsonObject root = JsonParser.parseString(json).getAsJsonObject();
    JsonObject body = root.getAsJsonObject("messageBody");

    assertEquals("Welcome", root.get("messageType").getAsString());

    JsonPrimitive clientId = body.getAsJsonPrimitive("clientID");
    assertTrue(clientId.isNumber());
    assertEquals(42, clientId.getAsInt());
  }

  @Test
  void encodingAliveProducesEmptyMessageBody() {
    JsonMessageCodec codec = new JsonMessageCodec();
    AliveBody aliveBody = new AliveBody();
    ProtocolMessage<AliveBody> sentMessage = new ProtocolMessage<>("Alive", aliveBody);

    String json = codec.encode(sentMessage);
    JsonObject root = JsonParser.parseString(json).getAsJsonObject();
    JsonObject body = root.getAsJsonObject("messageBody");

    assertNotNull(body);
    assertEquals(0, body.size());
    assertEquals("Alive", root.get("messageType").getAsString());
  }

  @Test
  void plainChatMessageIsNotAnAliveReply() {
    JsonMessageCodec codec = new JsonMessageCodec();

    assertFalse(codec.isAliveMessage("Alive"));
  }

  @Test
  void aliveProtocolMessageIsAnAliveReply() {
    JsonMessageCodec codec = new JsonMessageCodec();

    String message =
        """
            {"messageType":"Alive","messageBody":{}}
            """
            .strip();

    assertTrue(codec.isAliveMessage(message));
  }

  @Test
  void encodingPlayerValuesPreservesNameAndFigure() {
    JsonMessageCodec codec = new JsonMessageCodec();
    PlayerValuesBody playerValuesBody = new PlayerValuesBody("Nr. 5", 5);

    ProtocolMessage<PlayerValuesBody> sentMessage =
        new ProtocolMessage<>("PlayerValues", playerValuesBody);
    String json = codec.encode(sentMessage);
    JsonObject root = JsonParser.parseString(json).getAsJsonObject();
    JsonObject body = root.getAsJsonObject("messageBody");

    assertEquals("PlayerValues", root.get("messageType").getAsString());
    JsonPrimitive figure = body.getAsJsonPrimitive("figure");
    assertTrue(figure.isNumber());
    assertEquals(5, figure.getAsInt());
    assertEquals("Nr. 5", body.get("name").getAsString());
  }

  @Test
  void encodingPlayerAddedIdentifiesPlayerAndSelectedFigure() {
    JsonMessageCodec codec = new JsonMessageCodec();
    PlayerAddedBody playerAddedBody = new PlayerAddedBody(42, "Nr. 5", 5);
    ProtocolMessage<PlayerAddedBody> sentMessage =
        new ProtocolMessage<>("PlayerAdded", playerAddedBody);

    String json = codec.encode(sentMessage);
    JsonObject root = JsonParser.parseString(json).getAsJsonObject();
    JsonObject body = root.getAsJsonObject("messageBody");

    assertEquals("PlayerAdded", root.get("messageType").getAsString());
    JsonPrimitive clientId = body.getAsJsonPrimitive("clientID");
    JsonPrimitive figure = body.getAsJsonPrimitive("figure");

    assertTrue(clientId.isNumber());
    assertEquals(42, clientId.getAsInt());
    assertTrue(figure.isNumber());
    assertEquals(5, figure.getAsInt());
    assertEquals("Nr. 5", body.get("name").getAsString());
  }

  @Test
  void encodingSetStatusPreservesFalseReadyValue() {
    JsonMessageCodec codec = new JsonMessageCodec();
    SetStatusBody setBody = new SetStatusBody(false);
    ProtocolMessage<SetStatusBody> sentMessage = new ProtocolMessage<>("SetStatus", setBody);

    String json = codec.encode(sentMessage);
    JsonObject root = JsonParser.parseString(json).getAsJsonObject();
    JsonObject body = root.getAsJsonObject("messageBody");

    assertEquals("SetStatus", root.get("messageType").getAsString());

    assertTrue(body.getAsJsonPrimitive("ready").isBoolean());
    assertFalse(body.get("ready").getAsBoolean());
  }

  @Test
  void encodingPlayerStatusIdentifiesReadyPlayer() {
    JsonMessageCodec codec = new JsonMessageCodec();
    PlayerStatusBody playerStatusBody = new PlayerStatusBody(42, true);
    ProtocolMessage<PlayerStatusBody> sentMessage =
        new ProtocolMessage<>("PlayerStatus", playerStatusBody);

    String json = codec.encode(sentMessage);
    JsonObject root = JsonParser.parseString(json).getAsJsonObject();
    JsonObject body = root.getAsJsonObject("messageBody");

    assertEquals("PlayerStatus", root.get("messageType").getAsString());

    assertTrue(body.getAsJsonPrimitive("clientID").isNumber());
    assertEquals(42, body.getAsJsonPrimitive("clientID").getAsInt());

    JsonPrimitive ready = body.getAsJsonPrimitive("ready");
    assertTrue(ready.isBoolean());
    assertTrue(ready.getAsBoolean());
  }

  @Test
  void encodingSelectMapPreservesAvailableMaps(){
    JsonMessageCodec codec = new JsonMessageCodec();
    SelectMapBody sentBody = new SelectMapBody(List.of("Dizzy Highway"));
    ProtocolMessage<SelectMapBody> sentMessage = new ProtocolMessage<>("SelectMap", sentBody);

    String json = codec.encode(sentMessage);
    JsonObject root = JsonParser.parseString(json).getAsJsonObject();
    JsonObject body = root.getAsJsonObject("messageBody");


    assertEquals("SelectMap", root.get("messageType").getAsString());
    assertTrue(body.getAsJsonArray("availableMaps").isJsonArray());

    JsonArray availableMaps = body.getAsJsonArray("availableMaps");

    assertEquals(1, availableMaps.size());

    assertEquals("Dizzy Highway", availableMaps.get(0).getAsString());

  }

  @Test
  void encodingMapSelectedPreservesChosenMap(){
    JsonMessageCodec codec = new JsonMessageCodec();
    MapSelectedBody sentBody = new MapSelectedBody("Dizzy Highway");
    ProtocolMessage<MapSelectedBody> sentMessage = new ProtocolMessage<>("MapSelected", sentBody);

    String json = codec.encode(sentMessage);
    JsonObject root = JsonParser.parseString(json).getAsJsonObject();
    JsonObject body = root.getAsJsonObject("messageBody");

    assertEquals("MapSelected", root.get("messageType").getAsString());

    JsonPrimitive chosenMap = body.getAsJsonPrimitive("map");
    assertTrue(chosenMap.isString());
    assertEquals("Dizzy Highway", chosenMap.getAsString());
  }
}
