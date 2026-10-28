package vn.edu.ptit.oop.nhom05.model;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;

public class PoisonFood extends Food {

    public static final int SHRINK_BY = 2;
    public static final int LIFETIME_MS = 7000;
    private static final int BLINK_WHEN_LEFT_MS = 1500;

    private int remainingMs = LIFETIME_MS;

    public PoisonFood(Point position) {
        super(position, new Color(142, 68, 173), 0);
    }

    @Override
    public void applyTo(Snake snake) {
        snake.shrink(SHRINK_BY);
    }

    @Override
    public SoundEffect getEatSound() {
        return SoundEffect.POISON;
    }

    @Override
    public void update(int deltaMs) {
        if (deltaMs > 0) {
            remainingMs = Math.max(0, remainingMs - deltaMs);
        }
    }

    @Override
    public boolean isExpired() {
        return remainingMs <= 0;
    }

    public int getRemainingMs() {
        return remainingMs;
    }

    @Override
    public void draw(Graphics2D g, int cellSize) {
        if (remainingMs < BLINK_WHEN_LEFT_MS && (remainingMs / 150) % 2 == 0) {
            return;
        }
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        Point p = getPosition();
        int pad = cellSize / 6;
        int x = p.x * cellSize + pad;
        int y = p.y * cellSize + pad;
        int size = cellSize - 2 * pad;
        g.setColor(getColor());
        g.fillOval(x, y, size, size);
        g.setColor(Color.WHITE);
        g.setStroke(new BasicStroke(Math.max(1.5f, cellSize / 12f)));
        int m = size / 4;
        g.drawLine(x + m, y + m, x + size - m, y + size - m);
        g.drawLine(x + size - m, y + m, x + m, y + size - m);
        g.setStroke(new BasicStroke(1f));
    }
}
