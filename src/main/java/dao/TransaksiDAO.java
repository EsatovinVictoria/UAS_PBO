package dao;

import db.DBConnection;
import model.Transaksi;
import model.TransaksiDetail;
import model.BBM;
import model.PaymentMethod;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class TransaksiDAO {

  // Method untuk menyimpan transaksi
  public int saveTransaksi(Transaksi transaksi) {
    String sql = "INSERT INTO transaksi (nomor_transaksi, tanggal_waktu, plat_nomor, total, " +
        "metode_pembayaran, user_id) VALUES (?, ?, ?, ?, ?, ?)";

    Connection conn = null;
    PreparedStatement pstmt = null;
    ResultSet generatedKeys = null;
    int transaksiId = -1;

    try {
      conn = DBConnection.getConnection();
      conn.setAutoCommit(false); // Mulai transaction

      // Insert header transaksi
      pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
      pstmt.setString(1, transaksi.getNomorTransaksi());
      pstmt.setTimestamp(2, Timestamp.valueOf(transaksi.getTanggalWaktu()));
      pstmt.setString(3, transaksi.getPlatNomor());
      pstmt.setDouble(4, transaksi.getTotal());

      // Konversi PaymentMethod ke string
      String metodePembayaran = transaksi.getMetodePembayaran() != null
          ? transaksi.getMetodePembayaran().toString()
          : "CASH";
      pstmt.setString(5, metodePembayaran);

      pstmt.setInt(6, transaksi.getUserId());

      int affectedRows = pstmt.executeUpdate();
      if (affectedRows > 0) {
        generatedKeys = pstmt.getGeneratedKeys();
        if (generatedKeys.next()) {
          transaksiId = generatedKeys.getInt(1);
          transaksi.setId(transaksiId);

          // Simpan detail transaksi
          saveTransaksiDetails(conn, transaksiId, transaksi.getDetails());
          conn.commit();
        }
      }
    } catch (SQLException e) {
      if (conn != null) {
        try {
          conn.rollback();
        } catch (SQLException ex) {
          System.err.println("Error rollback transaksi: " + ex.getMessage());
        }
      }
      System.err.println("Error menyimpan transaksi: " + e.getMessage());
      e.printStackTrace();
    } finally {
      closeResources(null, pstmt, generatedKeys);
      if (conn != null) {
        try {
          conn.setAutoCommit(true);
          conn.close();
        } catch (SQLException e) {
          System.err.println("Error menutup koneksi: " + e.getMessage());
        }
      }
    }
    return transaksiId;
  }

  // Method untuk menyimpan detail transaksi
  private void saveTransaksiDetails(Connection conn, int transaksiId,
      List<TransaksiDetail> details) throws SQLException {
    if (details == null || details.isEmpty()) {
      return;
    }

    String sql = "INSERT INTO transaksi_detail (transaksi_id, produk_id, kuantitas, " +
        "harga_satuan, subtotal, include_layanan_angin, harga_layanan_angin, " +
        "tekanan_angin, gratis_layanan_angin) " +
        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

    try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
      for (TransaksiDetail detail : details) {
        pstmt.setInt(1, transaksiId);
        pstmt.setInt(2, detail.getProduk().getId());
        pstmt.setDouble(3, detail.getKuantitas());
        pstmt.setDouble(4, detail.getHargaSatuan());
        pstmt.setDouble(5, detail.getSubtotal());
        pstmt.setBoolean(6, detail.isIncludeLayananAngin());
        pstmt.setDouble(7, detail.getHargaLayananAngin());
        pstmt.setInt(8, detail.getTekananAngin());
        pstmt.setBoolean(9, detail.isGratisLayananAngin());
        pstmt.addBatch();
      }
      pstmt.executeBatch();
    }
  }

  // Method untuk mendapatkan semua transaksi
  public List<Transaksi> getAllTransaksi() {
    List<Transaksi> listTransaksi = new ArrayList<>();
    String sql = "SELECT * FROM transaksi ORDER BY tanggal_waktu DESC";

    try (Connection conn = DBConnection.getConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(sql)) {

      while (rs.next()) {
        // Di method getAllTransaksi() dan getTransaksiById()
        Transaksi transaksi = new Transaksi(
            rs.getInt("id"),
            rs.getString("nomor_transaksi"),
            rs.getTimestamp("tanggal_waktu").toLocalDateTime(),
            rs.getString("plat_nomor"),
            rs.getDouble("total"),
            PaymentMethod.fromString(rs.getString("metode_pembayaran")),
            rs.getInt("user_id"));

        // Ambil detail transaksi
        List<TransaksiDetail> details = getTransaksiDetails(conn, transaksi.getId());
        transaksi.setDetails(details); // Sekarang sudah ada method setDetails()

        listTransaksi.add(transaksi);
      }
    } catch (SQLException e) {
      System.err.println("Error mendapatkan semua transaksi: " + e.getMessage());
      e.printStackTrace();
    }
    return listTransaksi;
  }

  // Method untuk mendapatkan detail transaksi
  private List<TransaksiDetail> getTransaksiDetails(Connection conn, int transaksiId) throws SQLException {
    List<TransaksiDetail> details = new ArrayList<>();
    String sql = "SELECT td.*, p.nama, p.jenis, p.oktan " +
        "FROM transaksi_detail td " +
        "JOIN produk p ON td.produk_id = p.id " +
        "WHERE td.transaksi_id = ?";

    try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
      pstmt.setInt(1, transaksiId);
      try (ResultSet rs = pstmt.executeQuery()) {
        while (rs.next()) {
          // Buat objek BBM
          BBM bbm = new BBM(
              rs.getInt("produk_id"),
              rs.getString("nama"),
              rs.getDouble("harga_satuan"),
              rs.getString("jenis"),
              rs.getDouble("oktan"));

          // Buat objek TransaksiDetail
          TransaksiDetail detail = new TransaksiDetail(
              rs.getInt("id"),
              transaksiId,
              bbm,
              rs.getDouble("kuantitas"),
              rs.getDouble("harga_satuan"),
              rs.getBoolean("include_layanan_angin"),
              rs.getDouble("harga_layanan_angin"),
              rs.getInt("tekanan_angin"),
              rs.getBoolean("gratis_layanan_angin"));

          details.add(detail);
        }
      }
    }
    return details;
  }

  // Method untuk mendapatkan transaksi by tanggal
  public List<Transaksi> getTransaksiByDate(LocalDateTime start, LocalDateTime end) {
    List<Transaksi> listTransaksi = new ArrayList<>();
    String sql = "SELECT * FROM transaksi WHERE tanggal_waktu BETWEEN ? AND ? ORDER BY tanggal_waktu DESC";

    try (Connection conn = DBConnection.getConnection();
        PreparedStatement pstmt = conn.prepareStatement(sql)) {

      pstmt.setTimestamp(1, Timestamp.valueOf(start));
      pstmt.setTimestamp(2, Timestamp.valueOf(end));

      try (ResultSet rs = pstmt.executeQuery()) {
        while (rs.next()) {
          // Di method getAllTransaksi() dan getTransaksiById()
          Transaksi transaksi = new Transaksi(
              rs.getInt("id"),
              rs.getString("nomor_transaksi"),
              rs.getTimestamp("tanggal_waktu").toLocalDateTime(),
              rs.getString("plat_nomor"),
              rs.getDouble("total"),
              PaymentMethod.fromString(rs.getString("metode_pembayaran")),
              rs.getInt("user_id"));

          // Ambil detail transaksi
          List<TransaksiDetail> details = getTransaksiDetails(conn, transaksi.getId());
          transaksi.setDetails(details); // Sekarang sudah ada method setDetails()
        }
      }
    } catch (SQLException e) {
      System.err.println("Error mendapatkan transaksi by date: " + e.getMessage());
      e.printStackTrace();
    }
    return listTransaksi;
  }

  // Method untuk mendapatkan transaksi by user
  public List<Transaksi> getTransaksiByUser(int userId) {
    List<Transaksi> listTransaksi = new ArrayList<>();
    String sql = "SELECT * FROM transaksi WHERE user_id = ? ORDER BY tanggal_waktu DESC";

    try (Connection conn = DBConnection.getConnection();
        PreparedStatement pstmt = conn.prepareStatement(sql)) {

      pstmt.setInt(1, userId);

      try (ResultSet rs = pstmt.executeQuery()) {
        while (rs.next()) {
          // Konversi string ke PaymentMethod
          String metodeStr = rs.getString("metode_pembayaran");
          PaymentMethod metode = PaymentMethod.fromString(metodeStr);

          Transaksi transaksi = new Transaksi(
              rs.getInt("id"),
              rs.getString("nomor_transaksi"),
              rs.getTimestamp("tanggal_waktu").toLocalDateTime(),
              rs.getString("plat_nomor"),
              rs.getDouble("total"),
              metode,
              rs.getInt("user_id"));
          listTransaksi.add(transaksi);
        }
      }
    } catch (SQLException e) {
      System.err.println("Error mendapatkan transaksi by user: " + e.getMessage());
      e.printStackTrace();
    }
    return listTransaksi;
  }

  // Method untuk mendapatkan total pendapatan periode
  public double getTotalPendapatan(LocalDateTime start, LocalDateTime end) {
    String sql = "SELECT SUM(total) as total FROM transaksi WHERE tanggal_waktu BETWEEN ? AND ?";
    double total = 0;

    try (Connection conn = DBConnection.getConnection();
        PreparedStatement pstmt = conn.prepareStatement(sql)) {

      pstmt.setTimestamp(1, Timestamp.valueOf(start));
      pstmt.setTimestamp(2, Timestamp.valueOf(end));

      try (ResultSet rs = pstmt.executeQuery()) {
        if (rs.next()) {
          total = rs.getDouble("total");
        }
      }
    } catch (SQLException e) {
      System.err.println("Error mendapatkan total pendapatan: " + e.getMessage());
      e.printStackTrace();
    }
    return total;
  }

  // Method untuk mendapatkan jumlah transaksi periode
  public int getJumlahTransaksi(LocalDateTime start, LocalDateTime end) {
    String sql = "SELECT COUNT(*) as jumlah FROM transaksi WHERE tanggal_waktu BETWEEN ? AND ?";
    int jumlah = 0;

    try (Connection conn = DBConnection.getConnection();
        PreparedStatement pstmt = conn.prepareStatement(sql)) {

      pstmt.setTimestamp(1, Timestamp.valueOf(start));
      pstmt.setTimestamp(2, Timestamp.valueOf(end));

      try (ResultSet rs = pstmt.executeQuery()) {
        if (rs.next()) {
          jumlah = rs.getInt("jumlah");
        }
      }
    } catch (SQLException e) {
      System.err.println("Error mendapatkan jumlah transaksi: " + e.getMessage());
      e.printStackTrace();
    }
    return jumlah;
  }

  // Method untuk mendapatkan produk terlaris
  public List<Object[]> getProdukTerlaris(int limit) {
    List<Object[]> results = new ArrayList<>();
    String sql = "SELECT p.nama, SUM(td.kuantitas) as total_liter, COUNT(*) as jumlah_transaksi " +
        "FROM transaksi_detail td " +
        "JOIN produk p ON td.produk_id = p.id " +
        "WHERE p.jenis = 'bbm' " +
        "GROUP BY p.id, p.nama " +
        "ORDER BY total_liter DESC " +
        "LIMIT ?";

    try (Connection conn = DBConnection.getConnection();
        PreparedStatement pstmt = conn.prepareStatement(sql)) {

      pstmt.setInt(1, limit);

      try (ResultSet rs = pstmt.executeQuery()) {
        while (rs.next()) {
          Object[] row = {
              rs.getString("nama"),
              rs.getDouble("total_liter"),
              rs.getInt("jumlah_transaksi")
          };
          results.add(row);
        }
      }
    } catch (SQLException e) {
      System.err.println("Error mendapatkan produk terlaris: " + e.getMessage());
      e.printStackTrace();
    }
    return results;
  }

  // Method baru: Mendapatkan transaksi berdasarkan ID
  public Transaksi getTransaksiById(int id) {
    String sql = "SELECT * FROM transaksi WHERE id = ?";
    Transaksi transaksi = null;

    try (Connection conn = DBConnection.getConnection();
        PreparedStatement pstmt = conn.prepareStatement(sql)) {

      pstmt.setInt(1, id);

      try (ResultSet rs = pstmt.executeQuery()) {
        if (rs.next()) {
          // Konversi string ke PaymentMethod
          String metodeStr = rs.getString("metode_pembayaran");
          PaymentMethod metode = PaymentMethod.fromString(metodeStr);

          transaksi = new Transaksi(
              rs.getInt("id"),
              rs.getString("nomor_transaksi"),
              rs.getTimestamp("tanggal_waktu").toLocalDateTime(),
              rs.getString("plat_nomor"),
              rs.getDouble("total"),
              metode,
              rs.getInt("user_id"));

          // Ambil detail transaksi
          List<TransaksiDetail> details = getTransaksiDetails(conn, transaksi.getId());
          transaksi.setDetails(details);
        }
      }
    } catch (SQLException e) {
      System.err.println("Error mendapatkan transaksi by ID: " + e.getMessage());
      e.printStackTrace();
    }
    return transaksi;
  }

  // Method baru: Mendapatkan transaksi dengan detail lengkap
  public List<Transaksi> getAllTransaksiWithDetails() {
    List<Transaksi> listTransaksi = new ArrayList<>();
    String sql = "SELECT * FROM transaksi ORDER BY tanggal_waktu DESC";

    try (Connection conn = DBConnection.getConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(sql)) {

      while (rs.next()) {
        // Konversi string ke PaymentMethod
        String metodeStr = rs.getString("metode_pembayaran");
        PaymentMethod metode = PaymentMethod.fromString(metodeStr);

        Transaksi transaksi = new Transaksi(
            rs.getInt("id"),
            rs.getString("nomor_transaksi"),
            rs.getTimestamp("tanggal_waktu").toLocalDateTime(),
            rs.getString("plat_nomor"),
            rs.getDouble("total"),
            metode,
            rs.getInt("user_id"));

        // Ambil detail transaksi
        List<TransaksiDetail> details = getTransaksiDetails(conn, transaksi.getId());
        transaksi.setDetails(details);

        listTransaksi.add(transaksi);
      }
    } catch (SQLException e) {
      System.err.println("Error mendapatkan semua transaksi dengan detail: " + e.getMessage());
      e.printStackTrace();
    }
    return listTransaksi;
  }

  // Method baru: Hapus transaksi
  public boolean deleteTransaksi(int id) {
    String sql = "DELETE FROM transaksi WHERE id = ?";

    try (Connection conn = DBConnection.getConnection();
        PreparedStatement pstmt = conn.prepareStatement(sql)) {

      pstmt.setInt(1, id);
      int affectedRows = pstmt.executeUpdate();
      return affectedRows > 0;
    } catch (SQLException e) {
      System.err.println("Error menghapus transaksi: " + e.getMessage());
      e.printStackTrace();
      return false;
    }
  }

  // Utility method untuk close resources
  private void closeResources(Connection conn, Statement stmt, ResultSet rs) {
    try {
      if (rs != null)
        rs.close();
      if (stmt != null)
        stmt.close();
    } catch (SQLException e) {
      System.err.println("Error closing resources: " + e.getMessage());
    }
  }
}