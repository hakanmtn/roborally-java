package io.github.hakanmtn.roborally.model.protocol;

import io.github.hakanmtn.roborally.protocol.HelloClientBody;
import io.github.hakanmtn.roborally.protocol.ProtocolMessage;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ProtocolMessageTest {

    @Test
    void helloClientMessageContainsItsTypeAndProtocolVersion() {
        HelloClientBody body = new HelloClientBody("Version 0.1");

        ProtocolMessage<HelloClientBody> message =
                new ProtocolMessage<>("HelloClient", body);

        assertEquals("HelloClient", message.messageType());
        assertEquals("Version 0.1", message.messageBody().protocol());
    }
}
