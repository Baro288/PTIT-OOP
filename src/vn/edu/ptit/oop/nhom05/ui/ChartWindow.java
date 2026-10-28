package vn.edu.ptit.oop.nhom02.ui;

import vn.edu.ptit.oop.nhom02.repository.HighScoreRepository;
import vn.edu.ptit.oop.nhom02.ui.panels.ScoreChartsPanel;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.KeyStroke;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.net.URL;

public class ChartWindow extends JFrame {

    private final ScoreChartsPanel charts;

    public ChartWindow(HighScoreRepository repository) {
        super("Biểu đồ điểm số");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        loadIcon();

        charts = new ScoreChartsPanel(repository);
        setLayout(new BorderLayout());
        add(charts, BorderLayout.CENTER);

        getRootPane().registerKeyboardAction(e -> dispose(),
                KeyStroke.getKeyStroke("ESCAPE"), JComponent.WHEN_IN_FOCUSED_WINDOW);

        setMinimumSize(new Dimension(560, 380));
        setSize(820, 520);
        setLocationRelativeTo(null);
    }

    private void loadIcon() {
        URL url = ChartWindow.class.getResource("/resources/icons/app.png");
        if (url != null) {
            setIconImage(new ImageIcon(url).getImage());
        }
    }

    public void refresh() {
        charts.refresh();
    }
}
