# roborally-java

A Java implementation of RoboRally with a TCP chat server and a JavaFX client.

I first worked on RoboRally as part of a team in the Software Engineering
Practicum (Softwareentwicklungspraktikum) at LMU Munich. I’m now rebuilding
the game independently to practice object-oriented design and automated
testing, and to explore game AI as the project develops.

## Documentation

- [Game rules](docs/game-rules.md)
- [Domain model and design decisions](docs/domain-model.md)
- [UML class diagram](docs/domain-model-diagram.md)
- [JSON protocol proposal](docs/json-protocol.md)
- [Architecture decisions](docs/architecture-decisions.md)

## Build

With JDK 22 installed, run the following command from the project root:

```bash
./mvnw clean compile
```

On Windows, use `.\mvnw.cmd clean compile`.

## Run the desktop client

From the project root, start the JavaFX client:

```bash
./mvnw javafx:run
```

On Windows, use `.\mvnw.cmd javafx:run`.

### Verify chat with two clients

1. Start `io.github.hakanmtn.roborally.server.ChatServer` from IntelliJ IDEA.
2. Open two terminals in the project root and run `./mvnw javafx:run` in each.
   On Windows, use `.\mvnw.cmd javafx:run` in PowerShell.
3. Send a different message from each window. Both windows should show
   each message once.
4. Close one window and send another message from the remaining client.
   The message should still appear in its chat history.