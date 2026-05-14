package grup.proje;

public class BulletState {
    public String id;
    public double x, y;
    public double fx, fy;

    public BulletState(String id, double x, double y, double fx, double fy) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.fx = fx;
        this.fy = fy;
    }
}