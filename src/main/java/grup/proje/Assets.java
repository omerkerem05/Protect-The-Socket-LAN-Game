package grup.proje;

import javafx.scene.image.Image;
import javafx.scene.media.AudioClip;
import javafx.scene.media.MediaPlayer;
import java.util.ArrayList;
import java.util.List;

public class Assets {
    public static Image avatar1, avatar2, avatar3;
    public static Image lobbyBg, lobbyBgFull;

    public static Image bgImg, wallImg;
    public static Image playerImg;
    public static Image bulletImg;
    public static List<Image> enemySprites = new ArrayList<>();
    public static List<Image> remainsSprites = new ArrayList<>();

    public static AudioClip createLobbySound;
    public static AudioClip joinLobbySound;
    public static AudioClip sendMessageSound;
    public static AudioClip getMessageSound;

    public static AudioClip gameMusic;


    public static void load() {
        avatar1 = new Image(Assets.class.getResource("/images/avatar1.png").toExternalForm());
        avatar2 = new Image(Assets.class.getResource("/images/avatar2.png").toExternalForm());
        avatar3 = new Image(Assets.class.getResource("/images/avatar3.png").toExternalForm());

        lobbyBg = new Image(Assets.class.getResource("/images/lobbyBg.jpg").toExternalForm());
        lobbyBgFull = new Image(Assets.class.getResource("/images/lobbyBgFull.jpg").toExternalForm());

        bgImg = new Image(Assets.class.getResource("/images/gameBg.png").toExternalForm());
        wallImg = new Image(Assets.class.getResource("/images/cyberWall.png").toExternalForm());

        playerImg = new Image(Assets.class.getResource("/images/cyberShield.png").toExternalForm());
        bulletImg = new Image(Assets.class.getResource("/images/bullet.png").toExternalForm());

        enemySprites.add(new Image(Assets.class.getResource("/images/enemyRed.png").toExternalForm()));
        enemySprites.add(new Image(Assets.class.getResource("/images/enemyGreen.png").toExternalForm()));

        remainsSprites.add(new Image(Assets.class.getResource("/images/redDebris.png").toExternalForm()));
        remainsSprites.add(new Image(Assets.class.getResource("/images/greenDebris.png").toExternalForm()));

        createLobbySound = new AudioClip(Assets.class.getResource("/sounds/createLobbySound.wav").toString());
        joinLobbySound = new AudioClip(Assets.class.getResource("/sounds/joinLobbySound.wav").toString());
        createLobbySound.play(0);
        joinLobbySound.play(0);
        sendMessageSound = new AudioClip(Assets.class.getResource("/sounds/sendMessageSound.mp3").toString());
        getMessageSound = new AudioClip(Assets.class.getResource("/sounds/getMessageSound.mp3").toString());
        sendMessageSound.play(0);
        getMessageSound.play(0);

        gameMusic = new AudioClip(Assets.class.getResource("/sounds/gameMusic.mp3").toString());
        gameMusic.play(0.15);
    }
}
