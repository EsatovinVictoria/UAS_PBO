package model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class Transaksi {
  private int id;
  private String nomorTransaksi;
  private LocalDateTime tanggalWaktu;
  private String platNomor;
  private double total;
  private PaymentMethod metodePembayaran;
  private int userId;
  private List<TransaksiDetail> details;

  // Tarif layanan angin default
  private static double TARIF_ANGIN_DEFAULT = 5000;

  // Constructor untuk membuat transaksi baru
  public Transaksi(String platNomor, PaymentMethod metodePembayaran, int userId) {
    this.nomorTransaksi = generateNomorTransaksi();
    this.tanggalWaktu = LocalDateTime.now();
    this.platNomor = platNomor;
    this.metodePembayaran = metodePembayaran;
    this.userId = userId;
    this.total = 0;
    this.details = new ArrayList<>();
  }

  // Constructor untuk load dari database
  public Transaksi(int id, String nomorTransaksi, LocalDateTime tanggalWaktu,
      String platNomor, double total, PaymentMethod metodePembayaran,
      int userId) {
    this.id = id;
    this.nomorTransaksi = nomorTransaksi;
    this.tanggalWaktu = tanggalWaktu;
    this.platNomor = platNomor;
    this.total = total;
    this.metodePembayaran = metodePembayaran;
    this.userId = userId;
    this.details = new ArrayList<>();
  }

  // Overload constructor untuk string payment method
  public Transaksi(int id, String nomorTransaksi, LocalDateTime tanggalWaktu,
      String platNomor, double total, String metodePembayaranStr,
      int userId) {
    this(id, nomorTransaksi, tanggalWaktu, platNomor, total,
        PaymentMethod.fromString(metodePembayaranStr), userId);
  }

  private String generateNomorTransaksi() {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
    return "TRX-" + LocalDateTime.now().format(formatter);
  }

  // Getter dan Setter
  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public String getNomorTransaksi() {
    return nomorTransaksi;
  }

  public LocalDateTime getTanggalWaktu() {
    return tanggalWaktu;
  }

  public String getPlatNomor() {
    return platNomor;
  }

  public void setPlatNomor(String platNomor) {
    this.platNomor = platNomor;
  }

  public double getTotal() {
    return total;
  }

  public void setTotal(double total) {
    this.total = total;
  }

  public PaymentMethod getMetodePembayaran() {
    return metodePembayaran;
  }

  public void setMetodePembayaran(PaymentMethod metodePembayaran) {
    this.metodePembayaran = metodePembayaran;
  }

  public int getUserId() {
    return userId;
  }

  public void setUserId(int userId) {
    this.userId = userId;
  }

  public List<TransaksiDetail> getDetails() {
    return details;
  }

  // Tambahkan method setDetails yang hilang
  public void setDetails(List<TransaksiDetail> details) {
    if (details != null) {
      this.details = new ArrayList<>(details);
      calculateTotal();
    } else {
      this.details = new ArrayList<>();
    }
  }

  // Method untuk menambahkan item BBM dengan/tanpa layanan angin
  public void addBBM(BBM bbm, double liter, boolean includeLayananAngin,
      int tekananAngin, boolean gratisLayananAngin) {

    double hargaAngin = gratisLayananAngin ? 0 : TARIF_ANGIN_DEFAULT;

    TransaksiDetail detail = new TransaksiDetail(
        0, id, bbm, liter, bbm.getHarga(),
        includeLayananAngin, hargaAngin, tekananAngin, gratisLayananAngin);

    details.add(detail);
    calculateTotal();
  }

  public void addBBM(BBM bbm, double liter) {
    addBBM(bbm, liter, false, 30, false);
  }

  // Method untuk menambahkan detail secara langsung
  public void addDetail(TransaksiDetail detail) {
    if (detail != null) {
      details.add(detail);
      calculateTotal();
    }
  }

  // Method untuk menghapus detail
  public void removeDetail(int index) {
    if (index >= 0 && index < details.size()) {
      details.remove(index);
      calculateTotal();
    }
  }

  public void clearDetails() {
    details.clear();
    total = 0;
  }

  private void calculateTotal() {
    total = 0;
    for (TransaksiDetail detail : details) {
      total += detail.getSubtotal();
    }
  }

  // Hitung total BBM dan layanan angin secara terpisah
  public double getTotalBBM() {
    double totalBBM = 0;
    for (TransaksiDetail detail : details) {
      totalBBM += detail.getSubtotalBBM();
    }
    return totalBBM;
  }

  public double getTotalLayananAngin() {
    double totalAngin = 0;
    for (TransaksiDetail detail : details) {
      totalAngin += detail.getSubtotalLayananAngin();
    }
    return totalAngin;
  }

  public int getJumlahLayananAngin() {
    int count = 0;
    for (TransaksiDetail detail : details) {
      if (detail.isIncludeLayananAngin()) {
        count++;
      }
    }
    return count;
  }

  // Method utilitas
  public String getFormattedTanggal() {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
    return tanggalWaktu.format(formatter);
  }

  public String getFormattedDateOnly() {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    return tanggalWaktu.format(formatter);
  }

  public String getFormattedTimeOnly() {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
    return tanggalWaktu.format(formatter);
  }

  public Object[] toTableRow() {
    return new Object[] {
        id,
        nomorTransaksi,
        getFormattedDateOnly(),
        getFormattedTimeOnly(),
        platNomor,
        String.format("Rp %.0f", total),
        metodePembayaran != null ? metodePembayaran.toString() : "CASH",
        userId
    };
  }

  public String getRingkasan() {
    StringBuilder sb = new StringBuilder();
    sb.append("=== TRANSAKSI ===\n");
    sb.append("No. Transaksi: ").append(nomorTransaksi).append("\n");
    sb.append("Tanggal/Waktu: ").append(getFormattedTanggal()).append("\n");
    sb.append("Plat Nomor: ").append(platNomor).append("\n");
    sb.append("Kasir ID: ").append(userId).append("\n\n");
    sb.append("=== DETAIL PEMBELIAN ===\n");

    for (TransaksiDetail detail : details) {
      sb.append(detail.toString()).append("\n");
    }

    sb.append("\n=== RINCIAN TOTAL ===\n");
    sb.append("Total BBM: Rp ").append(String.format("%.0f", getTotalBBM())).append("\n");
    if (getJumlahLayananAngin() > 0) {
      sb.append("Layanan Angin: Rp ").append(String.format("%.0f", getTotalLayananAngin())).append("\n");
    }
    sb.append("------------------------------------\n");
    sb.append("Grand Total: Rp ").append(String.format("%.0f", total)).append("\n");
    sb.append("Metode Bayar: ").append(metodePembayaran != null ? metodePembayaran.toString() : "CASH").append("\n");

    return sb.toString();
  }

  // Method untuk validasi
  public boolean isValid() {
    if (platNomor == null || platNomor.trim().isEmpty()) {
      return false;
    }

    if (metodePembayaran == null) {
      return false;
    }

    if (userId <= 0) {
      return false;
    }

    if (details == null || details.isEmpty()) {
      return false;
    }

    return true;
  }

  // Static method untuk tarif angin
  public static double getTarifAnginDefault() {
    return TARIF_ANGIN_DEFAULT;
  }

  public static void setTarifAnginDefault(double tarif) {
    TARIF_ANGIN_DEFAULT = tarif;
  }

  @Override
  public String toString() {
    return String.format("Transaksi[%s] - %s - Rp %.0f",
        nomorTransaksi, getFormattedTanggal(), total);
  }
}