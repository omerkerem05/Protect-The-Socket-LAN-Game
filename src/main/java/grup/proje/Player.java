package grup.proje;

import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;

import java.util.Set;

public class Player extends GameObject {
    private double speed = 1.5;
    private String nickname;
    private boolean alive = true;

    public boolean isAlive() {
        return alive;
    }

    public void setAlive(boolean alive) {
        this.alive = alive;
    }

    public Player(Image img, double startX, double startY, String nickname) {
        super(img, startX, startY);
        this.nickname = nickname;
    }

    public String getNickname() {
        return nickname;
    }

    public void update(Set<KeyCode> activeKeys) {
        double vx = 0;
        double vy = 0;

        if (activeKeys.contains(KeyCode.W)) vy -= speed;
        if (activeKeys.contains(KeyCode.S)) vy += speed;
        if (activeKeys.contains(KeyCode.A)) vx -= speed;
        if (activeKeys.contains(KeyCode.D)) vx += speed;

        // Normalizasyon
        double length = Math.sqrt(vx * vx + vy * vy);
        if (length != 0) {
            vx = (vx / length) * speed;
            vy = (vy / length) * speed;
        }

        // Çarpışma kontrolü yaparak hareket etme
        if (canMoveTo(this.x + vx, this.y)) this.x += vx;
        if (canMoveTo(this.x, this.y + vy)) this.y += vy;

        moveSprite();
    }

    public void setPosition(double x, double y) {
        this.x = x;
        this.y = y;
        moveSprite();
    }

    private boolean canMoveTo(double nextX, double nextY) {
        int border = TILE_SIZE;
        if (nextX < border || nextX + TILE_SIZE > 700 - border) return false;
        if (nextY < border || nextY + TILE_SIZE > 700 - border) return false;
        return true;
    }
}