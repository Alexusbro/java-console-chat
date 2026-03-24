package ru.otus.java.basic.chat.server;

public class User {
    private String login;
    private String password;
    private String username;
    private String userRole;


    public User(String login, String password, String username, String userRole) {
        this.login = login;
        this.password = password;
        this.username = username;
        this.userRole = userRole;
    }

    public String getUsername() {
        return username;
    }

    public String getUserRole() {
        return userRole;
    }

    public String getLogin() {
        return login;
    }
}
