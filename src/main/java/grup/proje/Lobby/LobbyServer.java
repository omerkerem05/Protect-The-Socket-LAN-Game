package grup.proje.Lobby;

import grup.proje.*;
import grup.proje.UI.LobbyUI;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class LobbyServer {
    List<ClientHandler> clients = new CopyOnWriteArrayList<>();
    ServerSocket server;
    NetworkManager networkManager;
    LobbyUI lobbyUI;
    private Thread thread;
    public GameServer gameServer;
    private LobbyBroadcaster broadcaster;

    public LobbyServer(NetworkManager networkManager, LobbyUI lobbyUI, LobbyBroadcaster broadcaster){
        this.networkManager = networkManager;
        this.lobbyUI = lobbyUI;
        this.gameServer = new GameServer();
        this.broadcaster = broadcaster;
    }

    public void start(){
        thread = new Thread(() -> {
            try {
                server = new ServerSocket(5000);
                System.out.println("Server başladı");

                while (true) {
                    Socket socket = server.accept();
                    System.out.println("Bir client bağlandı");
                    ClientHandler handler = new ClientHandler(socket, this, gameServer);
                    clients.add(handler);
                    updateLobbyStatus();
                    networkManager.setCurrentPlayers(clients.size());
                    handler.start();
                }
            } catch (IOException e) {
                System.err.println("Server hatası: " + e.getMessage());
            }

        });

        thread.start();
    }

    public void stop(){
        try {
            // ClientHandler'ları kapat
            for (ClientHandler c : clients) {
                try { c.socket.close(); } catch (Exception ignored) {}
            }
            clients.clear();

            if (gameServer != null) gameServer.stop();

            if (server != null && !server.isClosed()) {
                server.close();
            }

            System.out.println("Sunucu kapatıldı.");
        } catch (IOException e) {
            System.err.println("Kapatılırken hata: " + e.getMessage());
        }
    }

    public void broadcast(String msg){
        for(ClientHandler client : clients){
            client.sendMessage(msg);
        }
    }

    public void sendPlayerList() {
        StringBuilder sb = new StringBuilder("PLAYERS;");

        for (ClientHandler client : clients) {
            if (client.getPlayerName() != null) {
                sb.append(client.getPlayerName()).append(",");
            }
        }
        broadcast(sb.toString());
    }

    public void updateLobbyStatus() {

        if (clients.size() >= 3) {
            broadcaster.status = "Dolu";
        } else {
            broadcaster.status = "Lobide";
        }

        if (broadcaster != null) {
            broadcaster.setCurrentPlayers(clients.size());
            broadcaster.setStatus(broadcaster.status);
        }
    }

    public void startGame() {
        broadcaster.status = "Oyunda";
        broadcaster.setStatus("Oyunda");

        broadcast("START_GAME");

        gameServer.start(clients);
    }
}
