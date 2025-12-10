package model;

public class TransaksiDetail {
  private int id;
  private int transaksiId;
  private Produk produk;
  private double kuantitas;
  private double hargaSatuan;
  private double subtotal;

  // Tambahan field khusus untuk layanan angin
  private boolean includeLayananAngin;
  private double hargaLayananAngin;
  private int tekananAngin; // dalam PSI
  private boolean gratisLayananAngin;

  // Constructor lengkap
  public TransaksiDetail(int id, int transaksiId, Produk produk,
      double kuantitas, double hargaSatuan,
      boolean includeLayananAngin, double hargaLayananAngin,
      int tekananAngin, boolean gratisLayananAngin) {
    this.id = id;
    this.transaksiId = transaksiId;
    this.produk = produk;
    this.kuantitas = kuantitas;
    this.hargaSatuan = hargaSatuan;
    this.includeLayananAngin = includeLayananAngin;
    this.hargaLayananAngin = hargaLayananAngin;
    this.tekananAngin = tekananAngin;
    this.gratisLayananAngin = gratisLayananAngin;
    this.subtotal = calculateSubtotal();
  }

  // Constructor tanpa layanan angin
  public TransaksiDetail(int id, int transaksiId, Produk produk,
      double kuantitas, double hargaSatuan) {
    this(id, transaksiId, produk, kuantitas, hargaSatuan,
        false, 0, 30, false);
  }

  // Getter dan Setter
  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public int getTransaksiId() {
    return transaksiId;
  }

  public void setTransaksiId(int transaksiId) {
    this.transaksiId = transaksiId;
  }

  public Produk getProduk() {
    return produk;
  }

  public void setProduk(Produk produk) {
    this.produk = produk;
    this.subtotal = calculateSubtotal();
  }

  public double getKuantitas() {
    return kuantitas;
  }

  public void setKuantitas(double kuantitas) {
    this.kuantitas = kuantitas;
    this.subtotal = calculateSubtotal();
  }

  public double getHargaSatuan() {
    return hargaSatuan;
  }

  public void setHargaSatuan(double hargaSatuan) {
    this.hargaSatuan = hargaSatuan;
    this.subtotal = calculateSubtotal();
  }

  public double getSubtotal() {
    return subtotal;
  }

  public boolean isIncludeLayananAngin() {
    return includeLayananAngin;
  }

  public void setIncludeLayananAngin(boolean includeLayananAngin) {
    this.includeLayananAngin = includeLayananAngin;
    this.subtotal = calculateSubtotal();
  }

  public double getHargaLayananAngin() {
    return hargaLayananAngin;
  }

  public void setHargaLayananAngin(double hargaLayananAngin) {
    this.hargaLayananAngin = hargaLayananAngin;
    this.subtotal = calculateSubtotal();
  }

  public int getTekananAngin() {
    return tekananAngin;
  }

  public void setTekananAngin(int tekananAngin) {
    this.tekananAngin = tekananAngin;
  }

  public boolean isGratisLayananAngin() {
    return gratisLayananAngin;
  }

  public void setGratisLayananAngin(boolean gratisLayananAngin) {
    this.gratisLayananAngin = gratisLayananAngin;
    this.subtotal = calculateSubtotal();
  }

  // Method untuk menghitung subtotal
  private double calculateSubtotal() {
    double subtotalBBM = kuantitas * hargaSatuan;
    double subtotalLayanan = 0;

    if (includeLayananAngin && !gratisLayananAngin) {
      subtotalLayanan = hargaLayananAngin;
    }

    return subtotalBBM + subtotalLayanan;
  }

  // Method untuk mendapatkan subtotal BBM saja
  public double getSubtotalBBM() {
    return kuantitas * hargaSatuan;
  }

  // Method untuk mendapatkan subtotal layanan angin saja
  public double getSubtotalLayananAngin() {
    if (includeLayananAngin && !gratisLayananAngin) {
      return hargaLayananAngin;
    }
    return 0;
  }

  // Method utilitas
  public String getSatuan() {
    return produk != null ? produk.getSatuan() : "Liter";
  }

  public boolean isBBM() {
    return produk instanceof BBM;
  }

  @Override
  public String toString() {
    String result = String.format("%s: %.2f %s @Rp %.0f = Rp %.0f",
        produk.getNama(), kuantitas, getSatuan(), hargaSatuan, getSubtotalBBM());

    if (includeLayananAngin) {
      result += String.format(" + Layanan Angin: %s",
          gratisLayananAngin ? "GRATIS" : String.format("Rp %.0f", hargaLayananAngin));
    }

    return result;
  }
}