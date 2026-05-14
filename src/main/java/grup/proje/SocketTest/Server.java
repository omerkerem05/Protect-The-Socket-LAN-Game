package grup.proje.SocketTest;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

    public static void main(String[] args) throws Exception {

        ServerSocket server = new ServerSocket(5000);
        System.out.println("Server başlatıldı...");
        Socket socket = server.accept();
        System.out.println("Bir client bağlandı!");

        //InputStream stream = socket.getInputStream(); byte olarak gelen veri
        //InputStreamReader reader = new InputStreamReader(stream); byte to char dönüşümü
        //BufferedReader input = new BufferedReader(reader); readLine() ile okumayı kolaylaştıran sınıf

        BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        String message = input.readLine();
        System.out.println("Client dedi ki: " + message);
    }
}