package vn.edu.ptit.oop.nhom05.model;

import java.awt.Color;
import java.util.Objects;

public class Player {

    private final int number;
    private final String name;
    private final Snake snake;
    private int score;
    private boolean alive = true;

    public Player(int number, String name, Snake snake) {
        if (number < 1) {
            throw new IllegalArgumentException("Số thứ tự người chơi phải >= 1");
        }
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Tên người chơi không được rỗng");
        }
        this.number = number;
        this.name = name;
        this.snake = Objects.requireNonNull(snake, "snake không được null");
    }

    public void addScore(int points) {
        if (points < 0) {
            throw new IllegalArgumentException("Điểm cộng thêm không được âm");
        }
        score += points;
    }

    public void die() {
        alive = false;
    }

    public int getNumber() { return number; }
    public String getName() { return name; }
    public Snake getSnake() { return snake; }
    public int getScore() { return score; }
    public boolean isAlive() { return alive; }
    public Color getColor() { return snake.getColor(); }
}
