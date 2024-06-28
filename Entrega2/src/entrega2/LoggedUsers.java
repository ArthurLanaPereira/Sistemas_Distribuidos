package entrega2;

import java.util.ArrayList;
import java.util.List;

public class LoggedUsers {
    private static List<UserInfo> loggedUsers = new ArrayList<>();

    public static void addUser(String ip, String email, String name) {
        loggedUsers.add(new UserInfo(ip, email, name));
        LoginInfoDisplay.updateDisplay();
    }

    public static void removeUser(String email) {
        loggedUsers.removeIf(user -> user.getEmail().equals(email));
        LoginInfoDisplay.updateDisplay();
    }

    public static List<UserInfo> getLoggedUsers() {
        return loggedUsers;
    }

    public static class UserInfo {
        private String ip;
        private String email;
        private String name;

        public UserInfo(String ip, String email, String name) {
            this.ip = ip;
            this.email = email;
            this.name = name;
        }

        public String getIp() {
            return ip;
        }

        public String getEmail() {
            return email;
        }

        public String getName() {
            return name;
        }
    }
}