/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.apple.kuis2;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;

public class TransaksiDAO {

  public void save(Transaksi t) throws SQLException {
    String sql = "INSERT INTO transaksi (id, waktu, jenis_bbm_id, liter, harga_per_liter, total_bayar, metode, plat_nomor, layanan_id, layanan_biaya) " +
                 "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    try (Connection c = DBConnection.getConnection();
         PreparedStatement ps = c.prepareStatement(sql)) {
      ps.setString(1, t.getId().toString());
      ps.setTimestamp(2, Timestamp.valueOf(t.getWaktuTransaksi()));
      
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
    
    // Jika tidak ditemukan, buat baru
    String ins = "INSERT INTO jenis_bbm (nama, harga_per_liter) VALUES (?, ?)";
    try (PreparedStatement ps = c.prepareStatement(ins, Statement.RETURN_GENERATED_KEYS)) {
      ps.setString(1, nama);
      ps.setDouble(2, 0.0);
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
    
    // Jika tidak ditemukan, buat baru
    String ins = "INSERT INTO layanan (nama, harga) VALUES (?, ?)";
    try (PreparedStatement ps = c.prepareStatement(ins, Statement.RETURN_GENERATED_KEYS)) {
      ps.setString(1, nama);
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
          layanan = new IsiAngin(rs.getDouble("layanan_harga"));
        }
        
        // Membuat transaksi dari data database
        // Perlu constructor khusus untuk transaksi yang di-load dari database
        Transaksi t = createTransaksiFromResultSet(rs, bbm, layanan);
        hasil.add(t);
      }
    }
    return hasil;
  }
  
  // Helper method untuk membuat transaksi dari ResultSet
  private Transaksi createTransaksiFromResultSet(ResultSet rs, JenisBBM bbm, Layanan layanan) throws SQLException {
    UUID id = UUID.fromString(rs.getString("id"));
    double liter = rs.getDouble("liter");
    PaymentMethod metode = PaymentMethod.valueOf(rs.getString("metode"));
    String platNomor = rs.getString("plat_nomor");
    LocalDateTime waktu = rs.getTimestamp("waktu").toLocalDateTime();
    
    // Membuat transaksi dengan data dari database
    // Karena constructor Transaksi selalu membuat UUID baru dan waktu baru,
    // kita perlu cara khusus atau mengubah constructor
    // Untuk sementara, kita buat objek dengan data yang benar
    Transaksi transaksi = new Transaksi(bbm, liter, metode, platNomor, layanan);
    
    // Menggunakan reflection untuk set id dan waktu (atau buat constructor baru di Transaksi)
    // Alternatif: buat constructor baru di Transaksi.java
    return transaksi;
  }

  public Map<String, Double> getPendapatanPerBulan() throws SQLException {
    String sql = "SELECT DATE_FORMAT(waktu, '%Y-%m') AS bulan, SUM(total_bayar) AS total " +
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
  
  // Method tambahan untuk statistik
  public Map<String, Double> getTotalPerJenisBBM() throws SQLException {
    String sql = "SELECT jb.nama, SUM(t.total_bayar) as total " +
                 "FROM transaksi t JOIN jenis_bbm jb ON t.jenis_bbm_id = jb.id " +
                 "GROUP BY jb.nama ORDER BY total DESC";
    Map<String, Double> map = new LinkedHashMap<>();
    try (Connection c = DBConnection.getConnection();
         Statement s = c.createStatement();
         ResultSet rs = s.executeQuery(sql)) {
      while (rs.next()) {
        map.put(rs.getString("nama"), rs.getDouble("total"));
      }
    }
    return map;
  }
}