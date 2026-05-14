package grup.proje.SocketTest;

import java.net.*;
import java.io.*;

public class ClientV2 {
    public static void main(String[] args) throws Exception {
        Socket socket = new Socket("localhost", 5000);
        BufferedReader input =
                new BufferedReader(
                        new InputStreamReader(socket.getInputStream()));
        PrintWriter output =
                new PrintWriter(socket.getOutputStream(), true);
        BufferedReader keyboard =
                new BufferedReader(
                        new InputStreamReader(System.in));

        String message;

        while(true){
            message = keyboard.readLine();
            output.println(message);
            String response = input.readLine();
            System.out.println("Server: " + response);
        }
    }
}