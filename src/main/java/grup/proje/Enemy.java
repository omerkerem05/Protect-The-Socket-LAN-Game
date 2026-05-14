package grup.proje;

import javafx.scene.image.Image;

import java.util.*;

public class Enemy extends GameObject {
    private int health;
    private Image remainsImg; // Bu düşman ölünce çıkacak görsel
    private boolean isFlashing = false;

    public Enemy(Image img, Image deadImg, double x, double y, int health) {
        super(img, x, y);
        this.remainsImg = deadImg;
        this.health = health;
    }

    public void takeDamage() {
        if (isFlashing) return; // Zaten beyazlamışsa hasar alma (opsiyonel)

        health--;
        if (health > 0) {
            flashWhite();
        }
    }

    private void flashWhite() {
        isFlashing = true;

        // JavaFX ColorAdjust kullanarak sprite'ı beyaz yapma
        javafx.scene.effect.ColorAdjust whiteEffect = new javafx.scene.effect.ColorAdjust();
        whiteEffect.setBrightness(1.0);
        sprite.setEffect(whiteEffect);

        javafx.animation.PauseTransition pause = new javafx.animation.PauseTransition(javafx.util.Duration.millis(200));
        pause.setOnFinished(e -> {
            sprite.setEffect(null); // Efekti kaldır
            isFlashing = false;
        });
        pause.play();
    }

    public Image getRemainsImg() {
        return remainsImg;
    }
}