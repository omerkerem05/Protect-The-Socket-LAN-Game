package grup.proje.SocketTest;

import java.io.PrintWriter;
import java.net.Socket;

public class Client {

    public static void main(String[] args) throws Exception {

        Socket socket = new Socket("localhost", 5000);

        System.out.println("Server'a bağlanıldı!");

        PrintWriter output =
                new PrintWriter(socket.getOutputStream(), true);

        output.println("Selam Server");
    }
}