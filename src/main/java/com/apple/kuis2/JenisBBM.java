/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.apple.kuis2;

/**
 *
 * @author USER
 */
public class JenisBBM extends Produk {
  private double hargaPerLiter;

  public JenisBBM(String nama, double hargaPerLiter) {
    super(nama);
    setHargaPerLiter(hargaPerLiter);
  }

  public double getHargaPerLiter() { return hargaPerLiter; }
  public final void setHargaPerLiter(double hargaPerLiter) {
    if (hargaPerLiter < 0) throw new IllegalArgumentException("Harga negatif");
    this.hargaPerLiter = hargaPerLiter;
  }

  @Override
  public String toString() {
    return String.format("%s (Rp %.0f/L)", getNama(), hargaPerLiter);
  }

  @Override
  public String infoSingkat() {
    return toString();
  }
}