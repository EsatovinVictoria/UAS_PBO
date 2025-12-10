package dao;

import db.DBConnection;
import model.User;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {

  // Method untuk authenticate user
  public User authenticate(String username, String password) {
    String sql = "SELECT * FROM users WHERE username = ? AND password = MD5(?) AND aktif = 1";
    User user = null;

    try (Connection conn = DBConnection.getConnection();
        PreparedStatement pstmt = conn.prepareStatement(sql)) {

      pstmt.setString(1, username);
      pstmt.setString(2, password);

      try (ResultSet rs = pstmt.executeQuery()) {
        if (rs.next()) {
          user = new User(
              rs.getInt("id"),
              rs.getString("username"),
              rs.getString("password"),
              rs.getString("nama_lengkap"),
              rs.getString("role"),
              rs.getString("no_telepon"),
              rs.getString("alamat"),
              rs.getBoolean("aktif"));
        }
      }
    } catch (SQLException e) {
      System.err.println("Error authenticate user: " + e.getMessage());
    }
    return user;
  }

  // Method untuk mendapatkan semua user
  public List<User> getAllUsers() {
    List<User> listUsers = new ArrayList<>();
    String sql = "SELECT * FROM users ORDER BY nama_lengkap";

    try (Connection conn = DBConnection.getConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(sql)) {

      while (rs.next()) {
        User user = new User(
            rs.getInt("id"),
            rs.getString("username"),
            rs.getString("password"),
            rs.getString("nama_lengkap"),
            rs.getString("role"),
            rs.getString("no_telepon"),
            rs.getString("alamat"),
            rs.getBoolean("aktif"));
        listUsers.add(user);
      }
    } catch (SQLException e) {
      System.err.println("Error mendapatkan semua user: " + e.getMessage());
    }
    return listUsers;
  }

  // Method untuk mendapatkan user by ID
  public User getUserById(int id) {
    String sql = "SELECT * FROM users WHERE id = ?";
    User user = null;

    try (Connection conn = DBConnection.getConnection();
        PreparedStatement pstmt = conn.prepareStatement(sql)) {

      pstmt.setInt(1, id);

      try (ResultSet rs = pstmt.executeQuery()) {
        if (rs.next()) {
          user = new User(
              rs.getInt("id"),
              rs.getString("username"),
              rs.getString("password"),
              rs.getString("nama_lengkap"),
              rs.getString("role"),
              rs.getString("no_telepon"),
              rs.getString("alamat"),
              rs.getBoolean("aktif"));
        }
      }
    } catch (SQLException e) {
      System.err.println("Error mendapatkan user by ID: " + e.getMessage());
    }
    return user;
  }

  // Method untuk menambah user
  public boolean addUser(User user) {
    String sql = "INSERT INTO users (username, password, nama_lengkap, role, no_telepon, alamat, aktif) " +
        "VALUES (?, MD5(?), ?, ?, ?, ?, ?)";

    try (Connection conn = DBConnection.getConnection();
        PreparedStatement pstmt = conn.prepareStatement(sql)) {

      pstmt.setString(1, user.getUsername());
      pstmt.setString(2, user.getPassword());
      pstmt.setString(3, user.getNamaLengkap());
      pstmt.setString(4, user.getRole());
      pstmt.setString(5, user.getNoTelepon());
      pstmt.setString(6, user.getAlamat());
      pstmt.setBoolean(7, user.isAktif());

      int affectedRows = pstmt.executeUpdate();
      return affectedRows > 0;
    } catch (SQLException e) {
      System.err.println("Error menambah user: " + e.getMessage());
      return false;
    }
  }

  // Method untuk update user
  public boolean updateUser(User user) {
    String sql = "UPDATE users SET username = ?, nama_lengkap = ?, role = ?, " +
        "no_telepon = ?, alamat = ?, aktif = ? WHERE id = ?";

    try (Connection conn = DBConnection.getConnection();
        PreparedStatement pstmt = conn.prepareStatement(sql)) {

      pstmt.setString(1, user.getUsername());
      pstmt.setString(2, user.getNamaLengkap());
      pstmt.setString(3, user.getRole());
      pstmt.setString(4, user.getNoTelepon());
      pstmt.setString(5, user.getAlamat());
      pstmt.setBoolean(6, user.isAktif());
      pstmt.setInt(7, user.getId());

      int affectedRows = pstmt.executeUpdate();
      return affectedRows > 0;
    } catch (SQLException e) {
      System.err.println("Error update user: " + e.getMessage());
      return false;
    }
  }

  // Method untuk ganti password
  public boolean changePassword(int userId, String newPassword) {
    String sql = "UPDATE users SET password = MD5(?) WHERE id = ?";

    try (Connection conn = DBConnection.getConnection();
        PreparedStatement pstmt = conn.prepareStatement(sql)) {

      pstmt.setString(1, newPassword);
      pstmt.setInt(2, userId);

      int affectedRows = pstmt.executeUpdate();
      return affectedRows > 0;
    } catch (SQLException e) {
      System.err.println("Error ganti password: " + e.getMessage());
      return false;
    }
  }

  // Method untuk hapus user (soft delete)
  public boolean deleteUser(int id) {
    String sql = "UPDATE users SET aktif = 0 WHERE id = ?";

    try (Connection conn = DBConnection.getConnection();
        PreparedStatement pstmt = conn.prepareStatement(sql)) {

      pstmt.setInt(1, id);
      int affectedRows = pstmt.executeUpdate();
      return affectedRows > 0;
    } catch (SQLException e) {
      System.err.println("Error hapus user: " + e.getMessage());
      return false;
    }
  }

  // Method untuk cek username unique
  public boolean isUsernameUnique(String username) {
    String sql = "SELECT COUNT(*) as count FROM users WHERE username = ?";

    try (Connection conn = DBConnection.getConnection();
        PreparedStatement pstmt = conn.prepareStatement(sql)) {

      pstmt.setString(1, username);

      try (ResultSet rs = pstmt.executeQuery()) {
        if (rs.next()) {
          return rs.getInt("count") == 0;
        }
      }
    } catch (SQLException e) {
      System.err.println("Error cek username unique: " + e.getMessage());
    }
    return false;
  }

  // Method untuk mencari user
  public List<User> searchUsers(String keyword) {
    List<User> listUsers = new ArrayList<>();
    String sql = "SELECT * FROM users WHERE nama_lengkap LIKE ? OR username LIKE ? ORDER BY nama_lengkap";

    try (Connection conn = DBConnection.getConnection();
        PreparedStatement pstmt = conn.prepareStatement(sql)) {

      pstmt.setString(1, "%" + keyword + "%");
      pstmt.setString(2, "%" + keyword + "%");

      try (ResultSet rs = pstmt.executeQuery()) {
        while (rs.next()) {
          User user = new User(
              rs.getInt("id"),
              rs.getString("username"),
              rs.getString("password"),
              rs.getString("nama_lengkap"),
              rs.getString("role"),
              rs.getString("no_telepon"),
              rs.getString("alamat"),
              rs.getBoolean("aktif"));
          listUsers.add(user);
        }
      }
    } catch (SQLException e) {
      System.err.println("Error mencari user: " + e.getMessage());
    }
    return listUsers;
  }

  // Method untuk mendapatkan jumlah user
  public int getTotalUsers() {
    String sql = "SELECT COUNT(*) as total FROM users";
    int total = 0;

    try (Connection conn = DBConnection.getConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(sql)) {

      if (rs.next()) {
        total = rs.getInt("total");
      }
    } catch (SQLException e) {
      System.err.println("Error mendapatkan total user: " + e.getMessage());
    }
    return total;
  }
}