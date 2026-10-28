package vn.edu.ptit.oop.nhom05.model;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractLevel implements LevelConfig {

    protected static final char WALL = '#';

    private final int number;
    private final String name;
    private final String description;

    protected AbstractLevel(int number, String name, String description) {
        if (number < 1) {
            throw new IllegalArgumentException("Số màn phải >= 1");
        }
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Tên màn không được rỗng");
        }
        if (description == null || description.isBlank()) {
            throw new IllegalArgumentException("Mô tả màn không được rỗng");
        }
        this.number = number;
        this.name = name;
        this.description = description;
    }

    @Override
    public int getNumber() { return number; }

    @Override
    public String getName() { return name; }

    @Override
    public String getDescription() { return description; }

    @Override
    public abstract List<Obstacle> createObstacles();

    protected static List<Obstacle> obstaclesFromLayout(String... rows) {
        if (rows == null || rows.length != GameConfig.ROWS) {
            throw new IllegalArgumentException("Sơ đồ phải có đúng " + GameConfig.ROWS + " hàng");
        }
        List<Obstacle> result = new ArrayList<>();
        for (int y = 0; y < rows.length; y++) {
            if (rows[y] == null || rows[y].length() != GameConfig.COLS) {
                throw new IllegalArgumentException(
                    "Hàng " + y + " của sơ đồ phải dài đúng " + GameConfig.COLS + " ký tự");
            }
            for (int x = 0; x < GameConfig.COLS; x++) {
                if (rows[y].charAt(x) == WALL) {
                    result.add(new Obstacle(new Point(x, y)));
                }
            }
        }
        return result;
    }

    @Override
    public String toString() {
        return "Màn " + number + " – " + name;
    }
}
