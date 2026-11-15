package io.github.hakanmtn.roborally.server;

import io.github.hakanmtn.roborally.server.ChatServer;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ChatServerTest {

    @Test
    void assignedClientIdsArePositiveAndDistinct(){
        ChatServer chatServer = new ChatServer();
        int firstId = chatServer.assignClientId();
        int secondId = chatServer.assignClientId();

        assertTrue(firstId > 0);
        assertTrue(secondId > 0);
        assertNotEquals(firstId, secondId);
    }

}
