package service;

import dao.TransaksiDAO;
import model.Transaksi;
import model.TransaksiDetail;
import model.BBM;
import java.time.LocalDateTime;
import java.util.List;

public class TransaksiService {
  private final TransaksiDAO transaksiDAO;

  public TransaksiService() {
    this.transaksiDAO = new TransaksiDAO();
  }

  public boolean simpanTransaksi(Transaksi transaksi) {
    try {
      int id = transaksiDAO.saveTransaksi(transaksi);
      return id > 0;
    } catch (Exception e) {
      System.err.println("Error menyimpan transaksi di service: " + e.getMessage());
      e.printStackTrace();
      return false;
    }
  }

  public List<Transaksi> getAllTransaksi() {
    return transaksiDAO.getAllTransaksi();
  }

  public List<Transaksi> getAllTransaksiWithDetails() {
    return transaksiDAO.getAllTransaksiWithDetails();
  }

  public Transaksi getTransaksiById(int id) {
    return transaksiDAO.getTransaksiById(id);
  }

  public List<Transaksi> getTransaksiByDate(LocalDateTime start, LocalDateTime end) {
    return transaksiDAO.getTransaksiByDate(start, end);
  }

  public List<Transaksi> getTransaksiByUser(int userId) {
    return transaksiDAO.getTransaksiByUser(userId);
  }

  public double getTotalPendapatan(LocalDateTime start, LocalDateTime end) {
    return transaksiDAO.getTotalPendapatan(start, end);
  }

  public int getJumlahTransaksi(LocalDateTime start, LocalDateTime end) {
    return transaksiDAO.getJumlahTransaksi(start, end);
  }

  // Method khusus untuk dashboard
  public double getPendapatanHariIni() {
    try {
      LocalDateTime start = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);
      LocalDateTime end = LocalDateTime.now().withHour(23).withMinute(59).withSecond(59);
      return transaksiDAO.getTotalPendapatan(start, end);
    } catch (Exception e) {
      System.err.println("Error mendapatkan pendapatan hari ini: " + e.getMessage());
      return 0.0;
    }
  }

  public double getPendapatanBulanIni() {
    try {
      LocalDateTime now = LocalDateTime.now();
      LocalDateTime start = now.withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
      LocalDateTime end = now.withDayOfMonth(now.toLocalDate().lengthOfMonth())
          .withHour(23).withMinute(59).withSecond(59);
      return transaksiDAO.getTotalPendapatan(start, end);
    } catch (Exception e) {
      System.err.println("Error mendapatkan pendapatan bulan ini: " + e.getMessage());
      return 0.0;
    }
  }

  public int getJumlahTransaksiHariIni() {
    try {
      LocalDateTime start = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);
      LocalDateTime end = LocalDateTime.now().withHour(23).withMinute(59).withSecond(59);
      return transaksiDAO.getJumlahTransaksi(start, end);
    } catch (Exception e) {
      System.err.println("Error mendapatkan jumlah transaksi hari ini: " + e.getMessage());
      return 0;
    }
  }

  public int getJumlahTransaksiBulanIni() {
    try {
      LocalDateTime now = LocalDateTime.now();
      LocalDateTime start = now.withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
      LocalDateTime end = now.withDayOfMonth(now.toLocalDate().lengthOfMonth())
          .withHour(23).withMinute(59).withSecond(59);
      return transaksiDAO.getJumlahTransaksi(start, end);
    } catch (Exception e) {
      System.err.println("Error mendapatkan jumlah transaksi bulan ini: " + e.getMessage());
      return 0;
    }
  }

  public List<Object[]> getProdukTerlaris(int limit) {
    return transaksiDAO.getProdukTerlaris(limit);
  }

  // Method untuk mendapatkan transaksi terbaru untuk dashboard
  public Object[][] getTransaksiTerbaru(int limit) {
    try {
      List<Transaksi> transaksiList = getAllTransaksi();
      int size = Math.min(transaksiList.size(), limit);
      Object[][] data = new Object[size][5];

      for (int i = 0; i < size; i++) {
        Transaksi t = transaksiList.get(i);
        data[i][0] = t.getNomorTransaksi();
        data[i][1] = t.getFormattedTanggal();
        data[i][2] = t.getPlatNomor();
        data[i][3] = String.format("Rp %,.0f", t.getTotal());
        data[i][4] = t.getMetodePembayaran() != null
            ? t.getMetodePembayaran().toString()
            : "CASH";
      }

      return data;
    } catch (Exception e) {
      System.err.println("Error mendapatkan transaksi terbaru: " + e.getMessage());
      return new Object[0][0];
    }
  }

  // Method baru: Hapus transaksi
  public boolean hapusTransaksi(int id) {
    return transaksiDAO.deleteTransaksi(id);
  }

  // Method baru: Validasi transaksi
  public boolean isValidTransaksi(Transaksi transaksi) {
    if (transaksi == null) {
      return false;
    }

    // Validasi plat nomor
    if (transaksi.getPlatNomor() == null || transaksi.getPlatNomor().trim().isEmpty()) {
      return false;
    }

    // Validasi total
    if (transaksi.getTotal() <= 0) {
      return false;
    }

    // Validasi user ID
    if (transaksi.getUserId() <= 0) {
      return false;
    }

    // Validasi detail transaksi
    if (transaksi.getDetails() == null || transaksi.getDetails().isEmpty()) {
      return false;
    }

    return true;
  }

  // Method baru: Generate ringkasan transaksi
  public String generateRingkasanTransaksi(int id) {
    Transaksi transaksi = getTransaksiById(id);
    if (transaksi == null) {
      return "Transaksi tidak ditemukan";
    }

    return transaksi.getRingkasan();
  }

  // Method baru: Pendapatan kasir hari ini
  public double getPendapatanKasirHariIni(int userId) {
    try {
      LocalDateTime start = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);
      LocalDateTime end = LocalDateTime.now().withHour(23).withMinute(59).withSecond(59);

      List<Transaksi> transaksiList = transaksiDAO.getTransaksiByUser(userId);
      double total = 0;

      for (Transaksi t : transaksiList) {
        if (t.getTanggalWaktu().isAfter(start) && t.getTanggalWaktu().isBefore(end)) {
          total += t.getTotal();
        }
      }

      return total;
    } catch (Exception e) {
      System.err.println("Error mendapatkan pendapatan kasir hari ini: " + e.getMessage());
      return 0.0;
    }
  }

  // Method baru: Jumlah transaksi kasir hari ini
  public int getJumlahTransaksiKasirHariIni(int userId) {
    try {
      LocalDateTime start = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);
      LocalDateTime end = LocalDateTime.now().withHour(23).withMinute(59).withSecond(59);

      List<Transaksi> transaksiList = transaksiDAO.getTransaksiByUser(userId);
      int count = 0;

      for (Transaksi t : transaksiList) {
        if (t.getTanggalWaktu().isAfter(start) && t.getTanggalWaktu().isBefore(end)) {
          count++;
        }
      }

      return count;
    } catch (Exception e) {
      System.err.println("Error mendapatkan jumlah transaksi kasir hari ini: " + e.getMessage());
      return 0;
    }
  }
}