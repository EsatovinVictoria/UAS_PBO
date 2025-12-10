package model;

import java.io.Serializable;

public abstract class Produk implements Serializable {
  private int id;
  private String nama;
  private double harga;
  private String satuan; // Liter, Unit, Layanan, dll
  private boolean aktif;

  public Produk(int id, String nama, double harga, String satuan) {
    this.id = id;
    this.nama = nama;
    this.harga = harga;
    this.satuan = satuan;
    this.aktif = true;
  }

  // Getter dan Setter
  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public String getNama() {
    return nama;
  }

  public void setNama(String nama) {
    this.nama = nama;
  }

  public double getHarga() {
    return harga;
  }

  public void setHarga(double harga) {
    this.harga = harga;
  }

  public String getSatuan() {
    return satuan;
  }

  public void setSatuan(String satuan) {
    this.satuan = satuan;
  }

  public boolean isAktif() {
    return aktif;
  }

  public void setAktif(boolean aktif) {
    this.aktif = aktif;
  }

  @Override
  public String toString() {
    return String.format("%s - Rp %.0f/%s", nama, harga, satuan);
  }
}