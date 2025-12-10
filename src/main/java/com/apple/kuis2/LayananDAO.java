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
public class LayananDAO {
    
    public void save(Layanan layanan) throws SQLException {
        String sql = "INSERT INTO layanan (nama, harga) VALUES (?, ?) " +
                     "ON DUPLICATE KEY UPDATE harga = ?";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, layanan.getNama());
            ps.setDouble(2, layanan.getHarga());
            ps.setDouble(3, layanan.getHarga());
            ps.executeUpdate();
        }
    }
    
    public Layanan findById(int id) throws SQLException {
        String sql = "SELECT * FROM layanan WHERE id = ?";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return createLayananFromResultSet(rs);
                }
            }
        }
        return null;
    }
    
public Integer findIdByName(String nama) throws SQLException {
    String sql = "SELECT id FROM layanan WHERE nama LIKE ?";
    try (Connection c = DBConnection.getConnection();
         PreparedStatement ps = c.prepareStatement(sql)) {
        ps.setString(1, "%" + nama + "%");
        try (ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt("id");
            }
        }
    }
    return null;
}
    
    public List<Layanan> findAll() throws SQLException {
        String sql = "SELECT * FROM layanan ORDER BY nama";
        List<Layanan> hasil = new ArrayList<>();
        try (Connection c = DBConnection.getConnection();
             Statement s = c.createStatement();
             ResultSet rs = s.executeQuery(sql)) {
            while (rs.next()) {
                Layanan layanan = createLayananFromResultSet(rs);
                hasil.add(layanan);
            }
        }
        return hasil;
    }
    
    private Layanan createLayananFromResultSet(ResultSet rs) throws SQLException {
        String nama = rs.getString("nama");
        double harga = rs.getDouble("harga");
        
        // Logic untuk menentukan jenis layanan berdasarkan nama
        if (nama.contains("Isi Angin") || nama.contains("Pompa Ban")) {
            return new IsiAngin(harga);
        }
        // Tambahkan jenis layanan lain di sini jika ada
        
        // Default: return sebagai IsiAngin (bisa diubah sesuai kebutuhan)
        return new IsiAngin(harga);
    }
    
    public boolean update(Layanan layanan) throws SQLException {
        String sql = "UPDATE layanan SET harga = ? WHERE nama = ?";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setDouble(1, layanan.getHarga());
            ps.setString(2, layanan.getNama());
            int affectedRows = ps.executeUpdate();
            return affectedRows > 0;
        }
    }
    
public boolean deleteById(int id) throws SQLException {
    String sql = "DELETE FROM layanan WHERE id = ?";
    try (Connection c = DBConnection.getConnection();
         PreparedStatement ps = c.prepareStatement(sql)) {
        ps.setInt(1, id);
        int affectedRows = ps.executeUpdate();
        return affectedRows > 0;
    }
}
    
    public boolean existsByName(String nama) throws SQLException {
        String sql = "SELECT COUNT(*) FROM layanan WHERE nama = ?";
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
    
    public Map<String, Double> getAllLayanan() throws SQLException {
        String sql = "SELECT nama, harga FROM layanan ORDER BY nama";
        Map<String, Double> layananMap = new LinkedHashMap<>();
        try (Connection c = DBConnection.getConnection();
             Statement s = c.createStatement();
             ResultSet rs = s.executeQuery(sql)) {
            while (rs.next()) {
                layananMap.put(rs.getString("nama"), rs.getDouble("harga"));
            }
        }
        return layananMap;
    }
    
    // Method untuk mendapatkan ID layanan berdasarkan nama
    public Integer getIdByName(String nama) throws SQLException {
        String sql = "SELECT id FROM layanan WHERE nama = ?";
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
}