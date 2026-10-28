package vn.edu.ptit.oop.nhom05.model;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

public class Level2 extends AbstractLevel {

    public Level2() {
        super(2, "Tường viền", "Một vòng tường bao quanh bàn chơi");
    }

    @Override
    public List<Obstacle> createObstacles() {
        List<Obstacle> walls = new ArrayList<>();
        for (int y = 0; y < GameConfig.ROWS; y++) {
            for (int x = 0; x < GameConfig.COLS; x++) {
                boolean onBorder = x == 0 || y == 0 || x == GameConfig.COLS - 1 || y == GameConfig.ROWS - 1;
                if (onBorder) {
                    walls.add(new Obstacle(new Point(x, y)));
                }
            }
        }
        return walls;
    }
}
