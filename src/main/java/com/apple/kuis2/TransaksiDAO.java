/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.apple.kuis2;

/**
 *
 * @author USER
 */
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class TransaksiDAO {

  public void save(Transaksi t) throws SQLException {
    String sql = "INSERT INTO transaksi (id, waktu, jenis_bbm_id, liter, harga_per_liter, total_bayar, metode, plat_nomor, layanan_id, layanan_biaya) " +
                 "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    try (Connection c = DBConnection.getConnection();
         PreparedStatement ps = c.prepareStatement(sql)) {
      ps.setString(1, t.getId().toString());
      ps.setTimestamp(2, Timestamp.valueOf(t.getWaktuTransaksi()));
      // for jenis_bbm_id: kita perlu lookup id dari nama (simple approach: assume jenis_bbm table has row and kita ambil id)
      int bbmId = findJenisBBMIdByName(t.getBbm().getNama(), c);
      ps.setInt(3, bbmId);
      ps.setDouble(4, t.getLiter());
      ps.setDouble(5, t.getBbm().getHargaPerLiter());
      ps.setDouble(6, t.getTotalBayar());
      ps.setString(7, t.getMetodePembayaran().toString());
      ps.setString(8, t.getPlatNomor());
      if (t.getLayanan() != null) {
        int layananId = findLayananIdByName(t.getLayanan().getNama(), c);
        ps.setInt(9, layananId);
        ps.setDouble(10, t.getLayanan().getHarga());
      } else {
        ps.setNull(9, Types.INTEGER);
        ps.setNull(10, Types.DOUBLE);
      }
      ps.executeUpdate();
    }
  }

  private int findJenisBBMIdByName(String nama, Connection c) throws SQLException {
    String q = "SELECT id FROM jenis_bbm WHERE nama = ?";
    try (PreparedStatement ps = c.prepareStatement(q)) {
      ps.setString(1, nama);
      try (ResultSet rs = ps.executeQuery()) {
        if (rs.next()) return rs.getInt("id");
      }
    }
    // fallback: insert jika tidak ada
    String ins = "INSERT INTO jenis_bbm (nama, harga_per_liter) VALUES (?, ?)";
    try (PreparedStatement ps = c.prepareStatement(ins, Statement.RETURN_GENERATED_KEYS)) {
      ps.setString(1, nama);
      ps.setDouble(2, 0.0); // jika tidak tahu harga
      ps.executeUpdate();
      try (ResultSet keys = ps.getGeneratedKeys()) {
        if (keys.next()) return keys.getInt(1);
      }
    }
    throw new SQLException("Gagal mengambil/menyimpan jenis_bbm");
  }

  private int findLayananIdByName(String nama, Connection c) throws SQLException {
    String q = "SELECT id FROM layanan WHERE nama = ?";
    try (PreparedStatement ps = c.prepareStatement(q)) {
      ps.setString(1, nama);
      try (ResultSet rs = ps.executeQuery()) {
        if (rs.next()) return rs.getInt("id");
      }
    }
    // fallback insert layanan
    String ins = "INSERT INTO layanan (nama, harga) VALUES (?, ?)";
    try (PreparedStatement ps = c.prepareStatement(ins, Statement.RETURN_GENERATED_KEYS)) {
      ps.setString(1, nama);
      // harga unknown, set 0
      ps.setDouble(2, 0.0);
      ps.executeUpdate();
      try (ResultSet keys = ps.getGeneratedKeys()) {
        if (keys.next()) return keys.getInt(1);
      }
    }
    throw new SQLException("Gagal mengambil/menyimpan layanan");
  }

  public List<Transaksi> findAll() throws SQLException {
    String sql = "SELECT t.*, jb.nama AS bbm_nama, jb.harga_per_liter AS bbm_harga, l.nama AS layanan_nama, l.harga AS layanan_harga " +
                 "FROM transaksi t " +
                 "JOIN jenis_bbm jb ON t.jenis_bbm_id = jb.id " +
                 "LEFT JOIN layanan l ON t.layanan_id = l.id " +
                 "ORDER BY t.waktu DESC";
    List<Transaksi> hasil = new ArrayList<>();
    try (Connection c = DBConnection.getConnection();
         Statement s = c.createStatement();
         ResultSet rs = s.executeQuery(sql)) {
      while (rs.next()) {
        JenisBBM bbm = new JenisBBM(rs.getString("bbm_nama"), rs.getDouble("bbm_harga"));
        Layanan layanan = null;
        String layananNama = rs.getString("layanan_nama");
        if (layananNama != null) {
          layanan = new IsiAngin(rs.getDouble("layanan_harga")); // asumsikan isi angin, atau bisa map ke jenis layanan
        }
        Transaksi t = new Transaksi(bbm, rs.getDouble("liter"),
            PaymentMethod.valueOf(rs.getString("metode")), rs.getString("plat_nomor"), layanan);
        // But the constructor sets new UUID and waktu = now; to preserve id/waktu we might need a separate constructor or deserialization.
        // For simplicity, we ignore preserving original id/waktu in this demo.
        hasil.add(t);
      }
    }
    return hasil;
  }

  // contoh method group by month
  public Map<String, Double> getPendapatanPerBulan() throws SQLException {
    String sql = "SELECT DATE_FORMAT(waktu, '%Y-%m') AS bulan, SUM(total_bayar + IFNULL(layanan_biaya,0)) AS total " +
                 "FROM transaksi GROUP BY DATE_FORMAT(waktu, '%Y-%m') ORDER BY bulan DESC";
    Map<String, Double> map = new LinkedHashMap<>();
    try (Connection c = DBConnection.getConnection();
         Statement s = c.createStatement();
         ResultSet rs = s.executeQuery(sql)) {
      while (rs.next()) {
        map.put(rs.getString("bulan"), rs.getDouble("total"));
      }
    }
    return map;
  }
}