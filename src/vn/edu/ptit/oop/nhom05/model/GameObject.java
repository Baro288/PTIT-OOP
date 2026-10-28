package vn.edu.ptit.oop.nhom05.model;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;

public abstract class GameObject {

    private Point position;
    private Color color;

    protected GameObject(Point position, Color color) {
        setPosition(position);
        setColor(color);
    }

    public abstract void draw(Graphics2D g, int cellSize);

    public boolean occupies(Point cell) {
        return position.equals(cell);
    }

    public Point getPosition() {
        return new Point(position);
    }

    protected final void setPosition(Point position) {
        if (position == null) {
            throw new IllegalArgumentException("Vị trí không được null");
        }
        if (position.x < 0 || position.y < 0) {
            throw new IllegalArgumentException(
                "Toạ độ ô không được âm: (" + position.x + ", " + position.y + ")");
        }
        this.position = new Point(position);
    }

    public Color getColor() {
        return color;
    }

    public final void setColor(Color color) {
        if (color == null) {
            throw new IllegalArgumentException("Màu không được null");
        }
        this.color = color;
    }
}
