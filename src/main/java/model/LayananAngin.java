package model;

public interface LayananAngin {
  // Konstanta untuk layanan angin
  String NAMA = "Layanan Angin Ban";
  String DESKRIPSI = "Pengisian angin untuk ban kendaraan";
  String SATUAN = "Layanan";

  // Method yang harus diimplementasikan
  double getHarga();

  String getInfoTekanan(); // informasi tekanan yang diberikan

  boolean isGratis(); // apakah gratis dengan pembelian BBM minimal tertentu
}