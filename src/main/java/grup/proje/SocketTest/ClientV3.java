package grup.proje.SocketTest;

import java.net.*;
import java.io.*;

public class ClientV3 {
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

        // SERVER DİNLEYEN THREAD
        Thread listener = new Thread(() -> {
            try {
                String msg;
                while((msg = input.readLine()) != null){
                    System.out.println(msg);
                }
            } catch (Exception e){
                System.out.println("Server bağlantısı kesildi");
            }
        });
        listener.start();

        /*
            Thread şu constructor'a sahiptir:
            Thread(Runnable target)

            Runnable şu interface'tir:
            java.lang.Runnable
            içinde tek bir metod vardır:
            void run();

            Lambda:
            () -> {
                ...
            }
            aslında şu kodun kısa hali:
            new Runnable() {
                @Override
                public void run() {
                ...
                }
            }

            Yani bizim kod aslında şudur:

            Thread listener = new Thread(
                new Runnable() {
                    public void run() {
                        try {
                            String msg;
                            while((msg = input.readLine()) != null){
                                System.out.println(msg);
                            }
                        } catch (Exception e){
                            System.out.println("Server bağlantısı kesildi");
                        }
                    }
                }
            );
        */

        // KLAVYE THREADİ
        String message;
        while((message = keyboard.readLine()) != null){
            output.println(message);
        }
    }
}