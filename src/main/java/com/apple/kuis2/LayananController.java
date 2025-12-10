/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.apple.kuis2;

import java.sql.SQLException;
import java.util.*;

/**
 *
 * @author USER
 */
public class LayananController {
    private final LayananDAO layananDAO;
    private final List<Layanan> daftarLayanan;
    
    public LayananController() {
        this.layananDAO = new LayananDAO();
        this.daftarLayanan = new ArrayList<>();
        loadFromDatabase();
    }
    
    private void loadFromDatabase() {
        try {
            List<Layanan> dariDB = layananDAO.findAll();
            if (!dariDB.isEmpty()) {
                daftarLayanan.clear();
                daftarLayanan.addAll(dariDB);
            } else {
                // Jika database kosong, inisialisasi dengan default
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            // Fallback ke default jika database error
        }
    }
    
    public void tambahLayanan(Layanan layanan) {
        daftarLayanan.add(layanan);
        try {
            layananDAO.save(layanan);
            System.out.println("Layanan '" + layanan.getNama() + "' berhasil ditambahkan ke database");
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new RuntimeException("Gagal menyimpan ke database: " + ex.getMessage());
        }
    }
    
    public boolean updateHargaLayanan(String nama, double hargaBaru) {
        for (Layanan layanan : daftarLayanan) {
            if (layanan.getNama().equalsIgnoreCase(nama)) {
                // Karena Layanan adalah interface, kita perlu membuat objek baru
                if (layanan instanceof IsiAngin) {
                    IsiAngin baru = new IsiAngin(hargaBaru);
                    daftarLayanan.remove(layanan);
                    daftarLayanan.add(baru);
                    
                    try {
                        boolean success = layananDAO.update(baru);
                        if (success) {
                            System.out.println("Harga layanan '" + nama + "' berhasil diupdate");
                        }
                        return success;
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                        return false;
                    }
                }
            }
        }
        return false;
    }
    
public boolean hapusLayanan(String nama) {
    try {
        // Cari ID berdasarkan nama
        Integer id = layananDAO.findIdByName(nama);
        if (id == null) {
            System.out.println("DEBUG: Layanan '" + nama + "' tidak ditemukan di database");
            return false;
        }
        
        // Hapus berdasarkan ID
        boolean berhasil = layananDAO.deleteById(id);
        
        if (berhasil) {
            // Hapus dari daftar di memori
            daftarLayanan.removeIf(l -> l.getNama().equalsIgnoreCase(nama));
            System.out.println("DEBUG: Layanan ID " + id + " ('" + nama + "') berhasil dihapus");
        }
        return berhasil;
    } catch (SQLException ex) {
        ex.printStackTrace();
        return false;
    }
}
    
    public List<Layanan> getSemuaLayanan() {
        return new ArrayList<>(daftarLayanan);
    }
    
    public Optional<Layanan> cariLayanan(String nama) {
        return daftarLayanan.stream()
                .filter(l -> l.getNama().equalsIgnoreCase(nama))
                .findFirst();
    }
    
    public String[] getTableHeader() {
        return new String[] { "No", "Nama Layanan", "Harga (Rp)" };
    }
    
    public Object[][] getTableData() {
        Object[][] data = new Object[daftarLayanan.size()][3];
        
        for (int i = 0; i < daftarLayanan.size(); i++) {
            Layanan layanan = daftarLayanan.get(i);
            data[i][0] = i + 1;
            data[i][1] = layanan.getNama();
            data[i][2] = String.format("%,.0f", layanan.getHarga());
        }
        
        return data;
    }
    
    public void refreshFromDatabase() {
        loadFromDatabase();
    }
    
    public Map<String, Double> getLayananMap() {
        try {
            return layananDAO.getAllLayanan();
        } catch (SQLException ex) {
            ex.printStackTrace();
            return new HashMap<>();
        }
    }
    
    public Layanan getLayananIsiAngin() {
        // Method khusus untuk mendapatkan layanan isi angin
        for (Layanan layanan : daftarLayanan) {
            if (layanan.getNama().contains("Isi Angin")) {
                return layanan;
            }
        }
        return null;
    }
    
    public void tambahIsiAngin(double harga) {
        Layanan isiAngin = new IsiAngin(harga);
        tambahLayanan(isiAngin);
    }
    
    // Method untuk mendapatkan ID layanan
    public Integer getIdLayanan(String nama) {
        try {
            return layananDAO.getIdByName(nama);
        } catch (SQLException ex) {
            ex.printStackTrace();
            return null;
        }
    }
}