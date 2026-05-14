package grup.proje.Lobby;

import java.net.*;
import java.nio.charset.StandardCharsets;

public class LobbyBroadcaster {

    private DatagramSocket socket;
    private boolean running;
    private Thread thread;
    private final int PORT = 4445;

    private String hostName;
    private int maxPlayers;

    private int currentPlayers = 1; // Host başlattığında 1 kişi var
    public String status = "Lobide";

    public LobbyBroadcaster(String hostName, int maxPlayers) {
        this.hostName = hostName;
        this.maxPlayers = maxPlayers;
    }

    public void start() {
        running = true;

        thread = new Thread(() -> {
            try {
                socket = new DatagramSocket();
                socket.setBroadcast(true);

                while (running) {

                    // Broadcast mesajı: hostName;current/maxPlayers;status
                    String message = hostName + ";" + currentPlayers + ";" + maxPlayers + ";" + status;

                    byte[] buffer = message.getBytes(StandardCharsets.UTF_8);

                    DatagramPacket packet = new DatagramPacket(
                            buffer,
                            buffer.length,
                            InetAddress.getByName("255.255.255.255"),
                            PORT
                    );

                    socket.send(packet);

                    Thread.sleep(1000); // 1 saniyede bir yayın
                }

            } catch (Exception e) {
                e.printStackTrace();
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

    public void setCurrentPlayers(int currentPlayers) {
        this.currentPlayers = currentPlayers;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}