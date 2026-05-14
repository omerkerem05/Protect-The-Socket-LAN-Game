package grup.proje.SocketTest;

import java.net.*;

public class ServerV2 {

    public static void main(String[] args) throws Exception {

        ServerSocket server = new ServerSocket(5000);
        System.out.println("Server başladı");

        while(true){
            Socket socket = server.accept();
            System.out.println("Bir client bağlandı");
            ClientHandler handler = new ClientHandler(socket);
            handler.start();
        }
    }
}