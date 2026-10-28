package vn.edu.ptit.oop.nhom05.model;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Snake extends GameObject {

    public static final int DEFAULT_LENGTH = 3;
    public static final Color DEFAULT_BODY_COLOR = new Color(46, 160, 67);
    public static final Color DEFAULT_HEAD_COLOR = new Color(0, 100, 0);

    private final LinkedList<Point> body = new LinkedList<>();
    private Direction direction;
    private Direction nextDirection;
    private int pendingGrowth;
    private final Color headColor;

    public Snake(Point head, int length, Direction dir) {
        this(head, length, dir, DEFAULT_BODY_COLOR, DEFAULT_HEAD_COLOR);
    }

    public Snake(Point head, int length, Direction dir, Color bodyColor, Color headColor) {
        super(head, bodyColor);
        if (headColor == null) {
            throw new IllegalArgumentException("Màu đầu rắn không được null");
        }
        this.headColor = headColor;
        if (length < 1) {
            throw new IllegalArgumentException("Độ dài rắn phải >= 1");
        }
        if (dir == null) {
            throw new IllegalArgumentException("Hướng ban đầu không được null");
        }
        this.direction = dir;
        this.nextDirection = dir;
        for (int i = 0; i < length; i++) {
            body.add(new Point(head.x - dir.getDx() * i, head.y - dir.getDy() * i));
        }
    }

    public Snake(Point head, Direction dir) {
        this(head, DEFAULT_LENGTH, dir);
    }

    public void changeDirection(Direction d) {
        if (d != null && !d.isOpposite(direction)) {
            nextDirection = d;
        }
    }

    public void grow(int n) {
        if (n > 0) {
            pendingGrowth += n;
        }
    }

    public void shrink(int n) {
        for (int i = 0; i < n; i++) {
            if (pendingGrowth > 0) {
                pendingGrowth--;
            } else if (body.size() > 1) {
                body.removeLast();
            }
        }
    }

    public void move() {
        direction = nextDirection;
        Point head = body.getFirst();
        Point newHead = new Point(head.x + direction.getDx(), head.y + direction.getDy());
        body.addFirst(newHead);
        if (pendingGrowth > 0) {
            pendingGrowth--;
        } else {
            body.removeLast();
        }
    }

    public void wrapHead(int cols, int rows) {
        if (cols <= 0 || rows <= 0) {
            throw new IllegalArgumentException("Kích thước bàn phải > 0");
        }
        Point head = body.getFirst();
        head.x = Math.floorMod(head.x, cols);
        head.y = Math.floorMod(head.y, rows);
    }

    public boolean hasHitItself() {
        Point head = body.getFirst();
        for (int i = 1; i < body.size(); i++) {
            if (body.get(i).equals(head)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean occupies(Point cell) {
        return body.contains(cell);
    }

    @Override
    public Point getPosition() {
        return getHead();
    }

    public Point getHead() {
        return new Point(body.getFirst());
    }

    public Direction getDirection() {
        return direction;
    }

    public int getLength() {
        return body.size();
    }

    public List<Point> getBody() {
        List<Point> copy = new ArrayList<>(body.size());
        for (Point p : body) {
            copy.add(new Point(p));
        }
        return copy;
    }

    @Override
    public void draw(Graphics2D g, int cellSize) {
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        int pad = Math.max(1, cellSize / 12);
        int size = cellSize - 2 * pad;
        int arc = cellSize / 3;

        for (int i = body.size() - 1; i >= 0; i--) {
            Point p = body.get(i);
            g.setColor(i == 0 ? headColor : getColor());
            g.fillRoundRect(p.x * cellSize + pad, p.y * cellSize + pad, size, size, arc, arc);
        }
        drawEyes(g, cellSize);
    }

    private void drawEyes(Graphics2D g, int cellSize) {
        Point head = body.getFirst();
        int cx = head.x * cellSize + cellSize / 2;
        int cy = head.y * cellSize + cellSize / 2;
        int eye = Math.max(2, cellSize / 6);
        int off = cellSize / 4;
        int fx = direction.getDx() * off;
        int fy = direction.getDy() * off;
        int sx = direction.getDy() * off;
        int sy = direction.getDx() * off;

        g.setColor(Color.WHITE);
        g.fillOval(cx + fx + sx - eye / 2, cy + fy + sy - eye / 2, eye, eye);
        g.fillOval(cx + fx - sx - eye / 2, cy + fy - sy - eye / 2, eye, eye);
        g.setStroke(new BasicStroke(1f));
    }
}
