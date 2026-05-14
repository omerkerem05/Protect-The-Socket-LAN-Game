package grup.proje;

import grup.proje.Controller.GameController;
import grup.proje.Lobby.ClientHandler;

import java.util.*;

public class GameServer {

    private Map<String, PlayerState> players = new HashMap<>();
    List<ClientHandler> clients;

    Map<String, EnemyState> enemies = new HashMap<>();
    int enemyIdCounter = 0;
    long lastEnemySpawn = 0;

    Map<String, BulletState> bullets = new HashMap<>();
    int bulletIdCounter = 0;

    private volatile boolean running = false;
    private Thread gameThread;

    public void broadcast(String msg) {
        for (ClientHandler c : clients) {
            c.sendMessage(msg);
        }
    }

    public void start(List<ClientHandler> clients) {
        this.clients = clients;

        Random r = new Random();

        for (ClientHandler c : clients) {
            int x = 282 + r.nextInt(100);
            int y = 282 + r.nextInt(100);

            players.put(c.getPlayerName(),
                    new PlayerState(c.getPlayerName(), x, y));

            broadcast("SPAWN:" + x + ":" + y + ":" + c.getPlayerName());
        }

        startLoop();
    }

    public void handleMove(String name, double x, double y) {
        PlayerState p = players.get(name);

        if (p != null) {
            p.x = x;
            p.y = y;

            broadcast("UPDATE:" + name + ":" + x + ":" + y);
        }
    }

