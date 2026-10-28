package vn.edu.ptit.oop.nhom05.model;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;

public class Obstacle extends GameObject {

    public Obstacle(Point position) {
        super(position, new Color(96, 96, 96));
    }

    @Override
    public void draw(Graphics2D g, int cellSize) {
        Point p = getPosition();
        int x = p.x * cellSize;
        int y = p.y * cellSize;
        g.setColor(getColor());
        g.fillRect(x, y, cellSize, cellSize);
        g.setColor(getColor().brighter());
        g.drawLine(x, y, x + cellSize - 1, y);
        g.drawLine(x, y, x, y + cellSize - 1);
        g.setColor(getColor().darker());
        g.drawLine(x, y + cellSize - 1, x + cellSize - 1, y + cellSize - 1);
        g.drawLine(x + cellSize - 1, y, x + cellSize - 1, y + cellSize - 1);
    }
}
