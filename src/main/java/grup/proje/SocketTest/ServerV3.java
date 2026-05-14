package grup.proje.SocketTest;

import java.net.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class ServerV3 {
    static List<ClientHandler> clients = new CopyOnWriteArrayList<>();

    public static void main(String[] args) throws Exception {
        ServerSocket server = new ServerSocket(5000);
        System.out.println("Server başladı");

        while(true){
            Socket socket = server.accept();
            System.out.println("Bir client bağlandı");
            ClientHandler handler = new ClientHandler(socket);
            clients.add(handler);
            handler.start();
        }
    }

    public static void broadcast(String msg){

        for(ClientHandler client : clients){
            client.sendMessage(msg);
        }

    }
}