    private void startLoop() {
        running = true;

        gameThread = new Thread(() -> {
            while (running) {
                try {
                    Thread.sleep(50);
                    spawnEnemyIfNeeded();
                    updateEnemies();
                    updateBullets();
                    checkCollisions();
                    broadcastEnemyState();
                    broadcastBulletState();

                } catch (InterruptedException e) {
                    System.out.println("Game loop durduruldu."); // ← sessizce çık
                    break; // ← döngüden çık
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            System.out.println("Game loop durdu.");
        });

        gameThread.start();
    }

    public void stop() {
        running = false;

        if (gameThread != null) {
            gameThread.interrupt(); // uyuyorsa uyandır
        }

        System.out.println("GameServer kapatıldı.");
    }

    private void spawnEnemyIfNeeded() {
        long now = System.currentTimeMillis();
        if (now - lastEnemySpawn >= 2000) {
            lastEnemySpawn = now;
            for (int i = 0; i<= GameController.secondsPassed/10; i++){
                spawnEnemy(); lastEnemySpawn = now;
            }
        }
    }

    private void spawnEnemy() {
        Random random = new Random();

        int kenar = random.nextInt(4);
        int x;
        int y;
        if (kenar == 0) {
            x = random.nextInt(20)*35;
            y = -35;
        } else if (kenar == 1) {
            x = random.nextInt(20)*35;
            y = 700;
        } else if (kenar == 2) {
            x = -35;
            y = random.nextInt(20)*35;
        } else {
            x = 700;
            y = random.nextInt(20)*35;
        }

        int health = random.nextBoolean() ? 2 : 1;

        String id = "E" + enemyIdCounter++;

        EnemyState e = new EnemyState(id, x, y, health);
        enemies.put(id, e);

        broadcast("ENEMY_SPAWN:" + id + ":" + x + ":" + y + ":" + health);
    }

    private void updateEnemies() {
        for (EnemyState e : enemies.values()) {

            if (!e.isMoving) {
                PlayerState closest = getClosestPlayer(e);
                if (closest == null) continue;

                calculateNextStep(e, closest);
            } else {
                moveToTarget(e);
            }
        }
    }

    private void calculateNextStep(EnemyState e, PlayerState player) {
        double nextX = e.x;
        double nextY = e.y;

        double diffX = player.x - e.x;
        double diffY = player.y - e.y;

        if (Math.abs(diffX) > Math.abs(diffY)) {
            nextX += (diffX > 0) ? 35 : -35;
        } else {
            nextY += (diffY > 0) ? 35 : -35;
        }

        if (!isTileOccupied(nextX, nextY, e)) {
            e.targetX = nextX;
            e.targetY = nextY;
            e.isMoving = true;
        }
    }

    private void moveToTarget(EnemyState e) {
        double speed = 2.5;

        if (e.x < e.targetX) e.x += speed;
        else if (e.x > e.targetX) e.x -= speed;

        if (e.y < e.targetY) e.y += speed;
        else if (e.y > e.targetY) e.y -= speed;

        if (Math.abs(e.x - e.targetX) < speed &&
                Math.abs(e.y - e.targetY) < speed) {

            e.x = e.targetX;
            e.y = e.targetY;
            e.isMoving = false;
        }
    }

    private boolean isTileOccupied(double tx, double ty, EnemyState current) {
        for (EnemyState other : enemies.values()) {

            if (other == current) continue;

            if ((other.x == tx && other.y == ty) ||
                    (other.targetX == tx && other.targetY == ty)) {

                return true;
            }
        }

        return false;
    }

    private PlayerState getClosestPlayer(EnemyState e) {

        PlayerState closest = null;
        double minDist = Double.MAX_VALUE;

        for (PlayerState p : players.values()) {
            if (!p.alive) continue; // 💀 ölüleri ignore et
            double dx = p.x - e.x;
            double dy = p.y - e.y;
            double dist = dx*dx + dy*dy;

            if (dist < minDist) {
                minDist = dist;
                closest = p;
            }
        }

        return closest;
    }

    private void broadcastEnemyState() {
        for (EnemyState e : enemies.values()) {
            broadcast("ENEMY_UPDATE:" + e.id + ":" + (int)e.x + ":" + (int)e.y);
        }
    }

    private void checkCollisions() {
        for (EnemyState e : enemies.values()) {

            for (PlayerState p : players.values()) {

                if (!p.alive) continue;

                if (intersects(e, p)) {

                    // player öldü
                    p.alive = false;

                    broadcast("PLAYER_DEAD:" + p.nickname);

                    break;
                }
            }
        }

        // Herkes öldü mü
        boolean allDead = true;

        for (PlayerState p : players.values()) {
            if (p.alive) {
                allDead = false;
                break;
            }
        }

        if (allDead) {
            broadcast("GAME_OVER");
        }
    }

    private boolean intersects(EnemyState e, PlayerState p) {
        return Math.abs(e.x - p.x) < 30 && Math.abs(e.y - p.y) < 30;
    }

    public void handleShoot(double x, double y, double fx, double fy, String shooterName) {
        String id = "B_" + shooterName + "_" + bulletIdCounter++;

        // Normalizasyon — çapraz giderken hız aynı kalsın
        double length = Math.sqrt(fx * fx + fy * fy);
        if (length != 0) {
            fx = fx / length;
            fy = fy / length;
        }

        BulletState b = new BulletState(id, x, y, fx, fy);
        bullets.put(id, b);

        broadcast("BULLET_SPAWN:" + id + ":" + x + ":" + y + ":" + fx + ":" + fy);
    }

    private void updateBullets() {
        double speed = 12;
        Iterator<Map.Entry<String, BulletState>> it = bullets.entrySet().iterator();

        while (it.hasNext()) {
            BulletState b = it.next().getValue();
            b.x += b.fx * speed;
            b.y += b.fy * speed;

            if (b.x < -50 || b.x > 750 || b.y < -50 || b.y > 750) {
                it.remove();
                broadcast("BULLET_REMOVE:" + b.id);
                continue;
            }

            // Bullet → Enemy çarpışma
            Iterator<Map.Entry<String, EnemyState>> eit = enemies.entrySet().iterator();
            while (eit.hasNext()) {
                EnemyState e = eit.next().getValue();

                if (Math.abs(b.x - e.x) < 25 && Math.abs(b.y - e.y) < 25) {
                    it.remove();
                    broadcast("BULLET_REMOVE:" + b.id);

                    e.health--;
                    broadcast("ENEMY_HIT:" + e.id + ":" + e.health);

                    if (e.health <= 0) {
                        eit.remove();
                        broadcast("ENEMY_DEAD:" + e.id);
                    }
                    break;
                }
            }
        }
    }

    private void broadcastBulletState() {
        for (BulletState b : bullets.values()) {
            broadcast("BULLET_UPDATE:" + b.id + ":" + (int)b.x + ":" + (int)b.y);
        }
    }
}