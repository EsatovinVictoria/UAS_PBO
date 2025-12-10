/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.apple.kuis2;

import javax.swing.*;

/**
 *
 * @author USER
 */
public class MainFrame extends JFrame {
  private JenisBBMController bbmController;
  private LayananController layananController;
  private TransaksiController transaksiController;
  
  public MainFrame() {
    setTitle("SPBU System");
    setSize(1200, 700);
    setLocationRelativeTo(null);
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    // Inisialisasi controller dengan urutan yang benar
    initControllers();
    
    // Membuat tab utama
    JTabbedPane tabs = new JTabbedPane();
    tabs.addTab("Transaksi", new PanelTransaksi(transaksiController, layananController));
    tabs.addTab("Master BBM", new PanelMasterBBM(bbmController));
    tabs.addTab("Master Layanan", new PanelMasterLayanan(layananController));
    tabs.addTab("Report", new PanelReport(transaksiController));

    add(tabs);
    setVisible(true);
  }
  
  private void initControllers() {
    // Inisialisasi controller BBM terlebih dahulu
    this.bbmController = new JenisBBMController();
    
    // Inisialisasi controller layanan
    this.layananController = new LayananController();
    
    // Buat BBMManager yang menggunakan data dari bbmController
    // Pastikan data sudah di-load dari database
    this.bbmController.refreshFromDatabase(); // Refresh data dari DB
    
    // Gunakan data BBM dari controller BBM untuk membuat TransaksiController
    this.transaksiController = new TransaksiController(new BBMManager(bbmController.getSemuaJenisBBM()));
  }
  
  // Getter untuk controller
  public JenisBBMController getBbmController() { return bbmController; }
  public LayananController getLayananController() { return layananController; }
  public TransaksiController getTransaksiController() { return transaksiController; }
}