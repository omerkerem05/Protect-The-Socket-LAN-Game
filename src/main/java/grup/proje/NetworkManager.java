package grup.proje;

import grup.proje.Controller.GameController;
import grup.proje.Lobby.*;

import grup.proje.UI.LobbyUI;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.List;
import java.util.ArrayList;

public class NetworkManager {

    private LobbyBroadcaster broadcaster;
    private LobbyDiscovery discovery;
    private LobbyServer lobbyServer;
    private LobbyClient lobbyClient;

    private ObservableList<LobbyInfo> activeLobbies = FXCollections.observableArrayList();

    private String hostName;
    private int maxPlayers = 3;
    private int currentPlayers = 1;

    public LobbyUI lobbyUI;
    public GameController gameController;
    private GameStartListener listener;

    public GameServer getGameServer() {
        if (lobbyServer != null) return lobbyServer.gameServer;
        return null;
    }

    public LobbyServer getLobbyServer() {
        if (lobbyServer != null) return lobbyServer;
        return null;
    }

    public void setGameStartListener(GameStartListener listener) {
        this.listener = listener;
    }

    public void setLobbyUI(LobbyUI lobbyUI){
        this.lobbyUI = lobbyUI;
    }

    public void setGameController(GameController gameController){
        this.gameController = gameController;
    }

    // ------------------- BROADCAST -------------------
    public void startBroadcast(String hostName, int maxPlayers) {
        if (broadcaster != null) broadcaster.stop();
        this.hostName = hostName;
        this.maxPlayers = maxPlayers;
        broadcaster = new LobbyBroadcaster(hostName, maxPlayers);
        broadcaster.setCurrentPlayers(currentPlayers);
        broadcaster.start();
        System.out.println("start broadcast");
    }

    public void stopBroadcast() {
        if (broadcaster != null) broadcaster.stop();
        System.out.println("stop broadcast");
    }

    public void setCurrentPlayers(int count) {
        this.currentPlayers = count;
        if (broadcaster != null) broadcaster.setCurrentPlayers(count);
    }

    public void setLobbyStatus(String status) {
        if (broadcaster != null) broadcaster.setStatus(status);
    }

    // ------------------- DISCOVERY -------------------
    public void startDiscovery() {
        if (discovery != null) stopDiscovery();
        activeLobbies.clear();
        discovery = new LobbyDiscovery(activeLobbies);
        discovery.start();
        System.out.println("start discovery");
    }

    public void stopDiscovery() {
        if (discovery != null) discovery.stop();
        System.out.println("stop discovery");
    }

    public ObservableList<LobbyInfo> getActiveLobbies() {
        return activeLobbies;
    }

    // ------------------- TCP SERVER -------------------
    public void startServer() {
        lobbyServer = new LobbyServer(this, lobbyUI, broadcaster);
        lobbyServer.start();
    }

    public void stopServer() {
        if (lobbyServer != null) {
            lobbyServer.broadcast("SERVER_CLOSED");
            lobbyServer.stop();
        }
    }

    // ------------------- TCP CLIENT -------------------
    public void connectToServer(String ip) throws Exception{
        lobbyClient = new LobbyClient(lobbyUI, this);
        lobbyClient.connect(ip);
    }

    public void disconnectClient() {
        if (lobbyClient != null) lobbyClient.disconnect();
    }

    // ------------------- CHAT -------------------
    public void sendChatMessage(String nickname, String message) {
        String formattedMessage = "CHAT;" + nickname + ";" + message;
        if (lobbyClient != null) {
            lobbyClient.output.println(formattedMessage); // Server'a gönder
        }
    }

    public void send(String message) {
        if (lobbyClient != null) {
            lobbyClient.output.println(message); // Server'a gönder
            System.out.println(message);
        }
    }

