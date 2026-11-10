# JSON protocol proposal

## Scope

This M2 proposal describes the connection, lobby, setup and gameplay flows.
The examples use RoboRally protocol version 0.1 as their reference.

## Encoding and message framing

Protocol 0.1 uses TCP and UTF-8. Each JSON object contains one message
with a `messageType` and a `messageBody`.

Our proposed framing is one JSON object per line. The sender writes the
complete object on a single line, followed by a newline (`\n`). The receiver
reads one line and parses it as one JSON message.

Protocol 0.1 leaves message boundaries unspecified. We still need to
check whether other implementations use the same newline delimiter.

## Connection setup

### Server to client — HelloClient

After the TCP connection is established, the server sends `HelloClient`
to tell the client which protocol version it uses.

```json
{
  "messageType": "HelloClient",
  "messageBody": {
    "protocol": "Version 0.1"
  }
}
```

### Client to server — HelloServer

The client replies with its group name, whether it is AI-controlled,
and the protocol version it uses.

```json
{
  "messageType": "HelloServer",
  "messageBody": {
    "group": "TolleTrolle",
    "isAI": false,
    "protocol": "Version 0.1"
  }
}
```

### Server to client — Welcome

If the server supports the client's protocol version, it assigns the client
a unique positive ID and sends that ID in a `Welcome` message.

```json
{
  "messageType": "Welcome",
  "messageBody": {
    "clientID": 42
  }
}
```

## Connection monitoring

### Server to client and back — Alive

Every five seconds, the server sends an `Alive` message to the client.
The client replies with the same message.

```json
{
  "messageType": "Alive",
  "messageBody": {}
}
```

## Lobby

### Client to server — PlayerValues

The player chooses a display name and a figure. Players may share a name,
but each figure can belong to only one player at a time.

```json
{
  "messageType": "PlayerValues",
  "messageBody": {
    "name": "Hakan",
    "figure": 5
  }
}
```

### Server to all clients — PlayerAdded

When the server accepts a player's name and figure, it broadcasts
`PlayerAdded` to all connected clients. A client joining later must
also be informed about the players already in the lobby.

```json
{
  "messageType": "PlayerAdded",
  "messageBody": {
    "clientID": 42,
    "name": "Hakan",
    "figure": 5
  }
}
```

### Client to server — SetStatus

After the server accepts the player's selection, the client sends `ready: true`
when the player is ready. The player can send `ready: false` to withdraw readiness.

```json
{
  "messageType": "SetStatus",
  "messageBody": {
    "ready": true
  }
}
```

### Server to all clients — PlayerStatus

The server broadcasts the player's ID and current readiness to all connected
clients. Clients that join later must also receive the current status.

```json
{
  "messageType": "PlayerStatus",
  "messageBody": {
    "clientID": 42,
    "ready": true
  }
}
```

### Server to the first ready player — SelectMap

The server sends the available maps to the first player who signals readiness.
If that player withdraws readiness or leaves, the next ready player gets to choose.

```json
{
  "messageType": "SelectMap",
  "messageBody": {
    "availableMaps": ["Dizzy Highway"]
  }
}
```

### Choosing client to server, then server to all players — MapSelected

The client replies with the selected map. The server forwards this message
to all players so everyone knows which map was chosen.

```json
{
  "messageType": "MapSelected",
  "messageBody": {
    "map": "Dizzy Highway"
  }
}
```
## Game setup

### Server to all clients — GameStarted

Once a map has been selected and all players are ready (at least two players),
the server sends the board layout to all clients. The example below shows
one cell from a map to illustrate the structure of `gameMap`.

```json
{
  "messageType": "GameStarted",
  "messageBody": {
    "gameMap": [
      [
        [
          { "type": "Wall", "isOnBoard": "4A", "orientations": ["top", "right"] }
        ]
      ]
    ]
  }
}
```
### Server to all clients — ActivePhase and CurrentPlayer

During setup, `ActivePhase` identifies the setup phase with the value `0`.

```json
{
  "messageType": "ActivePhase",
  "messageBody": {
    "phase": 0
  }
}
```

`CurrentPlayer` identifies the player whose turn it is. The first player
to connect makes the first starting-point choice.

```json
{
  "messageType": "CurrentPlayer",
  "messageBody": {
    "clientID": 42
  }
}
```

