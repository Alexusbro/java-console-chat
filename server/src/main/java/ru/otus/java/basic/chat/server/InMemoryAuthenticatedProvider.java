//package ru.otus.java.basic.chat.server;
//
//import java.util.List;
//import java.util.concurrent.CopyOnWriteArrayList;
//
//public class InMemoryAuthenticatedProvider implements AuthenticatedProvider {
//    private class User {
//        private String login;
//        private String password;
//        private String username;
//        private UserRole userRole;
//
//
//        public User(String login, String password, String username) {
//            this.login = login;
//            this.password = password;
//            this.username = username;
//            this.userRole = UserRole.USER;
//        }
//    }
//
//    private Server server;
//    private List<User> users;
//
//
//    public InMemoryAuthenticatedProvider(Server server) {
//        this.server = server;
//        this.users = new CopyOnWriteArrayList<>();
//        User admin = new User("admin", "admin", "Admin");
//        admin.userRole = UserRole.ADMIN;
//        this.users.add(admin);
//        this.users.add(new User("user1", "user1", "Ivan"));
//        this.users.add(new User("user2", "user2", "Anna"));
//        this.users.add(new User("user3", "user3", "Anton"));
//    }
//
//    @Override
//    public void initialize() {
//        System.out.println("Сервер аутентификации запущен в режиме InMemory");
//    }
//
//    private User getUserByLoginAndPassword(String login, String password) {
//        for (User user : users) {
//            if (user.login.equals(login) && user.password.equals(password)) {
//                return user;
//            }
//        }
//        return null;
//    }
//
//    @Override
//    public boolean authenticate(ClientHandler clientHandler, String login, String password) {
//        User authUser = getUserByLoginAndPassword(login, password);
//        if (authUser == null) {
//            clientHandler.sendMsg(ConsoleColors.RED_BRIGHT + "Неверный логин/пароль" + ConsoleColors.RESET);
//            return false;
//        }
//        if (server.isUsernameBusy(authUser.username)) {
//            clientHandler.sendMsg(ConsoleColors.RED_BRIGHT + "Учетная запись уже занята" + ConsoleColors.RESET);
//            return false;
//        }
//        clientHandler.setUsername(authUser.username);
//        clientHandler.setUserRole(authUser.userRole);
//        clientHandler.sendMsg(ConsoleColors.GREEN + "Вы подключились под ником " + authUser.username + ConsoleColors.RESET);
//        server.subscribe(clientHandler);
//        clientHandler.sendMsg("/authok " + authUser.username);
//        return true;
//    }
//
//    private boolean isLoginAlredyExists(String login) {
//        for (User user : users) {
//            if (user.login.equals(login)) {
//                return true;
//            }
//        }
//        return false;
//    }
//
//    private boolean isUsernameAlredyExists(String username) {
//        for (User user : users) {
//            if (user.username.equals(username)) {
//                return true;
//            }
//        }
//        return false;
//    }
//
//    @Override
//    public boolean register(ClientHandler clientHandler, String login, String password, String username) {
//        if (login.trim().length() < 3) {
//            clientHandler.sendMsg("Логин не может быть меньше 3 символов");
//            return false;
//        }
//
//        if (username.trim().length() < 3) {
//            clientHandler.sendMsg("Имя пользователя не может быть меньше 3 символов");
//            return false;
//        }
//
//        if (isLoginAlredyExists(login)) {
//            clientHandler.sendMsg("Указанный логин уже занят");
//            return false;
//        }
//
//        if (isUsernameAlredyExists(username)) {
//            clientHandler.sendMsg("Указанное имя пользователя уже занято");
//            return false;
//        }
//
//        users.add(new User(login, password, username));
//        clientHandler.setUsername(username);
//        clientHandler.sendMsg(ConsoleColors.GREEN + "Вы зарегистрировались и подключились под ником " + username + ConsoleColors.RESET);
//        server.subscribe(clientHandler);
//        clientHandler.sendMsg("/regok " + username);
//        return true;
//    }
//}
