package ru.otus.java.basic.chat.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ClientHandler implements Runnable {
    private Server server;
    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;
    private String username;
    private boolean isAuthenticate;

    public ClientHandler(Server server, Socket socket) throws IOException {
        this.server = server;
        this.socket = socket;
        this.in = new DataInputStream(socket.getInputStream());
        this.out = new DataOutputStream(socket.getOutputStream());
        //this.username = "user_" + socket.getPort();
    }

    @Override
    public void run() {
        try {
            while (!isAuthenticate) {
                sendMsg(ConsoleColors.GREEN + "Для авторизации введите данные в формате /auth login parol\nДля регистрации введите данные в формате /reg login parol username" + ConsoleColors.RESET);
                String message = in.readUTF();
                // /служебные сообщения;
                if (message.startsWith("/")) {
                    if (message.equals("/exit")) {
                        sendMsg("/exitOK");
                        break;
                    }
                    if (message.startsWith("/auth")) {
                        String[] token = message.trim().split(" ");
                        if (token.length != 3) {
                            sendMsg(ConsoleColors.RED_BRIGHT + "Неверный формат команды авторизации" + ConsoleColors.RESET);
                            continue;
                        }
                        if (server.getAuthenticatedProvider().authenticate(this, token[1], token[2])) {
                            isAuthenticate = true;
                            break;
                        }
                    }
                    if (message.startsWith("/reg")) {
                        String[] token = message.trim().split(" ");
                        if (token.length != 4) {
                            sendMsg(ConsoleColors.RED_BRIGHT + "Неверный формат команды регистрации" + ConsoleColors.RESET);
                            continue;
                        }
                        if (server.getAuthenticatedProvider().register(this, token[1], token[2], token[3])) {
                            isAuthenticate = true;
                            break;
                        }
                    }
                }

            }

            while (isAuthenticate) {
                String message = in.readUTF();
                // /служебные сообщения;
                if (message.startsWith("/")) {
                    if (message.equals("/exit")) {
                        sendMsg(ConsoleColors.GREEN + "/exitOK" + ConsoleColors.RESET);
                        break;
                    } else if (message.startsWith("/w")) {
                        String[] tokenMsg = message.split(" ", 3);
                        String recipient = tokenMsg[1];
                        String privateMsg = tokenMsg[2];
                        server.privateMessage(this, recipient, privateMsg);
                    }
                } else {
                    System.out.println(username + ": " + message);
                    server.broadcastMessage(username, message);
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            disconnect();
        }
    }

    public void sendMsg(String message) {
        try {
            out.writeUTF(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    private void disconnect() {
        isAuthenticate = false;
        server.unsubscribe(this);
        try {
            if (in != null) {
                in.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            if (out != null) {
                out.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            if (socket != null) {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}