### Current player to server — SetStartingPoint

During setup, the current player requests a starting position.

```json
{
  "messageType": "SetStartingPoint",
  "messageBody": {
    "x": 4,
    "y": 2
  }
}
```

### Server to all clients — StartingPointTaken

If the position is valid, the server tells all clients which player
has taken that starting position.

```json
{
  "messageType": "StartingPointTaken",
  "messageBody": {
    "x": 4,
    "y": 2,
    "clientID": 42
  }
}
```
## Programming phase

### Server to the player drawing cards — YourCards

The player draws nine programming cards. Only that player receives the card names.

```json
{
  "messageType": "YourCards",
  "messageBody": {
    "cardsInHand": [
      "MoveI", "TurnRight", "MoveI", "TurnLeft", "MoveII",
      "BackUp", "UTurn", "PowerUp", "Again"
    ]
  }
}
```

### Server to the other players — NotYourCards

The other players receive the drawing player's ID and the number of cards drawn.

```json
{
  "messageType": "NotYourCards",
  "messageBody": {
    "clientID": 42,
    "cardsInHand": 9
  }
}
```
### Client to server — SelectedCard

The player selects one card at a time and specifies its register.

```json
{
  "messageType": "SelectedCard",
  "messageBody": {
    "card": "MoveI",
    "register": 3
  }
}
```

### Server to all clients — CardSelected

The server announces which player's register was filled or cleared.
The selected card stays private.

```json
{
  "messageType": "CardSelected",
  "messageBody": {
    "clientID": 42,
    "register": 3,
    "filled": true
  }
}
```
### Server to all clients — SelectionFinished

Once a player has filled all five registers, their program is locked.
The server tells all clients which player finished.

```json
{
  "messageType": "SelectionFinished",
  "messageBody": {
    "clientID": 42
  }
}
```

### Server to all clients — TimerStarted

When the first player finishes, the server starts a 30-second timer
and notifies all clients.

```json
{
  "messageType": "TimerStarted",
  "messageBody": {}
}
```

### Server to all clients — TimerEnded

When the 30-second timer expires, the server lists the IDs of the players
who have not finished their programs.

```json
{
  "messageType": "TimerEnded",
  "messageBody": {
    "clientIDs": [17]
  }
}
```

### Server to the affected player — CardsYouGotNow

The player discards their remaining hand. Blindly drawn cards fill the
empty registers in the order given by the server.

```json
{
  "messageType": "CardsYouGotNow",
  "messageBody": {
    "cards": ["MoveI", "TurnRight"]
  }
}
```

## Activation phase

### Server to all clients — CurrentCards

At the start of each register, the server reveals each player's card for
that register. Cards in later registers stay hidden until their turn.

```json
{
  "messageType": "CurrentCards",
  "messageBody": {
    "activeCards": [
      { "clientID": 42, "card": "MoveI" },
      { "clientID": 17, "card": "TurnRight" }
    ]
  }
}
```

### Server to all clients — ReplaceCard

A card can be replaced during activation. In this example, player 42's
`Spam` card in register 3 is replaced with `MoveI`.

```json
{
  "messageType": "ReplaceCard",
  "messageBody": {
    "register": 3,
    "newCard": "MoveI",
    "clientID": 42
  }
}
```

## Gameplay events

### Server to all clients — Movement

When a robot moves between board cells, the server reports its new position.
In this example, player 42's robot moves to (4, 2).

```json
{
  "messageType": "Movement",
  "messageBody": {
    "clientID": 42,
    "x": 4,
    "y": 2
  }
}
```

### Server to all clients — PlayerTurning

The server identifies the player whose robot turns and reports the rotation
as `clockwise` or `counterclockwise`.

```json
{
  "messageType": "PlayerTurning",
  "messageBody": {
    "clientID": 42,
    "rotation": "clockwise"
  }
}
```

### Server to all clients — CheckPointReached

The server reports the player's checkpoint progress. A value of `3` means
the player has reached checkpoints 1, 2 and 3 in order.

```json
{
  "messageType": "CheckPointReached",
  "messageBody": {
    "clientID": 42,
    "number": 3
  }
}
```

### Server to all clients — GameFinished

When a player reaches the final checkpoint, the server announces the winner.

```json
{
  "messageType": "GameFinished",
  "messageBody": {
    "clientID": 42
  }
}
```