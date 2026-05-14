package grup.proje.Lobby;

import javafx.application.Platform;
import javafx.collections.ObservableList;

import java.net.*;
import java.nio.charset.StandardCharsets;

public class LobbyDiscovery {
    private DatagramSocket socket;
    private boolean running;
    private Thread thread;
    private final int PORT = 4445;

    private ObservableList<LobbyInfo> lobbyList;

    public LobbyDiscovery(ObservableList<LobbyInfo> lobbyList) {
        this.lobbyList = lobbyList;
    }

    public void start() {
        running = true;

        thread = new Thread(() -> {
            try {
                // Portu dinlemeye başla
                socket = new DatagramSocket(PORT, InetAddress.getByName("0.0.0.0"));
                socket.setBroadcast(true);

                byte[] buffer = new byte[1024];

                while (running) {
                    DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                    socket.receive(packet); // Paket gelene kadar bekler

                    String msg = new String(packet.getData(), 0, packet.getLength(), StandardCharsets.UTF_8);
                    String ip = packet.getAddress().getHostAddress();

                    String[] parts = msg.split(";");
                    if (parts.length == 4) {
                        String hostName = parts[0];
                        String playerCount = parts[1]; // parts[2] max oyuncu sayısını veriyor gereksiz
                        String status = parts[3];

                        LobbyInfo lobby = new LobbyInfo(hostName, playerCount, ip, status);

                        // LİSTEYE EKLEME KISMI
                        Platform.runLater(() -> {
                            // Eğer bu lobi listede zaten varsa ekleme (IP kontrolü)
                            if (!lobbyList.contains(lobby)) {
                                lobbyList.add(lobby);
                            } else {
                                // Varsa bilgilerini güncelle (oyuncu sayısı değişmiş olabilir)
                                int index = lobbyList.indexOf(lobby);
                                lobbyList.set(index, lobby);
                            }
                        });
                    }
                }
            } catch (Exception e) {
                if (running) e.printStackTrace();
            }
        });
        thread.start();
    }

    public void stop() {
        running = false;
        if (socket != null && !socket.isClosed()) {
            socket.close();
        }
    }
}