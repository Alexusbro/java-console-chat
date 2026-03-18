package ru.otus.java.basic.chat.client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class Client implements Runnable {
    private Socket socket;
    private DataOutputStream out;
    private DataInputStream in;
    private String host;
    private int port;
    private Scanner scan;
    private boolean active = true;

    public Client(String host, int port) throws IOException {
        this.host = host;
        this.port = port;
        scan = new Scanner(System.in);
        socket = new Socket(host, port);
        out = new DataOutputStream(socket.getOutputStream());
        in = new DataInputStream(socket.getInputStream());
    }

    private void readMessage() {
        new Thread(() -> {
            try {
                while (active) {
                    String message = in.readUTF();
                    if (message.startsWith("/")) {
                        if (message.equals("/exitOK")) {
                            break;
                        }
                        if (message.equals("/authok")) {
                            System.out.println("Удалось войти в чат с именем пользователя " + message.split(" ")[1]);
                        }
                        if (message.equals("/regok")) {
                            System.out.println("Удалось успешно зарегистироваться и войти в чат с именем пользователя " + message.split(" ")[1]);
                        }
                        continue;
                    }
                    System.out.println(message);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                disconnect();
            }
        }).start();
    }

    @Override
    public void run() {
        readMessage();
        try {
            while (active) {
                String message = scan.nextLine();
                out.writeUTF(message);
                if (message.equals("/exit")) {
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void disconnect() {
        active = false;
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

        if (scan != null) {
            scan.close();
        }

    }
}

