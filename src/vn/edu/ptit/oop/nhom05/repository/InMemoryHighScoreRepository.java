package vn.edu.ptit.oop.nhom05.repository;

import vn.edu.ptit.oop.nhom05.model.ScoreRecord;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class InMemoryHighScoreRepository implements HighScoreRepository {

    private final List<ScoreRecord> records = new ArrayList<>();

    @Override
    public void save(ScoreRecord record) {
        records.add(Objects.requireNonNull(record, "record không được null"));
    }

    @Override
    public List<ScoreRecord> findAll() {
        return Collections.unmodifiableList(new ArrayList<>(records));
    }
}
