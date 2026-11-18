package io.github.hakanmtn.roborally.protocol;

public record ReceivedChatBody(String message, int from, boolean isPrivate) {}
