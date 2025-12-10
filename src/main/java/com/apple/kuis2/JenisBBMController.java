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
public class JenisBBMController {
    private final JenisBBMDAO jenisBBMDAO;
    private final BBMManager bbmManager;
    
    public JenisBBMController() {
        this.jenisBBMDAO = new JenisBBMDAO();
        this.bbmManager = new BBMManager();
        loadFromDatabase();
    }
    
    public JenisBBMController(BBMManager bbmManager) {
        this.jenisBBMDAO = new JenisBBMDAO();
        this.bbmManager = bbmManager;
        loadFromDatabase();
    }
    
    private void loadFromDatabase() {
        try {
            List<JenisBBM> dariDB = jenisBBMDAO.findAll();
            if (!dariDB.isEmpty()) {
                bbmManager.setDaftar(dariDB);
                System.out.println("DEBUG [JenisBBMController]: Loaded " + dariDB.size() + " BBM from database");
            } else {
                System.out.println("DEBUG [JenisBBMController]: Database kosong, inisialisasi default");
                initializeDefaultBBM();
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            System.out.println("DEBUG [JenisBBMController]: Error loading from DB, using default");
            bbmManager.setDaftar(BBMManager.defaultHarga().getDaftar());
        }
    }
    
    private void initializeDefaultBBM() {
        List<JenisBBM> defaultBBM = new ArrayList<>();
        // defaultBBM.add(new JenisBBM("Pertalite", 10000.0));
        // defaultBBM.add(new JenisBBM("Pertamax", 14000.0));
        // defaultBBM.add(new JenisBBM("Solar", 7000.0));
        // defaultBBM.add(new JenisBBM("Dexlite", 13000.0));
        
        bbmManager.setDaftar(defaultBBM);
        
        // Simpan ke database
        for (JenisBBM bbm : defaultBBM) {
            try {
                jenisBBMDAO.save(bbm);
                System.out.println("DEBUG [JenisBBMController]: Saved default BBM: " + bbm.getNama());
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }
    
    public void tambahJenisBBM(JenisBBM jenisBBM) {
        System.out.println("DEBUG [JenisBBMController]: Menambah BBM: " + jenisBBM.getNama());
        bbmManager.tambah(jenisBBM);
        try {
            jenisBBMDAO.save(jenisBBM);
            System.out.println("DEBUG [JenisBBMController]: BBM berhasil disimpan ke database");
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new RuntimeException("Gagal menyimpan ke database: " + ex.getMessage());
        }
    }
    
    public boolean updateHarga(String nama, double hargaBaru) {
        System.out.println("DEBUG [JenisBBMController]: Update harga BBM '" + nama + "' menjadi " + hargaBaru);
        
        Optional<JenisBBM> opt = bbmManager.findByName(nama);
        if (opt.isPresent()) {
            JenisBBM j = opt.get();
            double hargaLama = j.getHargaPerLiter();
            j.setHargaPerLiter(hargaBaru);
            
            try {
                boolean success = jenisBBMDAO.update(j);
                if (success) {
                    System.out.println("DEBUG [JenisBBMController]: Harga BBM '" + nama + "' berhasil diupdate (" + hargaLama + " -> " + hargaBaru + ")");
                } else {
                    System.out.println("DEBUG [JenisBBMController]: Gagal update harga BBM '" + nama + "'");
                }
                return success;
            } catch (SQLException ex) {
                ex.printStackTrace();
                return false;
            }
        } else {
            System.out.println("DEBUG [JenisBBMController]: BBM '" + nama + "' tidak ditemukan untuk diupdate");
            return false;
        }
    }
    
    public boolean hapusJenisBBM(String nama) {
        System.out.println("\n=== DEBUG [JenisBBMController]: PROSES HAPUS BBM ===");
        System.out.println("DEBUG [JenisBBMController]: Mencoba menghapus BBM: '" + nama + "'");
        
        try {
            // Debug: tampilkan semua BBM di database sebelum hapus
            jenisBBMDAO.printAllBBM();
            
            // Coba hapus dari database
            boolean berhasil = jenisBBMDAO.deleteByName(nama);
            
            if (berhasil) {
                // Update local list
                List<JenisBBM> baru = new ArrayList<>();
                for (JenisBBM bbm : bbmManager.getDaftar()) {
                    if (!bbm.getNama().equalsIgnoreCase(nama)) {
                        baru.add(bbm);
                    }
                }
                bbmManager.setDaftar(baru);
                System.out.println("DEBUG [JenisBBMController]: BBM '" + nama + "' berhasil dihapus dari database dan memori");
                
                // Debug: tampilkan semua BBM di database setelah hapus
                jenisBBMDAO.printAllBBM();
            } else {
                System.out.println("DEBUG [JenisBBMController]: BBM '" + nama + "' gagal dihapus. Kemungkinan:");
                System.out.println("  1. Masih digunakan dalam transaksi");
                System.out.println("  2. Tidak ditemukan di database");
                System.out.println("  3. Nama tidak exact match");
            }
            
            return berhasil;
            
        } catch (SQLException ex) {
            ex.printStackTrace();
            System.out.println("DEBUG [JenisBBMController]: SQL Error saat menghapus BBM '" + nama + "': " + ex.getMessage());
            return false;
        }
    }
    
    // Method alternatif untuk hapus dengan ID (jika diperlukan)
    public boolean hapusJenisBBMById(int id) {
        try {
            return jenisBBMDAO.deleteById(id);
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }
    
    public List<JenisBBM> getSemuaJenisBBM() {
        return bbmManager.getDaftar();
    }
    
    public Optional<JenisBBM> cariJenisBBM(String nama) {
        return bbmManager.findByName(nama);
    }
    
    public String[] getTableHeader() {
        return new String[] { "No", "Nama BBM", "Harga per Liter (Rp)" };
    }
    
    public Object[][] getTableData() {
        List<JenisBBM> daftar = bbmManager.getDaftar();
        Object[][] data = new Object[daftar.size()][3];
        
        for (int i = 0; i < daftar.size(); i++) {
            JenisBBM bbm = daftar.get(i);
            data[i][0] = i + 1;
            data[i][1] = bbm.getNama();
            data[i][2] = String.format("%,.0f", bbm.getHargaPerLiter());
        }
        
        return data;
    }
    
    public void refreshFromDatabase() {
        System.out.println("DEBUG [JenisBBMController]: Refresh data dari database");
        loadFromDatabase();
    }
    
    public Map<String, Double> getHargaMap() {
        try {
            return jenisBBMDAO.getAllHarga();
        } catch (SQLException ex) {
            ex.printStackTrace();
            return new HashMap<>();
        }
    }
    
    // Method untuk mendapatkan ID BBM
    public Integer getIdBBM(String nama) {
        try {
            Integer id = jenisBBMDAO.getIdByName(nama);
            System.out.println("DEBUG [JenisBBMController]: ID BBM '" + nama + "' = " + id);
            return id;
        } catch (SQLException ex) {
            ex.printStackTrace();
            return null;
        }
    }
    
    // Method untuk debug
    public void printDebugInfo() {
        System.out.println("\n=== DEBUG INFO JenisBBMController ===");
        System.out.println("Jumlah BBM di memori: " + bbmManager.getDaftar().size());
        for (JenisBBM bbm : bbmManager.getDaftar()) {
            System.out.println("  - " + bbm.getNama() + " (Rp " + bbm.getHargaPerLiter() + ")");
        }
        
        try {
            jenisBBMDAO.printAllBBM();
        } catch (SQLException ex) {
            System.out.println("Error accessing database: " + ex.getMessage());
        }
    }
}