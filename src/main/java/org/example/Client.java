package org.example;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client {
    private static final String SERVER = "localhost";
    private static final int PORT = 9987;

    public static void main(String[] args) {
        try (Socket socket = new Socket(SERVER, PORT)) {
            System.out.println("Подключились к серверу " + SERVER + ":" + PORT);
            new Thread(new MessageReseiver(socket)).start();

            PrintWriter out = new PrintWriter(socket.getOutputStream(),true);
            BufferedReader consoleInput = new BufferedReader(new InputStreamReader(System.in));
            String userInput;
            while ((userInput = consoleInput.readLine()) != null) {
                out.println(userInput);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static class MessageReseiver implements Runnable {
        private Socket socket;
        public MessageReseiver(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
                String message;
                while((message = in.readLine()) != null) {
                    System.out.println(message);
                }
            }catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


}
