package ru.otus.java.basic.chat.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class Server {
    private final int port;
    private List<ClientHandler> clients;

    public Server(int port) {
        this.port = port;
        clients = new CopyOnWriteArrayList<>();
    }

    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server started, port: " + port);
            while (true) {
                Socket socket = serverSocket.accept();
                ClientHandler clientHandler = new ClientHandler(this, socket);
                subscribe(clientHandler);
                new Thread(clientHandler).start();

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void subscribe(ClientHandler clientHandler) {
        clients.add(clientHandler);
        System.out.println(clientHandler.getUsername() + " connected");
        clientHandler.sendMsg("Вы подключились под ником " + clientHandler.getUsername());
        broadcastMessage("Подключился пользователь " + clientHandler.getUsername());

    }

    public void unsubscribe(ClientHandler clientHandler) {
        clients.remove(clientHandler);
        System.out.println(clientHandler.getUsername() + " disconnected");
        broadcastMessage("Чат покинул пользователь " + clientHandler.getUsername());
    }


    public void broadcastMessage(String message) {
        for (ClientHandler client : clients) {
            client.sendMsg(message);
        }
    }

    public void privateMessage(ClientHandler clientHandler, String username, String message) {
        ClientHandler recipient = null;
        for (ClientHandler client : clients) {
            if (client.getUsername().equals(username)) {
                recipient = client;
            }
        }
        if (recipient != null) {
            recipient.sendMsg("Личное сообщение от " + clientHandler.getUsername() + ": " + message);
        }
    }
}
