package grup.proje;

public class EnemyState {
    public String id;
    public double x, y;
    public double targetX, targetY;
    public boolean isMoving;
    public int health;

    public EnemyState(String id, double x, double y, int health) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.targetX = x;
        this.targetY = y;
        this.health = health;
        isMoving = false;
    }
}
