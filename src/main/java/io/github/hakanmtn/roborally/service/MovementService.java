package io.github.hakanmtn.roborally.service;

import io.github.hakanmtn.roborally.model.Game;
import io.github.hakanmtn.roborally.model.board.Board;

import java.util.Objects;

public class MovementService {

    private final Board board;
    private final Game game;

    public MovementService(Board board, Game game) {
        this.board = Objects.requireNonNull(board, "Board muss not be null");
        this.game = Objects.requireNonNull(game, "Game muss not be null");
    }
}
