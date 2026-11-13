package io.github.hakanmtn.roborally.protocol;

public record ProtocolMessage<T>(String messageType, T messageBody) {}
