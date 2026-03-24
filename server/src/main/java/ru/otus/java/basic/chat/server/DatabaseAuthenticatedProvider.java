
package ru.otus.java.basic.chat.server;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DatabaseAuthenticatedProvider implements AuthenticatedProvider {
    private Server server;
    private DatabaseConnection databaseConnection;
    private static final String DB = "chat_db";
    private static final int PORT_DB = 5432;
    private static final String USER_DB = "postgres";
    private static final String PASSWORD_DB = "1";
    private static final String URL_DATABASE = String.format("jdbc:postgresql://localhost:%s/%s?user=%s&password=%s", PORT_DB, DB, USER_DB, PASSWORD_DB);

    public DatabaseAuthenticatedProvider(Server server) {
        this.server = server;
    }

    @Override
    public void initialize() {
        databaseConnection = new DatabaseConnection(URL_DATABASE);
        System.out.println("подключение к базе данных пользователей успешно");
    }

    private User findUserByLoginAndPassword(String login, String password) {
        try (PreparedStatement pstmt = databaseConnection.getConnection().prepareStatement("select * from users where login = ? and password = ?")) {
            pstmt.setString(1, login);
            pstmt.setString(2, password);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    String userLogin = rs.getString("login");
                    String userPassword = rs.getString("password");
                    String username = rs.getString("username");
                    String userRole = rs.getString("userRole");
                    return new User(userLogin, userPassword, username, userRole);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private boolean isLoginAlredyExists(String login) {
        try (PreparedStatement pstmt = databaseConnection.getConnection().prepareStatement("select login from users where login = ?")) {
            pstmt.setString(1, login);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    if (rs.getString("login").equals(login)) return true;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private boolean isUsernameAlredyExists(String username) {
        try (PreparedStatement pstmt = databaseConnection.getConnection().prepareStatement("select username from users where username = ?")) {
            pstmt.setString(1, username);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    if (rs.getString("username").equals(username)) return true;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private void writeNewUser(String login, String password, String username) {
        try (PreparedStatement pstmt = databaseConnection.getConnection().prepareStatement("insert into users (login, password, username, userRole) values (?, ?, ?, ?);")) {
            pstmt.setString(1, login);
            pstmt.setString(2, password);
            pstmt.setString(3, username);
            pstmt.setString(4, "User");
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean authenticate(ClientHandler clientHandler, String login, String password) {
        User authUser = findUserByLoginAndPassword(login, password);
        if (authUser == null) {
            clientHandler.sendMsg(ConsoleColors.RED_BRIGHT + "Неверный логин/пароль" + ConsoleColors.RESET);
            return false;
        }
        if (server.isUsernameBusy(authUser.getUsername())) {
            clientHandler.sendMsg(ConsoleColors.RED_BRIGHT + "Учетная запись уже занята" + ConsoleColors.RESET);
            return false;
        }
        clientHandler.setUsername(authUser.getUsername());
        clientHandler.setUserRole(authUser.getUserRole());
        clientHandler.sendMsg(ConsoleColors.GREEN + "Вы подключились под ником " + authUser.getUsername() + ConsoleColors.RESET);
        server.subscribe(clientHandler);
        clientHandler.sendMsg("/authok " + authUser.getUsername());
        return true;
    }

    @Override
    public boolean register(ClientHandler clientHandler, String login, String password, String username) {
        if (login.trim().length() < 3) {
            clientHandler.sendMsg("Логин не может быть меньше 3 символов");
            return false;
        }

        if (username.trim().length() < 3) {
            clientHandler.sendMsg("Имя пользователя не может быть меньше 3 символов");
            return false;
        }

        if (isLoginAlredyExists(login)) {
            clientHandler.sendMsg("Указанный логин уже занят");
            return false;
        }

        if (isUsernameAlredyExists(username)) {
            clientHandler.sendMsg("Указанное имя пользователя уже занято");
            return false;
        }
        writeNewUser(login, password, username);
        clientHandler.setUsername(username);
        clientHandler.sendMsg(ConsoleColors.GREEN + "Вы зарегистрировались и подключились под ником " + username + ConsoleColors.RESET);
        server.subscribe(clientHandler);
        clientHandler.sendMsg("/regok " + username);
        return true;
    }
}
