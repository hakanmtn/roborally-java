package io.github.hakanmtn.roborally.model.protocol;

import static org.junit.jupiter.api.Assertions.*;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.github.hakanmtn.roborally.protocol.HelloClientBody;
import io.github.hakanmtn.roborally.protocol.JsonMessageCodec;
import io.github.hakanmtn.roborally.protocol.ProtocolMessage;
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

    String json = """
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

    String json = """
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

    String json = """
            {
              "messageType": "   ",
              "messageBody": {}
            }
            """;

    assertThrows(IllegalArgumentException.class, () -> codec.decode(json));

  }
}
