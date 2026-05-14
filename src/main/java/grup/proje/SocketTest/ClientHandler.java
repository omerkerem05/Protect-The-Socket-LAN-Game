package grup.proje.SocketTest;

import java.net.*;
import java.io.*;

public class ClientHandler extends Thread {

    Socket socket;
    PrintWriter output;

    public ClientHandler(Socket socket){
        this.socket = socket;
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
                System.out.println("Client dedi ki: " + message);
                ServerV3.broadcast(socket.getPort() + ": " + message);
            }

        }catch(Exception e){
            System.out.println("Client bağlantıyı kapattı!");
        }finally {
            ServerV3.clients.remove(this);
        }
    }

    public void sendMessage(String msg){
        output.println(msg);
    }
}