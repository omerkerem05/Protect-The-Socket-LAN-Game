package grup.proje.Lobby;

public class LobbyInfo {
    public String hostName;
    public String playerCount;
    public String ipAddress;
    public String status; // "Lobide", "Oyunda", "Dolu"

    public LobbyInfo(String hostName, String playerCount, String ipAddress, String status) {
        this.hostName = hostName;
        this.playerCount = playerCount;
        this.ipAddress = ipAddress;
        this.status = status;
    }

    // IP adresine göre lobi karşılaştırması yapmak için (Listede var mı yok mu kontrolü)
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof LobbyInfo) {
            LobbyInfo other = (LobbyInfo) obj;
            return this.ipAddress.equals(other.ipAddress);
        }
        return false;
    }
}