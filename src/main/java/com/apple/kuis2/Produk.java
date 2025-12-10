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

public abstract class Produk implements Serializable {
  private String nama;

  public Produk(String nama) {
    setNama(nama);
  }

  public String getNama() { return nama; }
  public final void setNama(String nama) {
    if (nama == null || nama.trim().isEmpty()) throw new IllegalArgumentException("Nama kosong");
    this.nama = nama.trim();
  }

  public abstract String infoSingkat();
}