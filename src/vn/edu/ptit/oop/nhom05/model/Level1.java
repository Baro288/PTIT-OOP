package vn.edu.ptit.oop.nhom05.model;

import java.util.ArrayList;
import java.util.List;

public class Level1 extends AbstractLevel {

    public Level1() {
        super(1, "Đồng cỏ", "Không có tường — đi qua mép bàn sẽ ra ở phía đối diện");
    }

    @Override
    public List<Obstacle> createObstacles() {
        return new ArrayList<>();
    }

    @Override
    public boolean wrapsAround() {
        return true;
    }
}
