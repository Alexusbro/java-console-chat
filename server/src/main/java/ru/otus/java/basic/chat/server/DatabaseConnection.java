package ru.otus.java.basic.chat.server;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseConnection {
    private Connection connection;
    private Statement stmt;

    public DatabaseConnection() {
        connect("jdbc:postgresql://localhost:5432/chat_db?user=postgres&password=1");
        createTable();
    }

    private void connect(String url) {
        try {
            connection = DriverManager.getConnection(url);
            stmt = connection.createStatement();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void disconnect() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void createTable() {
        try {
            stmt.executeUpdate("create table if not exists users (" +
                    "id serial," +
                    "login varchar(50) not null," +
                    "password varchar(50) not null," +
                    "username varchar(100) not null," +
                    "userRole varchar(50) not null," +
                    "constraint pk_user primary key(id)" +
                    ")");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
