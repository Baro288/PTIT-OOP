package vn.edu.ptit.oop.nhom05.ui;

import vn.edu.ptit.oop.nhom05.common.BaseWindow;
import vn.edu.ptit.oop.nhom05.exception.HighScoreException;
import vn.edu.ptit.oop.nhom05.model.GameState;
import vn.edu.ptit.oop.nhom05.model.LevelConfig;
import vn.edu.ptit.oop.nhom05.model.Player;
import vn.edu.ptit.oop.nhom05.model.Levels;
import vn.edu.ptit.oop.nhom05.model.ScoreRecord;
import vn.edu.ptit.oop.nhom05.repository.FileHighScoreRepository;
import vn.edu.ptit.oop.nhom05.repository.HighScoreRepository;
import vn.edu.ptit.oop.nhom05.service.GameEngine;
import vn.edu.ptit.oop.nhom05.service.GameListener;
import vn.edu.ptit.oop.nhom05.service.SoundPlayer;
import vn.edu.ptit.oop.nhom05.ui.dialogs.AboutDialog;
import vn.edu.ptit.oop.nhom05.ui.dialogs.GameOverDialog;
import vn.edu.ptit.oop.nhom05.ui.dialogs.HighScoreDialog;
import vn.edu.ptit.oop.nhom05.ui.panels.GameBoardPanel;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridBagLayout;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

public class MainWindow extends BaseWindow {

    private GameEngine engine;
    private GameBoardPanel boardPanel;
    private JRadioButtonMenuItem[] levelItems;
    private List<LevelConfig> levels;
    private HighScoreRepository scores;
    private String lastPlayerName;
    private ChartWindow chartWindow;
    private SoundPlayer soundPlayer;
    private JCheckBoxMenuItem twoPlayerItem;

    public MainWindow() {
        super("Nhóm 05 – Trò chơi Rắn săn mồi", 800, 720);
        setResizable(false);
        pack();
        setLocationRelativeTo(null);
    }

    @Override
    protected void initUI() {
        engine = new GameEngine();
        boardPanel = new GameBoardPanel(engine);

        engine.addGameListener(boardPanel);
        engine.addGameListener(e -> refreshStatus());
        scores = new FileHighScoreRepository(FileHighScoreRepository.defaultPath());
        lastPlayerName = "";
        engine.addGameListener(new ScoreRecorder());
        soundPlayer = new SoundPlayer();
        engine.addGameListener(soundPlayer);

        JPanel holder = new JPanel(new GridBagLayout());
        holder.setBackground(new Color(214, 226, 208));
        holder.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        holder.add(boardPanel);
        add(holder, BorderLayout.CENTER);

        buildLevelMenu();
        buildModeMenu();
        buildSoundMenu();
        buildScoreMenu();
        buildChartMenu();

        boardPanel.onUpdate(engine);
        refreshStatus();
    }

