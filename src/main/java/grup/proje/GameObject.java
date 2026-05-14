package grup.proje;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public abstract class GameObject {
    public double x, y;
    public ImageView sprite;
    public static final int TILE_SIZE = 35;

    public GameObject(Image image, double x, double y) {
        this.sprite = new ImageView(image);
        this.x = x;
        this.y = y;

        this.sprite.setFitWidth(TILE_SIZE);
        this.sprite.setFitHeight(TILE_SIZE);

        moveSprite();
    }

    public void moveSprite() {
        sprite.setX(x);
        sprite.setY(y);
    }

    public ImageView getSprite() { return sprite; }
    public double getX() { return x; }
    public double getY() { return y; }
}