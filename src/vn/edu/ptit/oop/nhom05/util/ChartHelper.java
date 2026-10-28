package vn.edu.ptit.oop.nhom02.util;

import vn.edu.ptit.oop.nhom02.model.LevelConfig;
import vn.edu.ptit.oop.nhom02.model.ScoreRecord;
import vn.edu.ptit.oop.nhom02.service.ScoreStatistics;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.StandardChartTheme;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.labels.StandardCategoryToolTipGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.chart.renderer.category.StandardBarPainter;
import org.jfree.data.category.DefaultCategoryDataset;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.Paint;
import java.awt.geom.Ellipse2D;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public final class ChartHelper {

    public static final int RECENT_GAMES = 10;

    public static final String LINE_SERIES = "Điểm";
    public static final String BAR_SERIES = "Điểm trung bình";

    private static final Color LINE_COLOR = new Color(46, 110, 60);
    private static final Color[] LEVEL_COLORS = {
            new Color(76, 160, 90),
            new Color(230, 160, 40),
            new Color(200, 70, 60)
    };
    private static final Color PLOT_BG = new Color(246, 249, 244);

    private static boolean themeInstalled = false;

    private ChartHelper() {
    }

    public static synchronized void installTheme() {
        if (themeInstalled) {
            return;
        }
        String family = pickFontFamily();
        StandardChartTheme theme = (StandardChartTheme) StandardChartTheme.createJFreeTheme();
        theme.setExtraLargeFont(new Font(family, Font.BOLD, 18));
        theme.setLargeFont(new Font(family, Font.BOLD, 14));
        theme.setRegularFont(new Font(family, Font.PLAIN, 12));
        theme.setSmallFont(new Font(family, Font.PLAIN, 11));
        ChartFactory.setChartTheme(theme);
        themeInstalled = true;
    }

    private static String pickFontFamily() {
        String[] families = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
        return Arrays.asList(families).contains("Segoe UI") ? "Segoe UI" : Font.SANS_SERIF;
    }

    private static NumberFormat oneDecimal() {
        NumberFormat f = NumberFormat.getNumberInstance(Locale.forLanguageTag("vi-VN"));
        f.setMaximumFractionDigits(1);
        f.setMinimumFractionDigits(0);
        return f;
    }

    public static JFreeChart createRecentScoresChart(DefaultCategoryDataset dataset) {
        installTheme();
        JFreeChart chart = ChartFactory.createLineChart(
                "Điểm các lượt chơi gần nhất",
                "Lượt chơi (cũ → mới)", "Điểm",
                dataset, PlotOrientation.VERTICAL,
                false, true, false);

        CategoryPlot plot = chart.getCategoryPlot();
        stylePlot(plot, "Chưa có điểm nào — hãy chơi một ván!");
        plot.getDomainAxis().setCategoryLabelPositions(CategoryLabelPositions.UP_45);

        LineAndShapeRenderer renderer = new LineAndShapeRenderer(true, true);
        renderer.setSeriesPaint(0, LINE_COLOR);
        renderer.setSeriesStroke(0, new BasicStroke(2.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        renderer.setSeriesShape(0, new Ellipse2D.Double(-4, -4, 8, 8));
        renderer.setDefaultItemLabelGenerator(new StandardCategoryItemLabelGenerator("{2}", oneDecimal()));
        renderer.setDefaultItemLabelsVisible(true);
        renderer.setDefaultToolTipGenerator(new StandardCategoryToolTipGenerator("Lượt {1}: {2} điểm", oneDecimal()));
        plot.setRenderer(renderer);
        return chart;
    }

    public static void fillRecentScores(DefaultCategoryDataset dataset, ScoreStatistics stats, int n) {
        dataset.setNotify(false);
        try {
            dataset.clear();
            List<ScoreRecord> recent = stats.getRecent(n);
            int number = stats.getFirstRecentNumber(n);
            for (ScoreRecord r : recent) {
                dataset.addValue(r.getScore(), LINE_SERIES, "#" + number);
                number++;
            }
        } finally {
            dataset.setNotify(true);
        }
    }

    public static JFreeChart createAverageByLevelChart(DefaultCategoryDataset dataset) {
        installTheme();
        JFreeChart chart = ChartFactory.createBarChart(
                "Điểm trung bình theo màn chơi",
                "Màn chơi", "Điểm trung bình",
                dataset, PlotOrientation.VERTICAL,
                false, true, false);

        CategoryPlot plot = chart.getCategoryPlot();
        stylePlot(plot, "Chưa có điểm nào — hãy chơi một ván!");

        BarRenderer renderer = new BarRenderer() {
            @Override
            public Paint getItemPaint(int row, int column) {
                return LEVEL_COLORS[column % LEVEL_COLORS.length];
            }
        };
        renderer.setBarPainter(new StandardBarPainter());
        renderer.setShadowVisible(false);
        renderer.setMaximumBarWidth(0.2);
        renderer.setDefaultItemLabelGenerator(new StandardCategoryItemLabelGenerator("{2}", oneDecimal()));
        renderer.setDefaultItemLabelsVisible(true);
        renderer.setDefaultToolTipGenerator(new StandardCategoryToolTipGenerator("{1}: {2} điểm trung bình", oneDecimal()));
        plot.setRenderer(renderer);
        return chart;
    }

    public static void fillAverageByLevel(DefaultCategoryDataset dataset, ScoreStatistics stats, List<LevelConfig> levels) {
        dataset.setNotify(false);
        try {
            dataset.clear();
            if (stats.isEmpty()) {
                return;
            }
            Map<Integer, Double> avg = stats.getAverageByLevel();
            Map<Integer, Integer> games = stats.getGamesByLevel();
            for (LevelConfig level : levels) {
                Double value = avg.get(level.getNumber());
                int count = games.getOrDefault(level.getNumber(), 0);
                String label = "Màn " + level.getNumber() + " – " + level.getName() + " (" + count + " ván)";
                dataset.addValue(value, BAR_SERIES, label);
            }
        } finally {
            dataset.setNotify(true);
        }
    }

    private static void stylePlot(CategoryPlot plot, String noDataMessage) {
        plot.setBackgroundPaint(PLOT_BG);
        plot.setRangeGridlinePaint(new Color(200, 210, 195));
        plot.setDomainGridlinesVisible(false);
        plot.setOutlineVisible(false);
        plot.setNoDataMessage(noDataMessage);

        NumberAxis range = (NumberAxis) plot.getRangeAxis();
        range.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
        range.setAutoRangeIncludesZero(true);
        range.setUpperMargin(0.15);
    }
}
