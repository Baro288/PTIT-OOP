package vn.edu.ptit.oop.nhom02.ui.panels;

import vn.edu.ptit.oop.nhom02.exception.HighScoreException;
import vn.edu.ptit.oop.nhom02.model.LevelConfig;
import vn.edu.ptit.oop.nhom02.model.Levels;
import vn.edu.ptit.oop.nhom02.model.ScoreRecord;
import vn.edu.ptit.oop.nhom02.repository.HighScoreRepository;
import vn.edu.ptit.oop.nhom02.service.ScoreStatistics;
import vn.edu.ptit.oop.nhom02.util.ChartHelper;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.DefaultCategoryDataset;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class ScoreChartsPanel extends JPanel {

    private static final Color INFO_COLOR = new Color(60, 60, 60);
    private static final Color ERROR_COLOR = new Color(190, 40, 40);
    private static final Locale VIETNAMESE = Locale.forLanguageTag("vi-VN");

    private final HighScoreRepository repository;
    private final List<LevelConfig> levels;
    private final DefaultCategoryDataset recentDataset;
    private final DefaultCategoryDataset averageDataset;
    private final JTabbedPane tabs;
    private final JLabel summary;
    private final JButton reloadButton;

    public ScoreChartsPanel(HighScoreRepository repository) {
        super(new BorderLayout());
        this.repository = Objects.requireNonNull(repository, "repository không được null");
        this.levels = Levels.all();
        this.recentDataset = new DefaultCategoryDataset();
        this.averageDataset = new DefaultCategoryDataset();

        JFreeChart recentChart = ChartHelper.createRecentScoresChart(recentDataset);
        JFreeChart averageChart = ChartHelper.createAverageByLevelChart(averageDataset);

        tabs = new JTabbedPane();
        tabs.addTab("Điểm các lượt gần nhất", wrap(recentChart));
        tabs.addTab("Điểm trung bình theo màn", wrap(averageChart));

        summary = new JLabel(" ");
        summary.setBorder(BorderFactory.createEmptyBorder(6, 10, 6, 10));

        reloadButton = new JButton("Làm mới");
        reloadButton.setToolTipText("Đọc lại bảng điểm từ file");
        reloadButton.setFocusable(false);
        reloadButton.addActionListener(e -> refresh());
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 2));
        buttons.add(reloadButton);

        JPanel south = new JPanel(new BorderLayout());
        south.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Color.LIGHT_GRAY));
        south.add(summary, BorderLayout.CENTER);
        south.add(buttons, BorderLayout.EAST);

        add(tabs, BorderLayout.CENTER);
        add(south, BorderLayout.SOUTH);

        refresh();
    }

    private static ChartPanel wrap(JFreeChart chart) {
        ChartPanel panel = new ChartPanel(chart);
        panel.setDomainZoomable(false);
        panel.setRangeZoomable(false);
        panel.setMouseWheelEnabled(false);
        panel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        return panel;
    }

    public void refresh() {
        try {
            ScoreStatistics stats = ScoreStatistics.of(repository.findAll());
            ChartHelper.fillRecentScores(recentDataset, stats, ChartHelper.RECENT_GAMES);
            ChartHelper.fillAverageByLevel(averageDataset, stats, levels);
            showSummary(stats);
        } catch (HighScoreException ex) {
            summary.setForeground(ERROR_COLOR);
            summary.setText("Không đọc được bảng điểm: " + ex.getMessage());
        }
    }

    private void showSummary(ScoreStatistics stats) {
        summary.setForeground(INFO_COLOR);
        if (stats.isEmpty()) {
            summary.setText("Chưa có ván nào được lưu.");
            return;
        }
        ScoreRecord last = stats.getLatest();
        summary.setText(String.format(VIETNAMESE, "Tổng %d ván  •  Cao nhất %d  •  Trung bình %.1f  •  Ván gần nhất: %s – %d điểm (màn %d)",
                stats.getTotalGames(), stats.getBestScore(), stats.getAverageScore(),
                last.getName(), last.getScore(), last.getLevel()));
    }
}