    private void buildLevelMenu() {
        levels = Levels.all();
        levelItems = new JRadioButtonMenuItem[levels.size()];

        JMenu levelMenu = new JMenu("Màn chơi");
        levelMenu.setMnemonic(KeyEvent.VK_M);
        ButtonGroup group = new ButtonGroup();

        for (int i = 0; i < levels.size(); i++) {
            LevelConfig level = levels.get(i);
            JRadioButtonMenuItem item = new JRadioButtonMenuItem(level.toString());
            item.setToolTipText(level.getDescription());
            item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_1 + i, InputEvent.CTRL_DOWN_MASK));
            item.setSelected(level.getNumber() == engine.getLevel());
            item.addActionListener(e -> requestLevel(level));
            group.add(item);
            levelMenu.add(item);
            levelItems[i] = item;
        }
        viewMenu.add(levelMenu);
    }

    private void buildModeMenu() {
        twoPlayerItem = new JCheckBoxMenuItem("Chế độ 2 người chơi");
        twoPlayerItem.setMnemonic(KeyEvent.VK_N);
        twoPlayerItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_T, InputEvent.CTRL_DOWN_MASK));
        twoPlayerItem.setToolTipText("P1: phím mũi tên  •  P2: W A S D");
        twoPlayerItem.addActionListener(e -> requestTwoPlayerMode(twoPlayerItem.isSelected()));
        viewMenu.add(twoPlayerItem);
    }

    private void buildSoundMenu() {
        JCheckBoxMenuItem soundItem = new JCheckBoxMenuItem("Âm thanh", soundPlayer.isEnabled());
        soundItem.setMnemonic(KeyEvent.VK_A);
        soundItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_M, InputEvent.CTRL_DOWN_MASK));
        soundItem.addActionListener(e -> {
            soundPlayer.setEnabled(soundItem.isSelected());
            setStatus(soundItem.isSelected() ? "Đã bật âm thanh" : "Đã tắt âm thanh");
            boardPanel.requestFocusInWindow();
        });
        viewMenu.add(soundItem);
        viewMenu.addSeparator();
    }

    private void buildScoreMenu() {
        JMenuItem item = new JMenuItem("Bảng xếp hạng...");
        item.setMnemonic(KeyEvent.VK_B);
        item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_H, InputEvent.CTRL_DOWN_MASK));
        item.addActionListener(e -> openHighScores());
        fileMenu.insert(item, 0);
        fileMenu.insertSeparator(1);

        addToolButton("/resources/icons/trophy.png", "Bảng xếp hạng (Ctrl+H)", e -> openHighScores());
    }

    private void buildChartMenu() {
        JMenuItem item = new JMenuItem("Biểu đồ điểm...");
        item.setMnemonic(KeyEvent.VK_D);
        item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_G, InputEvent.CTRL_DOWN_MASK));
        item.addActionListener(e -> openCharts());
        viewMenu.add(item);

        addToolButton("/resources/icons/chart.png", "Biểu đồ điểm (Ctrl+G)", e -> openCharts());
    }

    private void requestLevel(LevelConfig target) {
        if (target.getNumber() == engine.getLevel() && engine.getState() == GameState.READY) {
            return;
        }
        if (engine.getState().isRunning()) {
            engine.togglePause();
        }
        if (engine.getState() == GameState.PAUSED) {
            int choice = JOptionPane.showConfirmDialog(this,
                    "Đổi sang " + target + " sẽ bỏ ván đang chơi (" + engine.getScore() + " điểm).\nBạn có muốn tiếp tục?",
                    "Đổi màn chơi", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
            if (choice != JOptionPane.YES_OPTION) {
                syncLevelMenu();
                return;
            }
        }
        engine.setLevel(target);
        syncLevelMenu();
        boardPanel.requestFocusInWindow();
    }

    private void requestTwoPlayerMode(boolean enable) {
        if (enable == engine.isTwoPlayerMode()) {
            return;
        }
        if (engine.getState().isRunning()) {
            engine.togglePause();
        }
        if (engine.getState() == GameState.PAUSED) {
            int choice = JOptionPane.showConfirmDialog(this,
                    "Đổi chế độ chơi sẽ bỏ ván đang chơi.\nBạn có muốn tiếp tục?",
                    "Đổi chế độ chơi", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
            if (choice != JOptionPane.YES_OPTION) {
                twoPlayerItem.setSelected(engine.isTwoPlayerMode());
                boardPanel.requestFocusInWindow();
                return;
            }
        }
        engine.setTwoPlayerMode(enable);
        twoPlayerItem.setSelected(engine.isTwoPlayerMode());
        boardPanel.requestFocusInWindow();
    }

    private void syncLevelMenu() {
        for (int i = 0; i < levels.size(); i++) {
            levelItems[i].setSelected(levels.get(i).getNumber() == engine.getLevel());
        }
    }

    @Override
    protected void showAbout() {
        new AboutDialog(this).setVisible(true);
        boardPanel.requestFocusInWindow();
    }

    private void openHighScores() {
        if (engine.getState().isRunning()) {
            engine.togglePause();
        }
        showHighScores(null);
    }

    private void showHighScores(ScoreRecord highlight) {
        try {
            List<ScoreRecord> all = scores.findAll();
            List<ScoreRecord> ranking = all.stream().sorted().toList();
            new HighScoreDialog(this, ranking, all.size(), highlight).setVisible(true);
        } catch (HighScoreException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Không đọc được bảng điểm", JOptionPane.ERROR_MESSAGE);
        }
        boardPanel.requestFocusInWindow();
    }

    private void openCharts() {
        if (engine.getState().isRunning()) {
            engine.togglePause();
        }
        if (chartWindow == null || !chartWindow.isDisplayable()) {
            chartWindow = new ChartWindow(scores);
            chartWindow.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosed(WindowEvent e) {
                    boardPanel.requestFocusInWindow();
                }
            });
            chartWindow.setLocationRelativeTo(this);
        } else {
            chartWindow.refresh();
        }
        chartWindow.setVisible(true);
        chartWindow.toFront();
    }

    private void refreshCharts() {
        if (chartWindow != null && chartWindow.isDisplayable()) {
            chartWindow.refresh();
        }
    }

    private void askForScore(int score, int level, String levelName) {
        if (!engine.getState().isGameOver()) {
            return;
        }
        Optional<ScoreRecord> result = new GameOverDialog(this, score, level, levelName, lastPlayerName).showAndGetResult();
        if (result.isEmpty()) {
            boardPanel.requestFocusInWindow();
            return;
        }
        ScoreRecord record = result.get();
        lastPlayerName = record.getName();
        try {
            scores.save(record);
            setStatus("Đã lưu điểm của " + record.getName() + ": " + record.getScore() + " điểm");
            refreshCharts();
        } catch (HighScoreException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage() + "\nĐiểm của ván này chưa được lưu.",
                    "Không lưu được điểm", JOptionPane.ERROR_MESSAGE);
            boardPanel.requestFocusInWindow();
            return;
        }
        showHighScores(record);
    }

    private final class ScoreRecorder implements GameListener {
        @Override
        public void onUpdate(GameEngine e) {
        }

        @Override
        public void onGameOver(GameEngine e) {
            if (e.isTwoPlayerMode()) {
                final String result = twoPlayerResult(e);
                SwingUtilities.invokeLater(() -> showTwoPlayerResult(result));
                return;
            }
            final int score = e.getScore();
            final int level = e.getLevel();
            final String levelName = e.getLevelConfig().getName();
            SwingUtilities.invokeLater(() -> askForScore(score, level, levelName));
        }
    }

    private static String twoPlayerResult(GameEngine e) {
        StringBuilder text = new StringBuilder(e.getWinner().map(p -> p.getName() + " thắng!").orElse("Hai người hoà!"));
        text.append("\n");
        for (Player p : e.getPlayers()) {
            text.append("\n").append(p.getName()).append(": ").append(p.getScore()).append(" điểm")
                .append(p.isAlive() ? "" : " (đã va chạm)");
        }
        return text.toString();
    }

    private void showTwoPlayerResult(String result) {
        if (!engine.getState().isGameOver()) {
            return;
        }
        JOptionPane.showMessageDialog(this, result + "\n\nVán 2 người chơi không được lưu vào bảng xếp hạng.",
                "Kết quả 2 người chơi", JOptionPane.INFORMATION_MESSAGE);
        boardPanel.requestFocusInWindow();
    }

    private void refreshStatus() {
        String speed = String.format(Locale.ROOT, "%.1f", 1000.0 / engine.getTickDelayMs());
        StringBuilder status = new StringBuilder("Trạng thái: " + engine.getState().getLabel()
                + "   |   " + engine.getLevelConfig());
        if (engine.isTwoPlayerMode()) {
            for (Player p : engine.getPlayers()) {
                status.append("   |   P").append(p.getNumber()).append(": ").append(p.getScore())
                      .append(" điểm, dài ").append(p.getSnake().getLength());
            }
        } else {
            status.append("   |   Điểm: ").append(engine.getScore())
                  .append("   |   Độ dài: ").append(engine.getSnake().getLength());
        }
        status.append("   |   Tốc độ: ").append(speed).append(" ô/giây");
        setStatus(status.toString());
    }
}
