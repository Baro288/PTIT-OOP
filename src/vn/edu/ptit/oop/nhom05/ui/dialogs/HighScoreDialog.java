package vn.edu.ptit.oop.nhom05.ui.dialogs;

import vn.edu.ptit.oop.nhom05.model.ScoreRecord;
import vn.edu.ptit.oop.nhom05.repository.HighScoreRepository;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.util.List;
import java.util.Objects;

public class HighScoreDialog extends JDialog {

    private static final Color NEW_ROW_BG = new Color(255, 240, 170);
    private static final Color[] MEDAL = {
            new Color(184, 134, 11),
            new Color(110, 110, 120),
            new Color(160, 90, 40)
    };

    private final HighScoreTableModel model;
    private final JTable table;

    public HighScoreDialog(Frame owner, List<ScoreRecord> ranking, int totalGames, ScoreRecord highlight) {
        super(owner, "Bảng xếp hạng", true);
        Objects.requireNonNull(ranking, "ranking không được null");
        List<ScoreRecord> top = ranking.subList(0, Math.min(ranking.size(), HighScoreRepository.TOP_SIZE));
        model = new HighScoreTableModel(top);
        table = new JTable(model);
        configureTable(highlight);

        JLabel title = new JLabel("TOP " + HighScoreRepository.TOP_SIZE + " ĐIỂM CAO NHẤT", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setBorder(BorderFactory.createEmptyBorder(12, 12, 8, 12));

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createEmptyBorder(0, 14, 0, 14));
        scroll.getViewport().setBackground(Color.WHITE);
        scroll.setPreferredSize(new Dimension(520, table.getRowHeight() * HighScoreRepository.TOP_SIZE
                + table.getTableHeader().getPreferredSize().height + 4));

        JLabel info = new JLabel(infoText(top.size(), totalGames, highlight, model), SwingConstants.CENTER);
        info.setBorder(BorderFactory.createEmptyBorder(8, 12, 4, 12));

        JButton close = new JButton("Đóng");
        close.addActionListener(e -> dispose());
        getRootPane().setDefaultButton(close);
        getRootPane().registerKeyboardAction(e -> dispose(),
                KeyStroke.getKeyStroke("ESCAPE"), JComponent.WHEN_IN_FOCUSED_WINDOW);
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttons.add(close);

        JPanel south = new JPanel(new BorderLayout());
        south.add(info, BorderLayout.NORTH);
        south.add(buttons, BorderLayout.SOUTH);

        setLayout(new BorderLayout());
        add(title, BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);
        add(south, BorderLayout.SOUTH);

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        pack();
        setResizable(false);
        setLocationRelativeTo(owner);
    }

    private static String infoText(int shown, int totalGames, ScoreRecord highlight, HighScoreTableModel model) {
        if (totalGames == 0 || shown == 0) {
            return "Chưa có điểm nào được lưu — hãy chơi một ván!";
        }
        String text = "Hiển thị " + shown + " trong tổng số " + totalGames + " lượt chơi";
        if (highlight != null) {
            int idx = model.indexOf(highlight);
            text += idx >= 0 ? "  •  Lượt vừa rồi của bạn đứng hạng " + (idx + 1)
                             : "  •  Lượt vừa rồi chưa lọt vào top " + HighScoreRepository.TOP_SIZE;
        }
        return text;
    }

    private void configureTable(ScoreRecord highlight) {
        table.setRowHeight(26);
        table.setFillsViewportHeight(true);
        table.setShowVerticalLines(false);
        table.setRowSelectionAllowed(false);
        table.setFocusable(false);
        table.setAutoCreateRowSorter(false);
        JTableHeader header = table.getTableHeader();
        header.setReorderingAllowed(false);
        header.setFont(header.getFont().deriveFont(Font.BOLD));

        table.setDefaultRenderer(Integer.class, new RankingCellRenderer(highlight, model));
        table.setDefaultRenderer(String.class, new RankingCellRenderer(highlight, model));

        int[] widths = {60, 150, 70, 60, 150};
        for (int i = 0; i < widths.length; i++) {
            table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
        }
    }

    private static final class RankingCellRenderer extends DefaultTableCellRenderer {
        private final ScoreRecord highlight;
        private final HighScoreTableModel model;

        RankingCellRenderer(ScoreRecord highlight, HighScoreTableModel model) {
            this.highlight = highlight;
            this.model = model;
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                       boolean hasFocus, int row, int column) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            boolean numeric = value instanceof Integer;
            setHorizontalAlignment(numeric ? SwingConstants.CENTER : SwingConstants.LEFT);
            setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8));

            boolean isNew = highlight != null && highlight.equals(model.getRecordAt(row));
            Font base = table.getFont();
            boolean medal = row < MEDAL.length && (column == HighScoreTableModel.COL_RANK
                    || column == HighScoreTableModel.COL_NAME || column == HighScoreTableModel.COL_SCORE);

            setFont(isNew || medal ? base.deriveFont(Font.BOLD) : base);
            setForeground(medal ? MEDAL[row] : table.getForeground());
            if (isNew) {
                setBackground(NEW_ROW_BG);
            } else {
                setBackground(row % 2 == 0 ? Color.WHITE : new Color(244, 247, 242));
            }
            return this;
        }
    }
}
