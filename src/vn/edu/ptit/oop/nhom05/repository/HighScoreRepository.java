package vn.edu.ptit.oop.nhom05.repository;

import vn.edu.ptit.oop.nhom05.exception.HighScoreException;
import vn.edu.ptit.oop.nhom05.model.ScoreRecord;

import java.util.List;

public interface HighScoreRepository {

    int TOP_SIZE = 10;

    void save(ScoreRecord record) throws HighScoreException;

    List<ScoreRecord> findAll() throws HighScoreException;

    default List<ScoreRecord> findTop(int n) throws HighScoreException {
        if (n < 0) {
            throw new IllegalArgumentException("n không được âm");
        }
        return findAll().stream().sorted().limit(n).toList();
    }

    default List<ScoreRecord> findRecent(int n) throws HighScoreException {
        if (n < 0) {
            throw new IllegalArgumentException("n không được âm");
        }
        List<ScoreRecord> all = findAll();
        return all.subList(Math.max(0, all.size() - n), all.size());
    }
}
