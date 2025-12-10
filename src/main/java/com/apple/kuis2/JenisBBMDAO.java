/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.apple.kuis2;

import java.sql.*;
import java.util.*;

/**
 *
 * @author USER
 */
public class JenisBBMDAO {
    
    public void save(JenisBBM jenisBBM) throws SQLException {
        String sql = "INSERT INTO jenis_bbm (nama, harga_per_liter) VALUES (?, ?) " +
                     "ON DUPLICATE KEY UPDATE harga_per_liter = ?";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, jenisBBM.getNama());
            ps.setDouble(2, jenisBBM.getHargaPerLiter());
            ps.setDouble(3, jenisBBM.getHargaPerLiter());
            ps.executeUpdate();
        }
    }
    
    public JenisBBM findById(int id) throws SQLException {
        String sql = "SELECT * FROM jenis_bbm WHERE id = ?";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new JenisBBM(
                        rs.getString("nama"),
                        rs.getDouble("harga_per_liter")
                    );
                }
            }
        }
        return null;
    }
    
    public JenisBBM findByName(String nama) throws SQLException {
        String sql = "SELECT * FROM jenis_bbm WHERE nama = ?";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, nama);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new JenisBBM(
                        rs.getString("nama"),
                        rs.getDouble("harga_per_liter")
                    );
                }
            }
        }
        return null;
    }
    
    public List<JenisBBM> findAll() throws SQLException {
        String sql = "SELECT * FROM jenis_bbm ORDER BY nama";
        List<JenisBBM> hasil = new ArrayList<>();
        try (Connection c = DBConnection.getConnection();
             Statement s = c.createStatement();
             ResultSet rs = s.executeQuery(sql)) {
            while (rs.next()) {
                JenisBBM bbm = new JenisBBM(
                    rs.getString("nama"),
                    rs.getDouble("harga_per_liter")
                );
                hasil.add(bbm);
            }
        }
        return hasil;
    }
    
    public boolean update(JenisBBM jenisBBM) throws SQLException {
        String sql = "UPDATE jenis_bbm SET harga_per_liter = ? WHERE nama = ?";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setDouble(1, jenisBBM.getHargaPerLiter());
            ps.setString(2, jenisBBM.getNama());
            int affectedRows = ps.executeUpdate();
            return affectedRows > 0;
        }
    }
    
    public boolean deleteByName(String nama) throws SQLException {
        System.out.println("DEBUG [JenisBBMDAO]: Mencoba menghapus BBM dengan nama: '" + nama + "'");
        
        // 1. Cari ID BBM berdasarkan nama
        Integer id = findIdByName(nama);
        if (id == null) {
            System.out.println("DEBUG [JenisBBMDAO]: BBM '" + nama + "' tidak ditemukan di database");
            return false;
        }
        
        System.out.println("DEBUG [JenisBBMDAO]: Ditemukan BBM '" + nama + "' dengan ID: " + id);
        
        // 2. Cek apakah BBM digunakan dalam transaksi
        if (isBBMDigunakan(id)) {
            System.out.println("DEBUG [JenisBBMDAO]: BBM ID " + id + " masih digunakan dalam transaksi");
            return false;
        }
        
        // 3. Hapus BBM dari database
        return deleteById(id);
    }
    
    public boolean deleteById(int id) throws SQLException {
        String sql = "DELETE FROM jenis_bbm WHERE id = ?";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            int affectedRows = ps.executeUpdate();
            System.out.println("DEBUG [JenisBBMDAO]: " + affectedRows + " baris BBM terhapus (ID: " + id + ")");
            return affectedRows > 0;
        }
    }
    
    private boolean isBBMDigunakan(int bbmId) throws SQLException {
        String sql = "SELECT COUNT(*) as jumlah FROM transaksi WHERE jenis_bbm_id = ?";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, bbmId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int jumlah = rs.getInt("jumlah");
                    System.out.println("DEBUG [JenisBBMDAO]: BBM ID " + bbmId + " digunakan dalam " + jumlah + " transaksi");
                    return jumlah > 0;
                }
            }
        }
        return false;
    }
    
    public Integer findIdByName(String nama) throws SQLException {
        String sql = "SELECT id FROM jenis_bbm WHERE nama = ?";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, nama);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id");
                }
            }
        }
        return null;
    }
    
    public boolean existsByName(String nama) throws SQLException {
        String sql = "SELECT COUNT(*) FROM jenis_bbm WHERE nama = ?";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, nama);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }
    
    public Map<String, Double> getAllHarga() throws SQLException {
        String sql = "SELECT nama, harga_per_liter FROM jenis_bbm ORDER BY nama";
        Map<String, Double> hargaMap = new LinkedHashMap<>();
        try (Connection c = DBConnection.getConnection();
             Statement s = c.createStatement();
             ResultSet rs = s.executeQuery(sql)) {
            while (rs.next()) {
                hargaMap.put(rs.getString("nama"), rs.getDouble("harga_per_liter"));
            }
        }
        return hargaMap;
    }
    
    // Method untuk mendapatkan ID BBM berdasarkan nama
    public Integer getIdByName(String nama) throws SQLException {
        return findIdByName(nama);
    }
    
    // Method untuk debug: tampilkan semua BBM di database
    public void printAllBBM() throws SQLException {
        System.out.println("\n=== DAFTAR BBM DI DATABASE ===");
        List<JenisBBM> semua = findAll();
        for (JenisBBM bbm : semua) {
            Integer id = findIdByName(bbm.getNama());
            System.out.println("ID: " + id + " | Nama: '" + bbm.getNama() + "' | Harga: " + bbm.getHargaPerLiter());
        }
        System.out.println("===============================\n");
    }
}