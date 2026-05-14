package grup.proje;

import javafx.scene.image.Image;

public class Bullet extends GameObject {

    public Bullet(Image img, double startX, double startY) {
        super(img, startX, startY);
        this.sprite.setFitWidth(15); // Mermi küçük olsun
        this.sprite.setFitHeight(15);
    }
}