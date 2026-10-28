package vn.edu.ptit.oop.nhom05.ui.dialogs;

import vn.edu.ptit.oop.nhom05.model.ScoreRecord;

import javax.swing.table.AbstractTableModel;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class HighScoreTableModel extends AbstractTableModel {

    public static final int COL_RANK = 0;
    public static final int COL_NAME = 1;
    public static final int COL_SCORE = 2;
    public static final int COL_LEVEL = 3;
    public static final int COL_DATE = 4;

    private static final String[] COLUMNS = {"Hạng", "Tên", "Điểm", "Màn", "Thời gian"};
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private final List<ScoreRecord> rows;

    public HighScoreTableModel(List<ScoreRecord> sortedRecords) {
        this.rows = new ArrayList<>(Objects.requireNonNull(sortedRecords, "sortedRecords không được null"));
    }

    public ScoreRecord getRecordAt(int row) {
        return rows.get(row);
    }

    public int indexOf(ScoreRecord record) {
        return rows.indexOf(record);
    }

    public List<ScoreRecord> getRecords() {
        return Collections.unmodifiableList(rows);
    }

    @Override public int getRowCount() { return rows.size(); }
    @Override public int getColumnCount() { return COLUMNS.length; }
    @Override public String getColumnName(int column) { return COLUMNS[column]; }

    @Override
    public Class<?> getColumnClass(int column) {
        return switch (column) {
            case COL_RANK, COL_SCORE, COL_LEVEL -> Integer.class;
            default -> String.class;
        };
    }

    @Override
    public Object getValueAt(int row, int column) {
        ScoreRecord r = rows.get(row);
        return switch (column) {
            case COL_RANK -> row + 1;
            case COL_NAME -> r.getName();
            case COL_SCORE -> r.getScore();
            case COL_LEVEL -> r.getLevel();
            case COL_DATE -> r.getDate().format(DATE_FORMAT);
            default -> throw new IllegalArgumentException("Cột không tồn tại: " + column);
        };
    }
}
