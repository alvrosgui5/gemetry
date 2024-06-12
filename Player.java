import java.awt.Graphics;
import java.awt.Color;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;

public class Player {
    private float x, y;
    private int width, height;
    private float yVelocity;
    private boolean jumping;
    private final float GRAVITY = 0.6f;
    private final float JUMP_STRENGTH = -19f;
    private final float FAST_FALL_GRAVITY = 10f;
    String color;

    public Player(float x, float y, String color) {
        this.x = x;
        this.y = y;
        this.width = 50;
        this.height = 50;
        this.yVelocity = 0;
        this.jumping = false;
        this.color = color;
    }
    
    public float getX() {
        return x;
    }

    public void draw(Graphics g) {
        if (color.equalsIgnoreCase("green")) {
            g.setColor(Color.GREEN);	
        } else  {
            g.setColor(Color.BLUE);
        }
        g.fillRect((int)x, (int)y, width, height);
    }

    public void update(boolean isFastFalling) {
        if (jumping) {
            yVelocity += GRAVITY;
            y += yVelocity;
        } else {
            float gravity = isFastFalling ? FAST_FALL_GRAVITY : GRAVITY;
            yVelocity += gravity;
            y += yVelocity;
        }
        int yFloor = 930;
        if (y > yFloor - height) {
            y = yFloor - height;
            yVelocity = 0;
            jumping = false;
        }
    }

    public void jump() {
        if (!jumping) {
            jumping = true;
            yVelocity = JUMP_STRENGTH;
        }
    }

    public Rectangle getBounds() {
        return new Rectangle((int)x, (int)y, width, height);
    }
    
    public void handleKeyPress(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_DOWN) {
            down();
        }
    }
    
    public void down() {
        yVelocity += FAST_FALL_GRAVITY;
    }
}