package io.github.hakanmtn.roborally.server;

import io.github.hakanmtn.roborally.protocol.*;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class ChatServer {

  private final CopyOnWriteArrayList<ClientHandler> activeClients = new CopyOnWriteArrayList<>();
  private int nextClientId = 1;
  private final Map<Integer, PlayerValuesBody> playerSelections = new HashMap<>();
  private final Map<Integer, Boolean> playerReadyStates = new HashMap<>();
  private final Set<Integer> readyPlayerOrder = new LinkedHashSet<>();
  private String selectedMap;
  private final List<String> availableMaps = List.of("Dizzy Highway");

  public static void main(String[] args) throws IOException {
    new ChatServer().start();
  }

  private void start() throws IOException {
    try (ServerSocket serverSocket = new ServerSocket(5500)) {
      System.out.println("Listening on port 5500...");

      while (true) {
        Socket clientSocket = serverSocket.accept();
        System.out.println("Client connected: " + clientSocket.getRemoteSocketAddress());

        var handler = new ClientHandler(clientSocket, this);
        activeClients.add(handler);
        new Thread(handler).start();
      }
    }
  }

  void removeClient(ClientHandler handler) {
    if (activeClients.remove(handler)) {
      System.out.println("Client disconnected: Active clients: " + activeClients.size());
    }
  }

  void broadcast(String message) {
    for (ClientHandler client : activeClients) {

      try {
        client.sendMessage(message);
      } catch (IOException e) {
        System.err.println("Could not send to client: " + e.getMessage());
        client.disconnect();
      }
    }
  }

  void sendToClient(int clientId, String message) {
    for (ClientHandler client : activeClients) {
      if (Objects.equals(clientId, client.getClientId())) {
        try {
          client.sendMessage(message);
        } catch (IOException e) {
          System.err.println("Could not send to client: " + e.getMessage());
          client.disconnect();
        }
        return;
      }
    }
  }

  void requestMapSelection() {
    Integer selectorId;
    List<String> maps;

    synchronized (this) {
      if (selectedMap != null) {
        return;
      }
      selectorId = getMapSelectorId();
      maps = availableMaps;
    }

    if (selectorId == null) {
      return;
    }

    SelectMapBody body = new SelectMapBody(maps);
    ProtocolMessage<SelectMapBody> protocolMessage = new ProtocolMessage<>("SelectMap", body);

    JsonMessageCodec codec = new JsonMessageCodec();
    sendToClient(selectorId, codec.encode(protocolMessage));
  }

  synchronized int assignClientId() {
    return nextClientId++;
  }

  synchronized boolean trySelectFigure(int clientId, PlayerValuesBody selection) {
    for (var entry : playerSelections.entrySet()) {
      int ownerId = entry.getKey();
      int selectedFigure = entry.getValue().figure();

      if (ownerId != clientId && selectedFigure == selection.figure()) {
        return false;
      }
    }
    playerSelections.put(clientId, selection);
    return true;
  }

  public synchronized List<PlayerAddedBody> getSelectedPlayers() {
    List<PlayerAddedBody> players = new ArrayList<>();
    for (var entry : playerSelections.entrySet()) {

      players.add(
          new PlayerAddedBody(entry.getKey(), entry.getValue().name(), entry.getValue().figure()));
    }
    return players;
  }

  synchronized void removePlayerSelection(int clientId) {
    playerSelections.remove(clientId);
    playerReadyStates.remove(clientId);
    readyPlayerOrder.remove(clientId);
  }

  synchronized boolean trySetReady(int clientId, boolean ready) {
    if (!playerSelections.containsKey(clientId)) {
      return false;
    }

    playerReadyStates.put(clientId, ready);

    if (ready) {
      readyPlayerOrder.add(clientId);
    } else {
      readyPlayerOrder.remove(clientId);
    }

    return true;
  }

  synchronized boolean isPlayerReady(int clientId) {
    return playerReadyStates.getOrDefault(clientId, false);
  }

  synchronized List<PlayerStatusBody> getPlayerStatuses() {
    List<PlayerStatusBody> statuses = new ArrayList<>();

    for (int clientId : playerSelections.keySet()) {
      statuses.add(new PlayerStatusBody(clientId, isPlayerReady(clientId)));
    }
    return statuses;
  }

  synchronized Integer getMapSelectorId() {
    if (readyPlayerOrder.isEmpty()) {
      return null;
    }
    return readyPlayerOrder.iterator().next();
  }

  synchronized boolean trySelectMap(int clientId, String map) {
    Integer selectorId = getMapSelectorId();

    if (selectorId == null || selectorId.intValue() != clientId) {
      return false;
    }

    if (map == null || !availableMaps.contains(map)) {
      return false;
    }
    selectedMap = map;
    return true;
  }

  synchronized String getSelectedMap() {
    return selectedMap;
  }
}
