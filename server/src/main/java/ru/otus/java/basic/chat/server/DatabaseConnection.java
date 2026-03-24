package ru.otus.java.basic.chat.server;

import java.sql.*;

public class DatabaseConnection {

    private Connection connection;
    private Statement stmt;

    public DatabaseConnection(String url) {
        connect(url);
        createTable();
    }

    public Connection getConnection() {
        return connection;
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
                    "login varchar(50) unique not null," +
                    "password varchar(50) not null," +
                    "username varchar(100) unique not null," +
                    "userRole varchar(50) not null," +
                    "constraint pk_user primary key(id)" +
                    ")");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


}
