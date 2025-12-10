package model;

public class BBM extends Produk {
  private String jenis; // Pertalite, Pertamax, Solar
  private double oktan; // untuk BBM non-solar

  public BBM(int id, String nama, double harga, String jenis, double oktan) {
    super(id, nama, harga, "Liter");
    this.jenis = jenis;
    this.oktan = oktan;
  }

  public BBM(String nama, double harga, String jenis, double oktan) {
    this(0, nama, harga, jenis, oktan);
  }

  // Getter dan Setter
  public String getJenis() {
    return jenis;
  }

  public void setJenis(String jenis) {
    this.jenis = jenis;
  }

  public double getOktan() {
    return oktan;
  }

  public void setOktan(double oktan) {
    this.oktan = oktan;
  }

  @Override
  public String toString() {
    return String.format("%s (%s) - Rp %.0f/Liter",
        getNama(), jenis, getHarga());
  }
}