package grup.proje.Lobby;

import grup.proje.GameServer;

import java.net.*;
import java.io.*;

public class ClientHandler extends Thread {

    Socket socket;
    PrintWriter output;
    public String playerName;
    public LobbyServer lobbyServer;
    public GameServer gameServer;

    public ClientHandler(Socket socket, LobbyServer lobbyServer, GameServer gameServer){
        this.socket = socket;
        this.lobbyServer = lobbyServer;
        this.gameServer = gameServer;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void run(){
        try{
            BufferedReader input =
                    new BufferedReader(
                            new InputStreamReader(socket.getInputStream()));
            output =
                    new PrintWriter(socket.getOutputStream(), true);
            String message;
            while((message = input.readLine()) != null){
                if(message.equals("DISCONNECT")) {
                    lobbyServer.clients.remove(this);
                    socket.close();

                    // 🔥 herkese yeni listeyi gönder
                    lobbyServer.sendPlayerList();
                }
                else if(message.startsWith("NAME;")) {
                    playerName = message.substring(5);

                    // isim gelince herkese listeyi tekrar gönder
                    lobbyServer.sendPlayerList();
                }
                else if (message.startsWith("CHAT;")){
                    lobbyServer.broadcast(message);
                }
                else if (message.startsWith("MOVE:")) {
                    String[] parts = message.split(":");

                    double x = Double.parseDouble(parts[1]);
                    double y = Double.parseDouble(parts[2]);
                    String name = parts[3];

                    gameServer.handleMove(name, x, y);
                }

                else if (message.startsWith("SHOOT:")) {
                    String[] parts = message.split(":");
                    double x  = Double.parseDouble(parts[1]);
                    double y  = Double.parseDouble(parts[2]);
                    double fx = Double.parseDouble(parts[3]);
                    double fy = Double.parseDouble(parts[4]);
                    String name = parts[5];

                    gameServer.handleShoot(x, y, fx, fy, name);
                }
            }

        }catch(Exception e){
            System.out.println("Client bağlantıyı kapattı! (clientHandler)");
        }finally {
            lobbyServer.clients.remove(this);
            lobbyServer.updateLobbyStatus();
        }
    }

    public void sendMessage(String msg){
        output.println(msg);
    }
}