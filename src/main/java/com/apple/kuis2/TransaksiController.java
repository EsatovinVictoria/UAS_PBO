/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.apple.kuis2;

import java.util.*;

public class TransaksiController {
  private BBMManager hargaBBM;
  private final List<Transaksi> daftarTransaksi;
  private final TransaksiDAO transaksiDAO;
  private final JenisBBMDAO jenisBBMDAO;
  private final LayananDAO layananDAO; // Tambahkan ini

  public TransaksiController(BBMManager hargaBBM) {
    this.hargaBBM = hargaBBM;
    this.daftarTransaksi = new ArrayList<>();
    this.transaksiDAO = new TransaksiDAO();
    this.jenisBBMDAO = new JenisBBMDAO();
    this.layananDAO = new LayananDAO(); // Inisialisasi
    refreshBBMFromDatabase();
  }

  // Refresh BBM data dari database
  private void refreshBBMFromDatabase() {
    try {
      List<JenisBBM> dariDB = jenisBBMDAO.findAll();
      System.out.println("Memuat " + dariDB.size() + " BBM dari database"); // Debug
      if (!dariDB.isEmpty()) {
        hargaBBM.setDaftar(dariDB);
      }
    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }

  // Tambahkan method untuk refresh data dari database
  public void refreshAllData() {
    refreshBBMFromDatabase();
  }
  
  // Ganti method getSemuaJenisBBM() untuk selalu mengambil data terbaru
  public List<JenisBBM> getSemuaJenisBBM() {
    refreshBBMFromDatabase(); // Selalu refresh sebelum mengambil data
    return hargaBBM.getDaftar();
  }

  public Transaksi buatTransaksi(String namaBBM, double liter, PaymentMethod metode, String platNomor) {
    Optional<JenisBBM> opt = hargaBBM.findByName(namaBBM);
    if (opt.isEmpty()) throw new IllegalArgumentException("Jenis BBM tidak ditemukan");
    return buatTransaksi(opt.get(), liter, metode, platNomor, null);
  }

  public Transaksi buatTransaksi(JenisBBM bbm, double liter, PaymentMethod metode, String platNomor) {
    return buatTransaksi(bbm, liter, metode, platNomor, null);
  }

  public Transaksi buatTransaksi(String namaBBM, double liter, PaymentMethod metode, String platNomor, Layanan layanan) {
    Optional<JenisBBM> opt = hargaBBM.findByName(namaBBM);
    if (opt.isEmpty()) throw new IllegalArgumentException("Jenis BBM tidak ditemukan");
    return buatTransaksi(opt.get(), liter, metode, platNomor, layanan);
  }

  public Transaksi buatTransaksi(JenisBBM bbm, double liter, PaymentMethod metode, String platNomor, Layanan layanan) {
    if (liter < 0.5) throw new IllegalArgumentException("Liter harus > 0.5");
    if (platNomor == null || platNomor.trim().isEmpty()) throw new IllegalArgumentException("Plat nomor harus diisi.");
    
    Transaksi trx = new Transaksi(bbm, liter, metode, platNomor, layanan);
    daftarTransaksi.add(trx);
    
    try { 
      transaksiDAO.save(trx); 
      // Update harga BBM di database jika ada perubahan
      jenisBBMDAO.save(bbm);
    } catch (Exception ex) { 
      ex.printStackTrace(); 
    }
    return trx;
  }

  public String[] getTableHeader() {
    return new String[] { "ID", "Waktu", "Jenis BBM", "Liter", "Harga / L", "Total Bayar", "Metode", "Plat Nomor", "Layanan" };
  }

  public List<Transaksi> getDaftarTransaksi() {
    try {
      List<Transaksi> fromDb = transaksiDAO.findAll();
      if (!fromDb.isEmpty()) {
        daftarTransaksi.clear();
        daftarTransaksi.addAll(fromDb);
      }
    } catch (Exception ex) {
      ex.printStackTrace();
    }
    return daftarTransaksi;
  }
  
  public boolean updateHarga(String nama, double hargaBaru) {
    boolean success = hargaBBM.updateHarga(nama, hargaBaru);
    if (success) {
      try {
        // Update juga di database
        Optional<JenisBBM> opt = hargaBBM.findByName(nama);
        if (opt.isPresent()) {
          jenisBBMDAO.save(opt.get());
        }
      } catch (Exception ex) {
        ex.printStackTrace();
      }
    }
    return success;
  }

  public boolean hapusTransaksiById(UUID id) {
    return daftarTransaksi.removeIf(t -> t.getId().equals(id));
  }

  public void clearAllTransaksi() {
    daftarTransaksi.clear();
  }
  
  // Method untuk refresh BBM dari database
  public void refreshBBMData() {
    refreshBBMFromDatabase();
  }
}