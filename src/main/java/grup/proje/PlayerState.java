package grup.proje;

public class PlayerState {
    public String nickname;
    public double x, y;
    public boolean alive = true;

    public PlayerState(String nickname, double x, double y) {
        this.nickname = nickname;
        this.x = x;
        this.y = y;
    }
}