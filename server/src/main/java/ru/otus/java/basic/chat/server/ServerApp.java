package ru.otus.java.basic.chat.server;

public class ServerApp {
    public static void main(String[] args) {
        Server server = new Server(8189);
        server.start();
    }
}
