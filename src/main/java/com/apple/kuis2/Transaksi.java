/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.apple.kuis2;

/**
 *
 * @author USER
 */

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public class Transaksi implements Serializable {
  private final UUID id;
  private final JenisBBM bbm;
  private final double liter;
  private final double totalBayar;
  private final PaymentMethod metodePembayaran;
  private final String platNomor;
  private final LocalDateTime waktuTransaksi;
  private final Layanan layanan; // bisa null

  public Transaksi(JenisBBM bbm, double liter, PaymentMethod metodePembayaran, String platNomor) {
    this(bbm, liter, metodePembayaran, platNomor, null);
  }

  public Transaksi(JenisBBM bbm, double liter, PaymentMethod metodePembayaran, String platNomor, Layanan layanan) {
    this.id = UUID.randomUUID();
    this.bbm = bbm;
    this.liter = liter;
    double biayaBBM = liter * bbm.getHargaPerLiter();
    double biayaLayanan = layanan == null ? 0.0 : layanan.getHarga();
    this.totalBayar = biayaBBM + biayaLayanan;
    this.metodePembayaran = metodePembayaran;
    this.platNomor = platNomor;
    this.waktuTransaksi = LocalDateTime.now();
    this.layanan = layanan;
  }

  public UUID getId() {
    return id;
  }

  public JenisBBM getBbm() {
    return bbm;
  }

  public double getLiter() {
    return liter;
  }

  public double getTotalBayar() {
    return totalBayar;
  }

  public PaymentMethod getMetodePembayaran() {
    return metodePembayaran;
  }

  public String getPlatNomor() {
    return platNomor;
  }

  public LocalDateTime getWaktuTransaksi() {
    return waktuTransaksi;
  }

  public String getFormattedWaktu() {
    DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    return waktuTransaksi.format(fmt);
  }

  public Layanan getLayanan() { return layanan; }

  public Object[] toTableRow() {
    return new Object[] {
        id.toString(),
        getFormattedWaktu(),
        bbm.getNama(),
        String.format("%.2f", liter),
        String.format("%.2f", bbm.getHargaPerLiter()),
        String.format("%.2f", totalBayar),
        metodePembayaran.toString(),
        platNomor,
        layanan == null ? "" : layanan.getNama()
    };
  }
}
