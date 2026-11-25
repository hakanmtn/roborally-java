package io.github.hakanmtn.roborally.protocol;

import com.google.gson.JsonArray;

public record GameStartedBody(JsonArray gameMap) {}
