package vn.edu.ptit.oop.nhom05.model;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;

public class BonusFood extends Food {

    public static final int MULTIPLIER = 3;
    public static final int LIFETIME_MS = 5000;
    private static final int BLINK_WHEN_LEFT_MS = 1500;

    private int remainingMs = LIFETIME_MS;

    public BonusFood(Point position) {
        super(position, new Color(255, 193, 7), BASE_POINTS * MULTIPLIER);
    }

    @Override
    public SoundEffect getEatSound() {
        return SoundEffect.BONUS;
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
        int x = p.x * cellSize;
        int y = p.y * cellSize;
        int pad = Math.max(1, cellSize / 10);

        int[] xs = { x + cellSize / 2, x + cellSize - pad, x + cellSize / 2, x + pad };
        int[] ys = { y + pad, y + cellSize / 2, y + cellSize - pad, y + cellSize / 2 };
        g.setColor(getColor());
        g.fillPolygon(xs, ys, 4);
        g.setColor(new Color(200, 130, 0));
        g.drawPolygon(xs, ys, 4);

        int barW = (int) ((cellSize - 2 * pad) * (remainingMs / (double) LIFETIME_MS));
        g.setColor(new Color(200, 130, 0));
        g.fillRect(x + pad, y + cellSize - Math.max(2, cellSize / 10), barW, Math.max(2, cellSize / 10));
    }
}
