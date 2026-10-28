package vn.edu.ptit.oop.nhom05.model;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;

public class Food extends GameObject {

    public static final int BASE_POINTS = 10;

    private final int points;

    public Food(Point position) {
        this(position, new Color(220, 50, 47), BASE_POINTS);
    }

    protected Food(Point position, Color color, int points) {
        super(position, color);
        if (points < 0) {
            throw new IllegalArgumentException("Điểm của mồi không được âm");
        }
        this.points = points;
    }

    public int getPoints() {
        return points;
    }

    public void applyTo(Snake snake) {
        snake.grow(1);
    }

    public SoundEffect getEatSound() {
        return SoundEffect.EAT;
    }

    public void update(int deltaMs) {
    }

    public boolean isExpired() {
        return false;
    }

    @Override
    public void draw(Graphics2D g, int cellSize) {
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        Point p = getPosition();
        int pad = cellSize / 6;
        g.setColor(getColor());
        g.fillOval(p.x * cellSize + pad, p.y * cellSize + pad, cellSize - 2 * pad, cellSize - 2 * pad);
        g.setColor(new Color(90, 60, 20));
        g.fillRect(p.x * cellSize + cellSize / 2 - 1, p.y * cellSize + pad / 2, 2, Math.max(2, cellSize / 5));
    }
}
