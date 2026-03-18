package ru.otus.java.basic.chat.client;

import java.io.IOException;

public class ClientApp {
    public static void main(String[] args) {
        try {
            Client client = new Client("localhost", 8190);
            client.run();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
