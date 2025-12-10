/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.apple.kuis2;

/**
 *
 * @author USER
 */

public class IsiAngin implements Layanan {
  private final String nama = "Isi Angin (Pompa Ban)";
  private final double harga; // mis: Rp 5000 per layanan

  public IsiAngin(double harga) {
    if (harga < 0) throw new IllegalArgumentException("Harga tidak boleh negatif");
    this.harga = harga;
  }

  @Override
  public String getNama() {
    return nama;
  }

  @Override
  public double getHarga() {
    return harga;
  }

  @Override
  public String toString() {
    return String.format("%s (Rp %.0f)", nama, harga);
  }
}