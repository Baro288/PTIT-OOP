package vn.edu.ptit.oop.nhom02.service;

import vn.edu.ptit.oop.nhom02.model.ScoreRecord;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

public final class ScoreStatistics {

    private final List<ScoreRecord> history;
    private final Map<Integer, Double> averageByLevel;
    private final Map<Integer, Integer> gamesByLevel;

    private ScoreStatistics(List<ScoreRecord> history) {
        this.history = Collections.unmodifiableList(new ArrayList<>(history));

        Map<Integer, Long> sum = new TreeMap<>();
        Map<Integer, Integer> count = new TreeMap<>();
        for (ScoreRecord r : this.history) {
            sum.merge(r.getLevel(), (long) r.getScore(), Long::sum);
            count.merge(r.getLevel(), 1, Integer::sum);
        }
        Map<Integer, Double> avg = new TreeMap<>();
        for (Map.Entry<Integer, Long> e : sum.entrySet()) {
            avg.put(e.getKey(), e.getValue() / (double) count.get(e.getKey()));
        }
        this.averageByLevel = Collections.unmodifiableMap(avg);
        this.gamesByLevel = Collections.unmodifiableMap(count);
    }

    public static ScoreStatistics of(List<ScoreRecord> history) {
        return new ScoreStatistics(Objects.requireNonNull(history, "history không được null"));
    }

    public int getTotalGames() {
        return history.size();
    }

    public boolean isEmpty() {
        return history.isEmpty();
    }

    public int getBestScore() {
        int best = 0;
        for (ScoreRecord r : history) {
            best = Math.max(best, r.getScore());
        }
        return best;
    }

    public double getAverageScore() {
        if (history.isEmpty()) {
            return 0;
        }
        long sum = 0;
        for (ScoreRecord r : history) {
            sum += r.getScore();
        }
        return sum / (double) history.size();
    }

    public List<ScoreRecord> getHistory() {
        return history;
    }

    public List<ScoreRecord> getRecent(int n) {
        if (n < 0) {
            throw new IllegalArgumentException("n không được âm (nhận được " + n + ")");
        }
        return history.subList(Math.max(0, history.size() - n), history.size());
    }

    public int getFirstRecentNumber(int n) {
        return history.size() - getRecent(n).size() + 1;
    }

    public Map<Integer, Double> getAverageByLevel() {
        return averageByLevel;
    }

    public Map<Integer, Integer> getGamesByLevel() {
        return gamesByLevel;
    }

    public ScoreRecord getLatest() {
        return history.isEmpty() ? null : history.get(history.size() - 1);
    }
}
