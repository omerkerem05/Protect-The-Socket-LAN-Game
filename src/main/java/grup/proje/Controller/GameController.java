package grup.proje.Controller;

import grup.proje.*;
import grup.proje.UI.GameUI;
import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;

import java.util.*;

public class GameController {
    private ScreenManager screenManager;
    private NetworkManager networkManager;
    private GameUI gameUI;

    private Set<KeyCode> activeKeys = new HashSet<>(); // Basılı tutulan tuşları saklamak için

    private boolean isGameStarted = false;
    private AnimationTimer gameLoop;

    private Player localPlayer;
    private Map<String, Player> remotePlayers = new HashMap<>();

    private Map<String, Bullet> bullets = new LinkedHashMap<>();
    private long lastShotTime = 0;
    private final long shotCooldown = 500_000_000;

    private Map<String, Enemy> enemies = new HashMap<>();

    public static int secondsPassed = -1;
    private long lastTimerUpdate = 0; // Zamanlayıcıyı her saniye güncellemek için

    public GameController(ScreenManager screenManager, NetworkManager networkManager) {
        this.screenManager = screenManager;
        this.networkManager = networkManager;
    }

    public void setUI(GameUI gameUI){
        this.gameUI = gameUI;
    }

    public void spawnPlayer(int x, int y, String name) {
        Player p = new Player(Assets.playerImg, x, y, name);

        if (name.equals(Main.nicknameAtTextBox)) {
            localPlayer = p;
        } else {
            remotePlayers.put(name, p);
        }

    }

    public void initGame() {
        enemies.clear();
        bullets.clear();

        // reset değerler
        secondsPassed = -1;
        lastShotTime = 0;

        isGameStarted = true;

        startGameLoop();
    }

    public void setupControls(Scene scene) {
        scene.setOnKeyPressed(e -> activeKeys.add(e.getCode()));
        scene.setOnKeyReleased(e -> activeKeys.remove(e.getCode()));
    }

    public void startGameLoop() {
        gameLoop = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (!isGameStarted) return;

                // 🔥 TIMER ARTIRMA
                if (now - lastTimerUpdate >= 1_000_000_000L) {
                    secondsPassed++;
                    lastTimerUpdate = now;
                }

                // 1. Mantık
                if (localPlayer.isAlive()) {
                    localPlayer.update(activeKeys);
                    sendMovement();
                }
                handleShooting(now);

                // 2. UI’ya sadece “render et” de
                gameUI.update(remotePlayers,localPlayer, enemies, bullets);
                gameUI.updateTimer(secondsPassed);
            }
        };
        gameLoop.start();
    }

    public void Exit(){
        if (Main.isHost) {
            networkManager.getLobbyServer().stop();
            networkManager.stopBroadcast();
        }
        networkManager.disconnectClient();
        Platform.runLater(() -> {
            // Mevcut stage'i kapat, yeni instance başlat
            Stage stage = (Stage) screenManager.root.getScene().getWindow();
            stage.close();

            try {
                new Main().start(new Stage());
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public void updateEnemy(String id, double x, double y) {
        Enemy e = enemies.get(id);

        if (e != null) {
            e.x = x;
            e.y = y;
            e.moveSprite();
        }
    }

    public void spawnEnemy(String id, int x, int y, int health) {
        Image img;
        Image remains;

        if (health == 2) {
            img = Assets.enemySprites.get(0);
            remains = Assets.remainsSprites.get(0);
        } else {
            img = Assets.enemySprites.get(1);
            remains = Assets.remainsSprites.get(1);
        }

        Enemy e = new Enemy(img, remains, x, y, health);
        enemies.put(id, e);
    }

    public void handlePlayerDead(String name) {

        if (name.equals(Main.nicknameAtTextBox)) {
            localPlayer.setAlive(false);
            localPlayer.getSprite().setVisible(false);
        } else {
            Player p = remotePlayers.get(name);
            if (p != null) {
                p.setAlive(false);
                p.getSprite().setVisible(false);
            }
        }
    }

    public void handleGameOver() {
        stopGame();
        if (networkManager.getGameServer() != null){
            GameServer gs = networkManager.getGameServer();
            gs.stop();
        }
        gameUI.showGameOver(secondsPassed);
    }

    public void stopGame() {
        isGameStarted = false;

        if (gameLoop != null) {
            gameLoop.stop();
        }
    }

    private void sendMovement() {
        double x = localPlayer.getX();
        double y = localPlayer.getY();

        networkManager.send("MOVE:" + x + ":" + y + ":" + Main.nicknameAtTextBox);
        System.out.println("gamecontroller sendmovement");
    }

    public void updateRemotePlayer(String name, double x, double y) {
        Player p = remotePlayers.get(name);

        if (p != null) {
            p.setPosition(x, y);
        }
    }

    private void handleShooting(long now) {
        double fx = 0, fy = 0;
        if (activeKeys.contains(KeyCode.UP)) fy -= 1;
        if (activeKeys.contains(KeyCode.DOWN)) fy += 1;
        if (activeKeys.contains(KeyCode.LEFT)) fx -= 1;
        if (activeKeys.contains(KeyCode.RIGHT)) fx += 1;

        if ((fx != 0 || fy != 0) && (now - lastShotTime >= shotCooldown)) {
            lastShotTime = now;
            networkManager.send("SHOOT:"
                    + (localPlayer.getX()+ localPlayer.sprite.getFitWidth()/3) + ":"
                    + (localPlayer.getY()+ localPlayer.sprite.getFitHeight()/3) + ":"
                    + fx + ":" + fy + ":"
                    + Main.nicknameAtTextBox);
        }
    }

    public void spawnBullet(String id, double x, double y, double fx, double fy) {
        Bullet b = new Bullet(Assets.bulletImg, x, y);
        bullets.put(id, b);
    }

    public void updateBullet(String id, double x, double y) {
        Bullet b = bullets.get(id);
        if (b != null) {
            b.x = x;
            b.y = y;
            b.moveSprite();
        }
    }

    public void removeBullet(String id) {
        bullets.remove(id); // ← direkt id ile sil
    }

    public void handleEnemyHit(String id) {
        Enemy e = enemies.get(id);
        if (e != null) e.takeDamage(); // flashWhite burada çalışır
    }

    public void handleEnemyDead(String id) {
        Enemy e = enemies.remove(id);
        if (e != null) {
            gameUI.CreateRemains(e.getX(), e.getY(), e.getRemainsImg());
        }
    }
}
