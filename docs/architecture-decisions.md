# Architecture decisions

## Display chat messages when the server broadcasts them

The JavaFX client sends the user's text to the server. The server broadcasts
it to all connected clients, including the sender. Each client appends the
message to its chat history when it receives the broadcast.

We considered showing the message as soon as the user pressed Send. With
the current broadcast behavior, the sender would then see the same message
twice. Displaying it on receipt keeps one entry per broadcast. The trade-off
is that the sender waits for the server round trip before seeing the message.

Verified with two JavaFX clients: each window displayed each message once.

Related issue: [#7 — TCP chat and JavaFX UI](https://github.com/hakanmtn/roborally-java/issues/7).