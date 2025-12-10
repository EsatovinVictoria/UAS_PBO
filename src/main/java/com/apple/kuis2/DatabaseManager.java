/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.apple.kuis2;

import java.util.*;

/**
 *
 * @author USER
 */
public class DatabaseManager {
    private static DatabaseManager instance;
    private final JenisBBMController bbmController;
    private final LayananController layananController;
    private final TransaksiController transaksiController;
    
    private DatabaseManager() {
        // Inisialisasi controller dengan urutan yang benar
        this.bbmController = new JenisBBMController();
        this.layananController = new LayananController();
        
        // Load data dari database terlebih dahulu
        this.bbmController.refreshFromDatabase();
        this.layananController.refreshFromDatabase();
        
        // Buat TransaksiController dengan data yang sudah di-load
        this.transaksiController = new TransaksiController(
            new BBMManager(bbmController.getSemuaJenisBBM())
        );
    }
    
    public static DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }
    
    public JenisBBMController getBbmController() {
        return bbmController;
    }
    
    public LayananController getLayananController() {
        return layananController;
    }
    
    public TransaksiController getTransaksiController() {
        return transaksiController;
    }
    
    // Method untuk refresh semua data
    public void refreshAllData() {
        bbmController.refreshFromDatabase();
        layananController.refreshFromDatabase();
        transaksiController.refreshAllData();
    }
}