    // Gelen mesajları handle et
    public void receiveMessage(String msg) {
        if (msg.startsWith("PLAYERS;")) {
            Assets.joinLobbySound.play();
            String data = msg.substring(8); // PLAYERS;
            String[] names = data.split(",");

            List<String> playerList = new ArrayList<>();
            for (String name : names) {
                if (!name.isEmpty()) playerList.add(name);
            }

            javafx.application.Platform.runLater(() -> {
                // UI güncelle
                lobbyUI.setPlayers(playerList);
            });

        }
        else if (msg.startsWith("CHAT;")) {
            Assets.getMessageSound.play(0.2);
            String[] parts = msg.split(";", 3); // CHAT, Nickname, Mesaj
            if (parts.length == 3) {
                String sender = parts[1];
                String content = parts[2];
                System.out.println(sender+" "+content);

                // UI güncelleneceği için Platform.runLater şart
                Platform.runLater(() -> {
                    lobbyUI.appendChatMessage(sender, content);
                });
            }
        }

        else if(msg.equals("START_GAME")) {
            if(listener != null) {
                listener.onGameStart();
            }
        }

        else if(msg.equals("SERVER_CLOSED") && !Main.isHost) {
            System.out.println("Host oyunu kapattı");

            lobbyUI.goBackToLobbyBrowser();
            lobbyClient.disconnect();
        }

        else if (msg.startsWith("SPAWN:")) {
            String[] parts = msg.split(":");

            int x = Integer.parseInt(parts[1]);
            int y = Integer.parseInt(parts[2]);
            String name = parts[3];

            Platform.runLater(() -> {
                gameController.spawnPlayer(x, y, name);
            });
        }

        else if (msg.startsWith("UPDATE:")) {
            String[] parts = msg.split(":");

            String name = parts[1];
            double x = Double.parseDouble(parts[2]);
            double y = Double.parseDouble(parts[3]);

            gameController.updateRemotePlayer(name, x, y);
        }

        else if (msg.startsWith("ENEMY_SPAWN:")) {
            String[] p = msg.split(":");

            String id = p[1];
            int x = Integer.parseInt(p[2]);
            int y = Integer.parseInt(p[3]);
            int health = Integer.parseInt(p[4]);

            Platform.runLater(() -> gameController.spawnEnemy(id, x, y, health));
        }

        else if (msg.startsWith("ENEMY_UPDATE:")) {
            String[] p = msg.split(":");

            String id = p[1];
            double x = Double.parseDouble(p[2]);
            double y = Double.parseDouble(p[3]);

            Platform.runLater(() -> {
                gameController.updateEnemy(id, x, y);
            });
        }

        if (msg.startsWith("PLAYER_DEAD:")) {
            String name = msg.split(":")[1];
            Platform.runLater(() -> gameController.handlePlayerDead(name));
        }

        if (msg.equals("GAME_OVER")) {
            Platform.runLater(() -> gameController.handleGameOver());
        }

        else if (msg.startsWith("BULLET_SPAWN:")) {
            String[] p = msg.split(":");
            String id = p[1];       // "B_Ali_0" gibi
            double x  = Double.parseDouble(p[2]);
            double y  = Double.parseDouble(p[3]);
            double fx = Double.parseDouble(p[4]);
            double fy = Double.parseDouble(p[5]);
            Platform.runLater(() -> gameController.spawnBullet(id, x, y, fx, fy));
        }
        else if (msg.startsWith("BULLET_UPDATE:")) {
            String[] p = msg.split(":");
            String id = p[1];
            double x  = Double.parseDouble(p[2]);
            double y  = Double.parseDouble(p[3]);
            Platform.runLater(() -> gameController.updateBullet(id, x, y));
        }
        else if (msg.startsWith("BULLET_REMOVE:")) {
            String id = msg.split(":")[1];
            Platform.runLater(() -> gameController.removeBullet(id));
        }
        else if (msg.startsWith("ENEMY_HIT:")) {
            String id = msg.split(":")[1];
            // health bilgisini kullanmak istersen: int health = Integer.parseInt(p[2]);
            Platform.runLater(() -> gameController.handleEnemyHit(id));
        }
        else if (msg.startsWith("ENEMY_DEAD:")) {
            String[] p = msg.split(":");
            String id = p[1];
            Platform.runLater(() -> gameController.handleEnemyDead(id));
        }
    }

    // ------------------- PLAYER LIST -------------------
    private List<String> players = new ArrayList<>();

    public void addPlayer(String name) {
        if (!players.contains(name)) players.add(name);
    }

    public void removePlayer(String name) {
        players.remove(name);
    }

    public void startGame(){
        lobbyServer.startGame();
    }
}