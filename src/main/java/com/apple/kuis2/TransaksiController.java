/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.apple.kuis2;

/**
 *
 * @author USER
 */
import java.util.*;

public class TransaksiController {
  private final BBMManager hargaBBM;
  private final List<Transaksi> daftarTransaksi;
  private final TransaksiDAO transaksiDAO; // untuk persist

  public TransaksiController(BBMManager hargaBBM) {
    this.hargaBBM = hargaBBM;
    this.daftarTransaksi = new ArrayList<>();
    this.transaksiDAO = new TransaksiDAO(); // config di DAO
  }

  // existing
  public Transaksi buatTransaksi(String namaBBM, double liter, PaymentMethod metode, String platNomor) {
    Optional<JenisBBM> opt = hargaBBM.findByName(namaBBM);
    if (opt.isEmpty()) throw new IllegalArgumentException("Jenis BBM tidak ditemukan");
    return buatTransaksi(opt.get(), liter, metode, platNomor, null);
  }

  // overloaded: terima JenisBBM langsung
  public Transaksi buatTransaksi(JenisBBM bbm, double liter, PaymentMethod metode, String platNomor) {
    return buatTransaksi(bbm, liter, metode, platNomor, null);
  }

  // overloaded: terima layanan
  public Transaksi buatTransaksi(String namaBBM, double liter, PaymentMethod metode, String platNomor, Layanan layanan) {
    Optional<JenisBBM> opt = hargaBBM.findByName(namaBBM);
    if (opt.isEmpty()) throw new IllegalArgumentException("Jenis BBM tidak ditemukan");
    return buatTransaksi(opt.get(), liter, metode, platNomor, layanan);
  }

  public Transaksi buatTransaksi(JenisBBM bbm, double liter, PaymentMethod metode, String platNomor, Layanan layanan) {
    // validasi seperti sebelumnya
    if (liter < 0.5) throw new IllegalArgumentException("Liter harus > 0.5");
    if (platNomor == null || platNomor.trim().isEmpty()) throw new IllegalArgumentException("Plat nomor harus diisi.");
    Transaksi trx = new Transaksi(bbm, liter, metode, platNomor, layanan);
    daftarTransaksi.add(trx);
    // persist ke DB
    try { transaksiDAO.save(trx); } catch (Exception ex) { ex.printStackTrace(); }
    return trx;
  }

  // update header
  public String[] getTableHeader() {
    return new String[] { "ID", "Waktu", "Jenis BBM", "Liter", "Harga / L", "Total Bayar", "Metode", "Plat Nomor", "Layanan" };
  }

  public List<Transaksi> getDaftarTransaksi() {
    // opsi: sinkron dengan DB
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
    return hargaBBM.updateHarga(nama, hargaBaru);
  }

  public boolean hapusTransaksiById(UUID id) {
    return daftarTransaksi.removeIf(t -> t.getId().equals(id));
  }

  public void clearAllTransaksi() {
    daftarTransaksi.clear();
  }
  
  public List<JenisBBM> getSemuaJenisBBM() {
    return hargaBBM.getDaftar();
  }
}
