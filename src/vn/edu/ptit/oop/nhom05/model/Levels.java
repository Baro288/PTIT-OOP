package vn.edu.ptit.oop.nhom05.model;

import java.util.Collections;
import java.util.List;

public final class Levels {

    private Levels() {
    }

    public static List<LevelConfig> all() {
        return Collections.unmodifiableList(List.of(new Level1(), new Level2(), new Level3()));
    }

    public static LevelConfig byNumber(int number) {
        for (LevelConfig level : all()) {
            if (level.getNumber() == number) {
                return level;
            }
        }
        throw new IllegalArgumentException("Không có màn số " + number);
    }

    public static LevelConfig first() {
        return new Level1();
    }
}
