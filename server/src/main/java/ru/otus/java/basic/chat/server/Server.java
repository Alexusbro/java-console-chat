package ru.otus.java.basic.chat.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class Server {
    private final int port;
    private List<ClientHandler> clients;
    private AuthenticatedProvider authenticatedProvider;

    public Server(int port) {
        this.port = port;
        this.clients = new CopyOnWriteArrayList<>();
        this.authenticatedProvider = new InMemoryAuthenticatedProvider(this);
    }

    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server started, port: " + port);
            while (true) {
                Socket socket = serverSocket.accept();
                ClientHandler clientHandler = new ClientHandler(this, socket);
                new Thread(clientHandler).start();

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void subscribe(ClientHandler clientHandler) {
        clients.add(clientHandler);
        System.out.println(clientHandler.getUsername() + " connected");
        broadcastMessage("Admin","Подключился пользователь " + clientHandler.getUsername());

    }

    public void unsubscribe(ClientHandler clientHandler) {
        clients.remove(clientHandler);
        System.out.println(clientHandler.getUsername() + " disconnected");
        broadcastMessage("Admin","Чат покинул пользователь " + clientHandler.getUsername());
    }


    public void broadcastMessage(String sender, String message) {
        for (ClientHandler client : clients) {
            client.sendMsg(ConsoleColors.WHITE_BOLD + sender + ": " + ConsoleColors.BLUE+ message + ConsoleColors.RESET);
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
            recipient.sendMsg(ConsoleColors.WHITE_BOLD + "Личное сообщение от " + clientHandler.getUsername() + ": " + ConsoleColors.BLUE + message + ConsoleColors.RESET);
        }
    }

    public boolean isUsernameBusy(String username) {
        for (ClientHandler client : clients) {
            if (client.getUsername().equals(username)) {
                return true;
            }
        }
        return false;
    }

    public AuthenticatedProvider getAuthenticatedProvider() {
        return authenticatedProvider;
    }
}
