package ru.otus.java.basic.chat.server;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class InMemoryAuthenticatedProvider implements AuthenticatedProvider {
    private class User {
        private String login;
        private String password;
        private String username;

        public User(String login, String password, String username) {
            this.login = login;
            this.password = password;
            this.username = username;
        }
    }

    private Server server;
    private List<User> users;

    public InMemoryAuthenticatedProvider(Server server) {
        this.server = server;
        this.users = new CopyOnWriteArrayList<>();
        this.users.add(new User("user1", "user1", "Ivan"));
        this.users.add(new User("user2", "user2", "Anna"));
        this.users.add(new User("user3", "user3", "Anton"));
    }

    @Override
    public void initialize() {
        System.out.println("Сервер аутентификации запущен в режиме InMemory");
    }

    private String getUserNameByLoginAndPassword(String login, String password) {
        for (User user : users) {
            if (user.login.equals(login) && user.password.equals(password)) {
                return user.username;
            }
        }
        return null;
    }

    @Override
    public boolean authenticate(ClientHandler clientHandler, String login, String password) {
        String authUsername = getUserNameByLoginAndPassword(login, password);
        if (authUsername == null) {
            clientHandler.sendMsg("Неверный логин/пароль");
        }
        if (server.isUsernameBusy(authUsername)) {
            clientHandler.sendMsg("Учетная запись уже занята");
        }
        clientHandler.setUsername(authUsername);
        server.subscribe(clientHandler);
        clientHandler.sendMsg("Вы подключились под ником " + clientHandler.getUsername());
        clientHandler.setUsername("/authok " + authUsername);
        return true;
    }
}
