package io.github.hakanmtn.roborally.protocol;

import static org.junit.jupiter.api.Assertions.*;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import java.util.List;
import org.junit.jupiter.api.Test;

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
  void encodingSelectMapPreservesAvailableMaps() {
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
  void encodingMapSelectedPreservesChosenMap() {
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

  @Test
  void encodingSendChatPreservesMessageAndPublicRecipient() {
    JsonMessageCodec codec = new JsonMessageCodec();
    SendChatBody sentBody = new SendChatBody("Hello", -1);
    ProtocolMessage<SendChatBody> sentMessage = new ProtocolMessage<>("SendChat", sentBody);
    String json = codec.encode(sentMessage);

    JsonObject root = JsonParser.parseString(json).getAsJsonObject();
    JsonObject body = root.getAsJsonObject("messageBody");

    assertEquals("SendChat", root.get("messageType").getAsString());

    JsonPrimitive message = body.getAsJsonPrimitive("message");
    assertTrue(message.isString());
    assertEquals("Hello", message.getAsString());

    JsonPrimitive to = body.getAsJsonPrimitive("to");
    assertTrue(to.isNumber());
    assertEquals(-1, to.getAsInt());
  }

  @Test
  void encodingReceivedChatPreservesSenderAndPrivacy() {
    JsonMessageCodec codec = new JsonMessageCodec();
    ReceivedChatBody sentBody = new ReceivedChatBody("Hello", 42, true);
    ProtocolMessage<ReceivedChatBody> sentMessage = new ProtocolMessage<>("ReceivedChat", sentBody);
    String json = codec.encode(sentMessage);
    JsonObject root = JsonParser.parseString(json).getAsJsonObject();
    JsonObject body = root.getAsJsonObject("messageBody");

    assertEquals("ReceivedChat", root.get("messageType").getAsString());

    JsonPrimitive message = body.getAsJsonPrimitive("message");
    assertTrue(message.isString());
    assertEquals("Hello", message.getAsString());

    JsonPrimitive from = body.getAsJsonPrimitive("from");
    assertTrue(from.isNumber());
    assertEquals(42, from.getAsInt());

    JsonPrimitive isPrivate = body.getAsJsonPrimitive("isPrivate");
    assertTrue(isPrivate.isBoolean());
    assertTrue(isPrivate.getAsBoolean());
  }

  @Test
  void decodingSendChatWithoutMessageThrowsException() {
    JsonMessageCodec codec = new JsonMessageCodec();

    JsonObject body =
        JsonParser.parseString(
                """
            {"to":-1}
            """)
            .getAsJsonObject();

    assertThrows(IllegalArgumentException.class, () -> codec.decodeBody(body, SendChatBody.class));
  }

  @Test
  void decodingSendChatWithNullMessageThrowsException() {
    JsonMessageCodec codec = new JsonMessageCodec();
    JsonObject body =
        JsonParser.parseString(
                """
        {"message":null,"to":-1}
        """)
            .getAsJsonObject();
    assertThrows(IllegalArgumentException.class, () -> codec.decodeBody(body, SendChatBody.class));
  }

  @Test
  void decodingSendChatWithNumericMessageThrowsException() {
    JsonMessageCodec codec = new JsonMessageCodec();

    JsonObject body =
        JsonParser.parseString(
                """
            {"message":42,"to":-1}
            """)
            .getAsJsonObject();

    assertThrows(IllegalArgumentException.class, () -> codec.decodeBody(body, SendChatBody.class));
  }

  @Test
  void decodingSendChatWithoutRecipientThrowsException() {
    JsonMessageCodec codec = new JsonMessageCodec();

    JsonObject body =
        JsonParser.parseString(
                """
            {"message":"Hello"}
            """)
            .getAsJsonObject();

    assertThrows(IllegalArgumentException.class, () -> codec.decodeBody(body, SendChatBody.class));
  }

  @Test
  void decodingSendChatWithStringRecipientThrowsException() {
    JsonMessageCodec codec = new JsonMessageCodec();

    JsonObject body =
        JsonParser.parseString(
                """
            {"message":"Hello","to":"2"}
            """)
            .getAsJsonObject();

    assertThrows(IllegalArgumentException.class, () -> codec.decodeBody(body, SendChatBody.class));
  }

  @Test
  void decodingSendChatWithFractionalRecipientThrowsException() {
    JsonMessageCodec codec = new JsonMessageCodec();
    JsonObject body =
        JsonParser.parseString(
                """
        {"message":"Hello","to":2.5}
        """)
            .getAsJsonObject();
    assertThrows(IllegalArgumentException.class, () -> codec.decodeBody(body, SendChatBody.class));
  }

  @Test
  void decodingSendChatWithZeroRecipientThrowsException() {
    JsonMessageCodec codec = new JsonMessageCodec();
    JsonObject body =
        JsonParser.parseString(
                """
            {"message":"Hello","to":0}
            """)
            .getAsJsonObject();

    assertThrows(IllegalArgumentException.class, () -> codec.decodeBody(body, SendChatBody.class));
  }

  @Test
  void decodingSendChatWithInvalidNegativeRecipientThrowsException() {
    JsonMessageCodec codec = new JsonMessageCodec();

    JsonObject body =
        JsonParser.parseString(
                """
            {"message":"Hello","to":-2}
            """)
            .getAsJsonObject();

    assertThrows(IllegalArgumentException.class, () -> codec.decodeBody(body, SendChatBody.class));
  }

  @Test
  void decodingSendChatAllowsPublicRecipient() {
    JsonMessageCodec codec = new JsonMessageCodec();

    JsonObject body =
        JsonParser.parseString(
                """
            {"message":"Hello","to":-1}
            """)
            .getAsJsonObject();

    SendChatBody chat = codec.decodeBody(body, SendChatBody.class);

    assertEquals("Hello", chat.message());
    assertEquals(-1, chat.to());
  }

  @Test
  void decodingSendChatAllowsPositiveRecipient() {
    JsonMessageCodec codec = new JsonMessageCodec();

    JsonObject body =
        JsonParser.parseString(
                """
            {"message":"Hello Sam","to":1}
            """)
            .getAsJsonObject();

    SendChatBody chat = codec.decodeBody(body, SendChatBody.class);

    assertEquals("Hello Sam", chat.message());
    assertEquals(1, chat.to());
  }

  @Test
  void decodingSetStatusWithoutReadyThrowsException() {
    JsonMessageCodec codec = new JsonMessageCodec();
    JsonObject body = new JsonObject();

    assertThrows(IllegalArgumentException.class, () -> codec.decodeBody(body, SetStatusBody.class));
  }

  @Test
  void decodingSetStatusAllowsFalseReadyValue() {
    JsonMessageCodec codec = new JsonMessageCodec();

    JsonObject body =
        JsonParser.parseString(
                """
            {"ready":false}
            """)
            .getAsJsonObject();

    SetStatusBody status = codec.decodeBody(body, SetStatusBody.class);

    assertFalse(status.ready());
  }

  @Test
  void decodingSetStatusWithStringReadyThrowsException() {
    JsonMessageCodec codec = new JsonMessageCodec();

    JsonObject body =
        JsonParser.parseString(
                """
            {"ready":"false"}
            """)
            .getAsJsonObject();

    assertThrows(IllegalArgumentException.class, () -> codec.decodeBody(body, SetStatusBody.class));
  }

  @Test
  void decodingPlayerValuesWithoutNameThrowsException() {
    JsonMessageCodec codec = new JsonMessageCodec();

    JsonObject body =
        JsonParser.parseString(
                """
            {"figure":5}
            """)
            .getAsJsonObject();

    assertThrows(
        IllegalArgumentException.class, () -> codec.decodeBody(body, PlayerValuesBody.class));
  }

  @Test
  void decodingPlayerValuesWithNumericNameThrowsException() {
    JsonMessageCodec codec = new JsonMessageCodec();

    JsonObject body = JsonParser.parseString("""
            {"name":42,"figure":5}
            """).getAsJsonObject();

    assertThrows(
            IllegalArgumentException.class,
            () -> codec.decodeBody(body, PlayerValuesBody.class));
  }

  @Test
  void decodingPlayerValuesWithoutFigureThrowsException() {
    JsonMessageCodec codec = new JsonMessageCodec();

    JsonObject body = JsonParser.parseString("""
            {"name":"Alex"}
            """).getAsJsonObject();

    assertThrows(
            IllegalArgumentException.class,
            () -> codec.decodeBody(body, PlayerValuesBody.class));
  }

  @Test
  void decodingPlayerValuesWithFractionalFigureThrowsException(){
    JsonMessageCodec codec = new JsonMessageCodec();
    JsonObject body = JsonParser.parseString("""
        {"name":"Alex","figure":2.5}
        """).getAsJsonObject();

    assertThrows(
        IllegalArgumentException.class,
        () -> codec.decodeBody(body, PlayerValuesBody.class));

  }

  @Test
  void decodingPlayerValuesPreservesValidSelection() {
    JsonMessageCodec codec = new JsonMessageCodec();

    JsonObject body = JsonParser.parseString("""
            {"name":"Alex","figure":5}
            """).getAsJsonObject();

    PlayerValuesBody selection =
            codec.decodeBody(body, PlayerValuesBody.class);

    assertEquals("Alex", selection.name());
    assertEquals(5, selection.figure());
  }

  @Test
  void decodingMapSelectedWithoutMapThrowsException() {
    JsonMessageCodec codec = new JsonMessageCodec();
    JsonObject body = new JsonObject();

    assertThrows(
            IllegalArgumentException.class,
            () -> codec.decodeBody(body, MapSelectedBody.class));
  }

  @Test
  void decodingMapSelectedWithNumericMapThrowsException() {
    JsonMessageCodec codec = new JsonMessageCodec();

    JsonObject body = JsonParser.parseString("""
            {"map":42}
            """).getAsJsonObject();

    assertThrows(
            IllegalArgumentException.class,
            () -> codec.decodeBody(body, MapSelectedBody.class));
  }

  @Test
  void decodingMapSelectedPreservesValidMap() {
    JsonMessageCodec codec = new JsonMessageCodec();

    JsonObject body = JsonParser.parseString("""
            {"map":"Dizzy Highway"}
            """).getAsJsonObject();

    MapSelectedBody selection =
            codec.decodeBody(body, MapSelectedBody.class);

    assertEquals("Dizzy Highway", selection.map());
  }

}
