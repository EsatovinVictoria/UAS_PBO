package service;

import dao.UserDAO;
import model.User;
import java.util.List;

public class UserService {
    private final UserDAO userDAO;

    public UserService() {
        this.userDAO = new UserDAO();
    }

    public User authenticate(String username, String password) {
        return userDAO.authenticate(username, password);
    }

    public List<User> getAllUsers() {
        return userDAO.getAllUsers();
    }

    public User getUserById(int id) {
        return userDAO.getUserById(id);
    }

    public boolean tambahUser(User user) {
        return userDAO.addUser(user);
    }

    public boolean updateUser(User user) {
        return userDAO.updateUser(user);
    }

    public boolean hapusUser(int id) {
        return userDAO.deleteUser(id);
    }

    public boolean gantiPassword(int userId, String newPassword) {
        return userDAO.changePassword(userId, newPassword);
    }

    public boolean isUsernameUnique(String username) {
        return userDAO.isUsernameUnique(username);
    }

    public List<User> cariUser(String keyword) {
        return userDAO.searchUsers(keyword);
    }

    public List<User> getUsersByRole(String role) {
        List<User> allUsers = getAllUsers();
        return allUsers.stream()
                .filter(user -> user.getRole().equalsIgnoreCase(role))
                .toList();
    }

    public boolean toggleUserStatus(int id) {
        User user = getUserById(id);
        if (user != null) {
            user.setAktif(!user.isAktif());
            return updateUser(user);
        }
        return false;
    }

    public int getJumlahUser() {
        return userDAO.getTotalUsers();
    }

    public int getJumlahUserAktif() {
        List<User> users = getAllUsers();
        return (int) users.stream()
                .filter(User::isAktif)
                .count();
    }
}