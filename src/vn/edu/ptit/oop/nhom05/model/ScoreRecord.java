package vn.edu.ptit.oop.nhom05.model;

import vn.edu.ptit.oop.nhom05.exception.InvalidScoreRecordException;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

public final class ScoreRecord implements Comparable<ScoreRecord> {

    public static final int MAX_NAME_LENGTH = 20;

    private final String name;
    private final int score;
    private final int level;
    private final LocalDateTime date;

    public ScoreRecord(String name, int score, int level) {
        this(name, score, level, LocalDateTime.now());
    }

    public ScoreRecord(String name, int score, int level, LocalDateTime date) {
        this.name = normalizeName(name);
        if (score < 0) {
            throw new InvalidScoreRecordException("Điểm không được âm (nhận được " + score + ")");
        }
        if (level < 1) {
            throw new InvalidScoreRecordException("Màn chơi phải từ 1 trở lên (nhận được " + level + ")");
        }
        if (date == null) {
            throw new InvalidScoreRecordException("Thiếu thời điểm chơi");
        }
        this.score = score;
        this.level = level;
        this.date = date.truncatedTo(ChronoUnit.SECONDS);
    }

    public static String normalizeName(String raw) {
        if (raw == null) {
            throw new InvalidScoreRecordException("Vui lòng nhập tên");
        }
        for (int i = 0; i < raw.length(); i++) {
            char c = raw.charAt(i);
            if (Character.isISOControl(c) && !Character.isWhitespace(c)) {
                throw new InvalidScoreRecordException("Tên chứa ký tự không hợp lệ");
            }
        }
        String cleaned = raw.trim().replaceAll("\\s+", " ");
        if (cleaned.isEmpty()) {
            throw new InvalidScoreRecordException("Vui lòng nhập tên");
        }
        if (cleaned.length() > MAX_NAME_LENGTH) {
            throw new InvalidScoreRecordException("Tên tối đa " + MAX_NAME_LENGTH + " ký tự (hiện có " + cleaned.length() + ")");
        }
        return cleaned;
    }

    public String getName() { return name; }
    public int getScore() { return score; }
    public int getLevel() { return level; }
    public LocalDateTime getDate() { return date; }

    @Override
    public int compareTo(ScoreRecord other) {
        int byScore = Integer.compare(other.score, this.score);
        if (byScore != 0) {
            return byScore;
        }
        int byDate = this.date.compareTo(other.date);
        if (byDate != 0) {
            return byDate;
        }
        return this.name.compareToIgnoreCase(other.name);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ScoreRecord other)) {
            return false;
        }
        return score == other.score && level == other.level && name.equals(other.name) && date.equals(other.date);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, score, level, date);
    }

    @Override
    public String toString() {
        return name + " – " + score + " điểm (màn " + level + ", " + date + ")";
    }
}
