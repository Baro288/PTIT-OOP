package vn.edu.ptit.oop.nhom05.ui.panels;

import vn.edu.ptit.oop.nhom05.model.Direction;
import vn.edu.ptit.oop.nhom05.model.Food;
import vn.edu.ptit.oop.nhom05.model.GameConfig;
import vn.edu.ptit.oop.nhom05.model.GameObject;
import vn.edu.ptit.oop.nhom05.model.GameState;
import vn.edu.ptit.oop.nhom05.model.Obstacle;
import vn.edu.ptit.oop.nhom05.model.Player;
import vn.edu.ptit.oop.nhom05.service.GameControls;
import vn.edu.ptit.oop.nhom05.service.GameEngine;
import vn.edu.ptit.oop.nhom05.service.GameListener;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public class GameBoardPanel extends JPanel implements GameListener {

    public static final int HUD_HEIGHT = 40;

    private static final Color HUD_BG = new Color(38, 50, 40);
    private static final Color TILE_A = new Color(236, 244, 230);
    private static final Color TILE_B = new Color(226, 238, 219);
    private static final Color GRID_LINE = new Color(0, 0, 0, 16);
    private static final Color OVERLAY_BG = new Color(0, 0, 0, 150);

    private final GameControls controls;
    private final int cols;
    private final int rows;
    private final int cellSize;

    private final Map<GameState, String> hints = new EnumMap<>(GameState.class);

    private List<Player> players = Collections.emptyList();
    private List<Food> foods = Collections.emptyList();
    private List<Obstacle> obstacles = Collections.emptyList();
    private int level = 1;
    private String levelName;
    private GameState state = GameState.READY;
    private boolean twoPlayerMode;
    private Optional<Player> winner = Optional.empty();

    public GameBoardPanel(GameControls controls) {
        this.controls = Objects.requireNonNull(controls, "controls không được null");
        this.cols = GameConfig.COLS;
        this.rows = GameConfig.ROWS;
        this.cellSize = GameConfig.CELL_SIZE;

        hints.put(GameState.READY, "Nhấn SPACE để bắt đầu");
        hints.put(GameState.PAUSED, "Nhấn P (hoặc ESC) để tiếp tục");
        hints.put(GameState.GAME_OVER, "Nhấn SPACE để chơi lại");

        setPreferredSize(new Dimension(cols * cellSize, HUD_HEIGHT + rows * cellSize));
        setBackground(TILE_A);
        setFocusable(true);
        installKeyBindings();
    }

    @Override
    public void onUpdate(GameEngine engine) {
        this.players = engine.getPlayers();
        this.foods = engine.getFoods();
        this.obstacles = engine.getObstacles();
        this.level = engine.getLevel();
        this.levelName = engine.getLevelConfig().getName();
        this.state = engine.getState();
        this.twoPlayerMode = engine.isTwoPlayerMode();
        this.winner = engine.getWinner();
        repaint();
    }

    private void installKeyBindings() {
        bindAction("p1.up", () -> controls.changeDirection(0, Direction.UP), "UP");
        bindAction("p1.down", () -> controls.changeDirection(0, Direction.DOWN), "DOWN");
        bindAction("p1.left", () -> controls.changeDirection(0, Direction.LEFT), "LEFT");
        bindAction("p1.right", () -> controls.changeDirection(0, Direction.RIGHT), "RIGHT");
        bindAction("p2.up", () -> controls.changeDirection(1, Direction.UP), "W");
        bindAction("p2.down", () -> controls.changeDirection(1, Direction.DOWN), "S");
        bindAction("p2.left", () -> controls.changeDirection(1, Direction.LEFT), "A");
        bindAction("p2.right", () -> controls.changeDirection(1, Direction.RIGHT), "D");
        bindAction("game.pause", controls::togglePause, "P", "ESCAPE");
        bindAction("game.start", controls::startOrRestart, "SPACE", "ENTER");
    }

    private void bindAction(String actionName, Runnable action, String... keys) {
        InputMap inputMap = getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap actionMap = getActionMap();
        actionMap.put(actionName, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                action.run();
            }
        });
        for (String key : keys) {
            KeyStroke ks = KeyStroke.getKeyStroke(key);
            if (ks == null) {
                throw new IllegalStateException("Tên phím không hợp lệ: " + key);
            }
            inputMap.put(ks, actionName);
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        try {
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            drawHud(g2);

            g2.translate(0, HUD_HEIGHT);
            drawGrid(g2);
            drawAll(g2, obstacles);
            drawAll(g2, foods);
            for (Player p : players) {
                p.getSnake().draw(g2, cellSize);
            }
            drawOverlay(g2);
        } finally {
            g2.dispose();
        }
    }

    private void drawAll(Graphics2D g2, Collection<? extends GameObject> objects) {
        for (GameObject o : objects) {
            o.draw(g2, cellSize);
        }
    }

    private void drawGrid(Graphics2D g2) {
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                g2.setColor(((r + c) & 1) == 0 ? TILE_A : TILE_B);
                g2.fillRect(c * cellSize, r * cellSize, cellSize, cellSize);
            }
        }
        g2.setColor(GRID_LINE);
        for (int c = 0; c <= cols; c++) {
            g2.drawLine(c * cellSize, 0, c * cellSize, rows * cellSize);
        }
        for (int r = 0; r <= rows; r++) {
            g2.drawLine(0, r * cellSize, cols * cellSize, r * cellSize);
        }
    }

    private void drawHud(Graphics2D g2) {
        int width = cols * cellSize;
        g2.setColor(HUD_BG);
        g2.fillRect(0, 0, width, HUD_HEIGHT);

        g2.setFont(new Font("Segoe UI", Font.BOLD, 17));
        FontMetrics fm = g2.getFontMetrics();
        int baseline = (HUD_HEIGHT - fm.getHeight()) / 2 + fm.getAscent();

        int x = 14;
        if (twoPlayerMode) {
            for (Player p : players) {
                String text = "P" + p.getNumber() + ": " + p.getScore();
                g2.setColor(p.getColor().brighter());
                g2.drawString(text, x, baseline);
                x += fm.stringWidth(text) + 18;
            }
        } else if (!players.isEmpty()) {
            g2.setColor(Color.WHITE);
            g2.drawString("Điểm: " + players.get(0).getScore(), x, baseline);
        }

        g2.setColor(Color.WHITE);
        String mid = "Màn " + level + (levelName == null ? "" : " – " + levelName);
        if (!twoPlayerMode && !players.isEmpty()) {
            mid += "   •   Dài: " + players.get(0).getSnake().getLength();
        }
        g2.drawString(mid, (width - fm.stringWidth(mid)) / 2, baseline);

        String stateText = state.getLabel();
        g2.setColor(state.isRunning() ? new Color(120, 220, 130) : new Color(255, 204, 80));
        g2.drawString(stateText, width - 14 - fm.stringWidth(stateText), baseline);
    }

    private void drawOverlay(Graphics2D g2) {
        String hint = hints.get(state);
        if (hint == null) {
            return;
        }
        int width = cols * cellSize;
        int height = rows * cellSize;
        g2.setColor(OVERLAY_BG);
        g2.fillRect(0, 0, width, height);

        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Segoe UI", Font.BOLD, 38));
        FontMetrics big = g2.getFontMetrics();
        String title = state.isGameOver() ? "GAME OVER" : state.getLabel().toUpperCase(Locale.ROOT);
        int y = height / 2 - 10;
        g2.drawString(title, (width - big.stringWidth(title)) / 2, y);

        g2.setFont(new Font("Segoe UI", Font.PLAIN, 17));
        FontMetrics small = g2.getFontMetrics();
        y += 14 + small.getAscent();
        for (String line : overlayLines(hint)) {
            g2.drawString(line, (width - small.stringWidth(line)) / 2, y);
            y += small.getHeight() + 2;
        }
    }

    private List<String> overlayLines(String hint) {
        List<String> lines = new ArrayList<>();
        if (state.isGameOver()) {
            if (twoPlayerMode) {
                lines.add(winner.map(p -> p.getName() + " thắng!").orElse("Hoà!"));
                StringBuilder scores = new StringBuilder();
                for (Player p : players) {
                    scores.append(scores.length() == 0 ? "" : "   •   ").append("P").append(p.getNumber()).append(": ").append(p.getScore());
                }
                lines.add(scores.toString());
            } else if (!players.isEmpty()) {
                lines.add("Điểm của bạn: " + players.get(0).getScore());
            }
        }
        if (state == GameState.READY) {
            lines.add(twoPlayerMode ? "P1 (xanh lá): ← ↑ ↓ →   •   P2 (xanh dương): W A S D"
                                    : "Điều khiển: ← ↑ ↓ → hoặc W A S D");
        }
        lines.add(hint);
        return lines;
    }
}
