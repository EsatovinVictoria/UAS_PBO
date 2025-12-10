package dao;

import db.DBConnection;
import model.BBM;
import model.Produk;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProdukDAO {

  // Method untuk mendapatkan semua produk (BBM)
  public List<BBM> getAllBBM() {
    List<BBM> listBBM = new ArrayList<>();
    String sql = "SELECT * FROM produk WHERE jenis = 'bbm' AND aktif = 1 ORDER BY nama";

    try (Connection conn = DBConnection.getConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(sql)) {

      while (rs.next()) {
        BBM bbm = new BBM(
            rs.getInt("id"),
            rs.getString("nama"),
            rs.getDouble("harga"),
            rs.getString("jenis"),
            rs.getDouble("oktan"));
        listBBM.add(bbm);
      }
    } catch (SQLException e) {
      System.err.println("Error mendapatkan data BBM: " + e.getMessage());
    }
    return listBBM;
  }

  // Method untuk mendapatkan BBM by ID
  public BBM getBBMById(int id) {
    String sql = "SELECT * FROM produk WHERE id = ? AND jenis = 'bbm'";
    BBM bbm = null;

    try (Connection conn = DBConnection.getConnection();
        PreparedStatement pstmt = conn.prepareStatement(sql)) {

      pstmt.setInt(1, id);
      try (ResultSet rs = pstmt.executeQuery()) {
        if (rs.next()) {
          bbm = new BBM(
              rs.getInt("id"),
              rs.getString("nama"),
              rs.getDouble("harga"),
              rs.getString("jenis"),
              rs.getDouble("oktan"));
        }
      }
    } catch (SQLException e) {
      System.err.println("Error mendapatkan BBM by ID: " + e.getMessage());
    }
    return bbm;
  }

  // Method untuk menambah BBM
  public boolean addBBM(BBM bbm) {
    String sql = "INSERT INTO produk (nama, harga, jenis, oktan, aktif) VALUES (?, ?, 'bbm', ?, 1)";

    try (Connection conn = DBConnection.getConnection();
        PreparedStatement pstmt = conn.prepareStatement(sql)) {

      pstmt.setString(1, bbm.getNama());
      pstmt.setDouble(2, bbm.getHarga());
      pstmt.setDouble(3, bbm.getOktan());

      int affectedRows = pstmt.executeUpdate();
      return affectedRows > 0;
    } catch (SQLException e) {
      System.err.println("Error menambah BBM: " + e.getMessage());
      return false;
    }
  }

  // Method untuk update BBM
  public boolean updateBBM(BBM bbm) {
    String sql = "UPDATE produk SET nama = ?, harga = ?, jenis = ?, oktan = ? WHERE id = ?";

    try (Connection conn = DBConnection.getConnection();
        PreparedStatement pstmt = conn.prepareStatement(sql)) {

      pstmt.setString(1, bbm.getNama());
      pstmt.setDouble(2, bbm.getHarga());
      pstmt.setString(3, bbm.getJenis());
      pstmt.setDouble(4, bbm.getOktan());
      pstmt.setInt(5, bbm.getId());

      int affectedRows = pstmt.executeUpdate();
      return affectedRows > 0;
    } catch (SQLException e) {
      System.err.println("Error update BBM: " + e.getMessage());
      return false;
    }
  }

  // Method untuk hapus BBM (soft delete)
  public boolean deleteBBM(int id) {
    String sql = "UPDATE produk SET aktif = 0 WHERE id = ?";

    try (Connection conn = DBConnection.getConnection();
        PreparedStatement pstmt = conn.prepareStatement(sql)) {

      pstmt.setInt(1, id);
      int affectedRows = pstmt.executeUpdate();
      return affectedRows > 0;
    } catch (SQLException e) {
      System.err.println("Error hapus BBM: " + e.getMessage());
      return false;
    }
  }

  // Method untuk mendapatkan harga layanan angin
  public double getHargaLayananAngin() {
    String sql = "SELECT harga FROM produk WHERE nama = 'Layanan Angin' AND jenis = 'layanan'";
    double harga = 5000; // default

    try (Connection conn = DBConnection.getConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(sql)) {

      if (rs.next()) {
        harga = rs.getDouble("harga");
      } else {
        // Jika belum ada, buat data default
        createDefaultLayananAngin();
      }
    } catch (SQLException e) {
      System.err.println("Error mendapatkan harga layanan angin: " + e.getMessage());
    }
    return harga;
  }

  // Method untuk update harga layanan angin
  public boolean updateHargaLayananAngin(double harga) {
    String sql = "UPDATE produk SET harga = ? WHERE nama = 'Layanan Angin' AND jenis = 'layanan'";

    try (Connection conn = DBConnection.getConnection();
        PreparedStatement pstmt = conn.prepareStatement(sql)) {

      pstmt.setDouble(1, harga);
      int affectedRows = pstmt.executeUpdate();

      if (affectedRows == 0) {
        // Jika belum ada data, insert baru
        return createDefaultLayananAngin();
      }
      return affectedRows > 0;
    } catch (SQLException e) {
      System.err.println("Error update harga layanan angin: " + e.getMessage());
      return false;
    }
  }

  // Method untuk membuat data default layanan angin
  private boolean createDefaultLayananAngin() {
    String sql = "INSERT INTO produk (nama, harga, jenis, aktif) VALUES ('Layanan Angin', 5000, 'layanan', 1)";

    try (Connection conn = DBConnection.getConnection();
        Statement stmt = conn.createStatement()) {

      int affectedRows = stmt.executeUpdate(sql);
      return affectedRows > 0;
    } catch (SQLException e) {
      System.err.println("Error membuat layanan angin default: " + e.getMessage());
      return false;
    }
  }

  // Method untuk mencari BBM
  public List<BBM> searchBBM(String keyword) {
    List<BBM> listBBM = new ArrayList<>();
    String sql = "SELECT * FROM produk WHERE jenis = 'bbm' AND aktif = 1 " +
        "AND (nama LIKE ? OR jenis LIKE ?) ORDER BY nama";

    try (Connection conn = DBConnection.getConnection();
        PreparedStatement pstmt = conn.prepareStatement(sql)) {

      pstmt.setString(1, "%" + keyword + "%");
      pstmt.setString(2, "%" + keyword + "%");

      try (ResultSet rs = pstmt.executeQuery()) {
        while (rs.next()) {
          BBM bbm = new BBM(
              rs.getInt("id"),
              rs.getString("nama"),
              rs.getDouble("harga"),
              rs.getString("jenis"),
              rs.getDouble("oktan"));
          listBBM.add(bbm);
        }
      }
    } catch (SQLException e) {
      System.err.println("Error mencari BBM: " + e.getMessage());
    }
    return listBBM;
  }
}