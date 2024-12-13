package org.example;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;
import java.util.Set;

public class Server {
    private static Set<ClientHandler> clientList = new HashSet<>();
    private static final int PORT = 9987;

    public static void main(String args[]) {
        System.out.println("Сервер запущен на порту " + PORT);
        try (ServerSocket serverSocket = new ServerSocket(PORT)){
            while (true) {
                new ClientHandler(serverSocket.accept()).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static class ClientHandler extends Thread {
        private Socket socket;
        private BufferedReader in;
        private PrintWriter out;
        private String clientName;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        /**
         * метод взаимодействия с клиентом
         */
        public void run() {
            try {
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(),true);

                out.println("Введите имя: ");
                clientName = in.readLine();

                if (clientName != null) {
                    out.println("Добро пожаловать, " + clientName);
                }
                //передача сообщения пользователям, что новый клиент подключился к чату
                synchronized (clientList) {
                    broadcast(clientName + " подключился к чату", null);
                    System.out.println(clientName + " подключился к чату");
                    clientList.add(this);
                };
                //сообщения в чате
                String message;
                while ((message = in.readLine()) != null) {
                    System.out.println(clientName + ": " + message);
                    broadcast(message, this);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }finally {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                //передача сообщения остальным пользователям, что клиент вышел из чата
                synchronized (clientList) {
                    clientList.remove(this);
                    broadcast(clientName + " вышел из чата", null);
                    System.out.println(clientName + " вышел из чата");
                }
            }
        }
        private void broadcast(String message, ClientHandler sender) {
            synchronized (clientList) {
                for (ClientHandler handler: clientList) {
                    //сообщения только другим пользователям
                    if (handler != sender) {
                        handler.out.println(clientName + ": " + message);
                    }
                }
            }
        }
    }

}
