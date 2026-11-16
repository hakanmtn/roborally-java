package io.github.hakanmtn.roborally.server;

import static org.junit.jupiter.api.Assertions.*;

import io.github.hakanmtn.roborally.protocol.PlayerAddedBody;
import io.github.hakanmtn.roborally.protocol.PlayerValuesBody;
import org.junit.jupiter.api.Test;

import java.util.List;

class ChatServerTest {

  @Test
  void assignedClientIdsArePositiveAndDistinct() {
    ChatServer chatServer = new ChatServer();
    int firstId = chatServer.assignClientId();
    int secondId = chatServer.assignClientId();

    assertTrue(firstId > 0);
    assertTrue(secondId > 0);
    assertNotEquals(firstId, secondId);
  }

  @Test
  void playersWithTheSameNameCanChooseDifferentFigures() {
    ChatServer chatServer = new ChatServer();
    int firstId = chatServer.assignClientId();
    int secondId = chatServer.assignClientId();

    assertTrue(chatServer.trySelectFigure(firstId, new PlayerValuesBody("Alex", 5)));
    assertTrue(chatServer.trySelectFigure(secondId, new PlayerValuesBody("Alex", 2)));
  }

  @Test
  void rejectsFigureAlreadySelectedByAnotherPlayer() {
    ChatServer chatServer = new ChatServer();
    int firstId = chatServer.assignClientId();
    int secondId = chatServer.assignClientId();

    assertTrue(chatServer.trySelectFigure(firstId, new PlayerValuesBody("Alex", 5)));
    assertFalse(chatServer.trySelectFigure(secondId, new PlayerValuesBody("Sam", 5)));
  }

  @Test
  void playerCanChooseAnotherFigureAfterRejection() {
    ChatServer chatServer = new ChatServer();
    int firstId = chatServer.assignClientId();
    int secondId = chatServer.assignClientId();

    assertTrue(chatServer.trySelectFigure(firstId, new PlayerValuesBody("Alex", 5)));
    assertFalse(chatServer.trySelectFigure(secondId, new PlayerValuesBody("Sam", 5)));
    assertTrue(chatServer.trySelectFigure(secondId, new PlayerValuesBody("Sam", 2)));
  }

  @Test
  void rejectedSelectionKeepsFigureUnavailableToOtherPlayers() {
    ChatServer chatServer = new ChatServer();
    int firstId = chatServer.assignClientId();
    int secondId = chatServer.assignClientId();

    assertTrue(chatServer.trySelectFigure(firstId, new PlayerValuesBody("Alex", 5)));
    assertFalse(chatServer.trySelectFigure(secondId, new PlayerValuesBody("Sam", 5)));

    int thirdId = chatServer.assignClientId();
    assertFalse(chatServer.trySelectFigure(thirdId, new PlayerValuesBody("Kim", 5)));
  }

  @Test
  void selectedPlayersIncludePreviouslyAcceptedPlayer(){
    ChatServer chatServer = new ChatServer();
    int alexId = chatServer.assignClientId();
    chatServer.trySelectFigure(alexId, new PlayerValuesBody("Alex", 5));

    assertEquals(List.of(new PlayerAddedBody(alexId, "Alex" , 5)), chatServer.getSelectedPlayers());
  }

  @Test
  void removingPlayerSelectionFreesFigureForAnotherPlayer(){
    ChatServer chatServer = new ChatServer();
    int firstId = chatServer.assignClientId();
    int secondId = chatServer.assignClientId();

    assertTrue(chatServer.trySelectFigure(firstId, new PlayerValuesBody("Alex", 5)));
    assertFalse(chatServer.trySelectFigure(secondId, new PlayerValuesBody("Sam", 5)));

    chatServer.removePlayerSelection(firstId);
    assertTrue(chatServer.trySelectFigure(secondId, new PlayerValuesBody("Sam", 5)));
  }
}
