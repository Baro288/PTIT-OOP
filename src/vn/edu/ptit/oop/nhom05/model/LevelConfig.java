package vn.edu.ptit.oop.nhom05.model;

import java.util.List;

public interface LevelConfig {

    int getNumber();

    String getName();

    String getDescription();

    List<Obstacle> createObstacles();

    default boolean wrapsAround() {
        return false;
    }
}
