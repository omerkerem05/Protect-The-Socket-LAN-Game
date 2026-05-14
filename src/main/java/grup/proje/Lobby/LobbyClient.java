package grup.proje.Lobby;

import grup.proje.Main;
import grup.proje.NetworkManager;
import grup.proje.UI.LobbyUI;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class LobbyClient {
    Socket socket;
    BufferedReader input;
    public PrintWriter output;
    LobbyUI lobbyUI;
    NetworkManager networkManager;
    String nickname;

    public LobbyClient(LobbyUI lobbyUI, NetworkManager networkManager) {
        this.lobbyUI = lobbyUI;
        this.networkManager = networkManager;
    }

    public void connect(String ip) throws Exception{
        nickname = Main.nicknameAtTextBox;
        socket = new Socket(ip, 5000);
        input =
                new BufferedReader(
                        new InputStreamReader(socket.getInputStream()));
        output =
                new PrintWriter(socket.getOutputStream(), true);

        output.println("NAME;" + nickname);

        // SERVER DİNLEYEN THREAD
        Thread listener = new Thread(() -> {
            try {
                String msg;
                while ((msg = input.readLine()) != null) {
                    networkManager.receiveMessage(msg);
                }
            } catch (Exception e) {
                System.out.println("Server bağlantısı kesildi (lobby client)");
            }
        });
        listener.start();
    }

    public void disconnect(){
        try {
            if (output != null) {
                output.println("DISCONNECT");
                output.close();
            }

            if (input != null) input.close();

            if (socket != null && !socket.isClosed()) {
                socket.close(); // 🔥 en önemli satır
            }

            System.out.println("Server bağlantısı kapatıldı (lobby client)");

        } catch (IOException e) {
            System.out.println("Disconnect hatası: " + e.getMessage());
        }
    }
}