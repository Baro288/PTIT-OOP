package vn.edu.ptit.oop.nhom05.service;

import vn.edu.ptit.oop.nhom05.model.Direction;

public interface GameControls {

    void changeDirection(int playerIndex, Direction d);

    default void changeDirection(Direction d) {
        changeDirection(0, d);
    }

    void togglePause();

    void startOrRestart();
}
