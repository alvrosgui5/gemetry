import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;

public class Block {
    private int x, y;
    private int width, height;
    private int speed;
    private boolean scored;
    private final static int BLOCK_GAP = 500;
    private Color color;

    public Block(int x, int y, Color color, int speed) {
        this.x = x;
        this.y = y;
        this.width = 50;
        this.height = 50;
        this.speed = speed;
        this.scored = false;
        this.color = color;
    }

    public void draw(Graphics g) {
        g.setColor(color);
        g.fillRect(x, y, width, height);
    }

    public void update() {
        x -= speed;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getWidth() {
        return width;
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public boolean isScored() {
        return scored;
    }

    public void setScored(boolean scored) {
        this.scored = scored;
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }

    public static int getBLOCK_GAP() {
        return BLOCK_GAP;
    }
}