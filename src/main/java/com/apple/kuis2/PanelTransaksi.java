/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.apple.kuis2;

/**
 *
 * @author USER
 */
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.UUID;

public class PanelTransaksi extends JPanel {

  private final TransaksiController controller;

  private JComboBox<JenisBBM> cbBBM; // Komponen untuk menampilkan daftar jenis BBM
  private JSpinner spinLiter; // Komponen untuk input jumlah liter
  private JLabel lblHarga, lblTotal; // Komponen untuk menampilkan harga dan total
  private JComboBox<PaymentMethod> cbPayment; // Komponen untuk memilih metode pembayaran
  private JTextField txtPlat; // Komponen untuk input plat nomor
  private JButton btnSimpan, btnReset, btnHapus, btnUpdateHarga, btnExportCsv; // Komponen button
  private JTable table; // Komponen untuk menampilkan daftar transaksi
  private DefaultTableModel tableModel; // Model tabel (untuk atur row dan column)
  private JCheckBox cbCetak; // Komponen untuk cetak struk
  private JTextArea txtInfo; // Komponen untuk menampilkan informasi
  private JCheckBox cbLayanan;
  private JComboBox<Layanan> cbTipeLayanan;

  public PanelTransaksi(TransaksiController controller) {
    this.controller = controller;
    initComponents();
    layoutComponents();
    initEvents();
    refreshBBMList();
  }

  private void initComponents() {
    cbBBM = new JComboBox<>();
    spinLiter = new JSpinner(new SpinnerNumberModel(1.0, 0.1, 500.0, 0.1));
    lblHarga = new JLabel("Rp 0");
    lblTotal = new JLabel("Rp 0");
    cbPayment = new JComboBox<>(PaymentMethod.values());
    txtPlat = new JTextField();

    btnSimpan = new JButton("Simpan Transaksi");
    btnReset = new JButton("Reset");
    btnHapus = new JButton("Hapus Baris Terpilih");
    btnUpdateHarga = new JButton("Update Harga BBM");
    btnExportCsv = new JButton("Export CSV");
    cbCetak = new JCheckBox("Cetak struk otomatis");
    
    cbLayanan = new JCheckBox("Tambah Layanan");
    cbTipeLayanan = new JComboBox<>();
    cbTipeLayanan.addItem(new IsiAngin(5000)); // contoh
    cbTipeLayanan.setEnabled(false);

    tableModel = new DefaultTableModel(controller.getTableHeader(), 0) {
      @Override
      public boolean isCellEditable(int row, int column) {
        return false;
      }
    };
    table = new JTable(tableModel);
    table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

    txtInfo = new JTextArea(5, 30);
    txtInfo.setEditable(false);
  }

  private JPanel buildFormPanel() {
    JPanel form = new JPanel(new GridLayout(4, 4, 10, 10));
    form.setBorder(BorderFactory.createTitledBorder("Form Transaksi"));

    // Baris 1
    form.add(label("Jenis BBM:"));
    form.add(cbBBM);
    form.add(label("Harga / L:"));
    form.add(lblHarga);

    // Baris 2
    form.add(label("Liter:"));
    form.add(spinLiter);
    form.add(label("Total:"));
    form.add(lblTotal);

    // Baris 3
    form.add(label("Metode Pembayaran:"));
    form.add(cbPayment);
    form.add(label("Plat Nomor:"));
    form.add(txtPlat);

    // Baris 4
    form.add(btnSimpan);
    form.add(btnReset);
    form.add(cbCetak);
    form.add(new JLabel()); // pengisi kosong agar layout rapi
    
    form.add(label("Layanan:"));
    form.add(cbLayanan);
    form.add(label("Tipe Layanan:"));
    form.add(cbTipeLayanan);

    return form;
  }

  private JPanel buildRightPanel() {
    JPanel right = new JPanel(new GridLayout(6, 1, 6, 6));
    right.add(btnHapus);
    right.add(btnUpdateHarga);
    right.add(btnExportCsv);

    JPanel wrapper = new JPanel(new BorderLayout());
    wrapper.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    wrapper.add(right);

    return wrapper;
  }

  private JPanel buildInfoPanel() {
    JPanel south = new JPanel(new BorderLayout());
    south.setBorder(BorderFactory.createTitledBorder("Informasi"));
    south.add(new JScrollPane(txtInfo), BorderLayout.CENTER);
    // JScrollPane Komponen untuk membungkus komponen agar bisa di scroll
    return south;
  }

  private JLabel label(String text) {
    return new JLabel(text);
  }

  private void layoutComponents() {
    setLayout(new BorderLayout(8, 8));

    add(buildFormPanel(), BorderLayout.NORTH);
    add(new JScrollPane(table), BorderLayout.CENTER);
    // JScrollPane Komponen untuk membungkus komponen agar bisa di scroll
    add(buildRightPanel(), BorderLayout.EAST);
    add(buildInfoPanel(), BorderLayout.SOUTH);
  }

  private void initEvents() {
    cbBBM.addActionListener(e -> updateHargaLabel());
    spinLiter.addChangeListener(e -> updateTotalLabel());

    btnSimpan.addActionListener(e -> {
      try {
        saveTransaksiFromForm();
      } catch (Exception ex) {
        JOptionPane.showMessageDialog(this, "Gagal simpan: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        // JOptionPane komponen unut menampilkan dialog (pop up)
      }
    });

    btnReset.addActionListener(e -> resetForm());

    btnHapus.addActionListener(e -> {
      int row = table.getSelectedRow();
      if (row < 0) {
        JOptionPane.showMessageDialog(this, "Pilih baris yang ingin dihapus.");
        return;
      }
      String idStr = tableModel.getValueAt(row, 0).toString();
      UUID id = UUID.fromString(idStr);
      controller.hapusTransaksiById(id);
      tableModel.removeRow(row);
      txtInfo.append("Transaksi dihapus: " + idStr + "\n");
    });

    btnUpdateHarga.addActionListener(e -> {
      JenisBBM selected = (JenisBBM) cbBBM.getSelectedItem();
      String nama = selected.getNama();
      String input = JOptionPane.showInputDialog(this, "Masukkan harga baru untuk " + nama + ":",
          selected.getHargaPerLiter());
      if (input != null) {
        try {
          double hargaBaru = Double.parseDouble(input);
          controller.updateHarga(nama, hargaBaru);
          refreshBBMList();
          txtInfo.append("Harga " + nama + " diupdate menjadi Rp " + hargaBaru + "\n");
        } catch (NumberFormatException ex) {
          JOptionPane.showMessageDialog(this, "Harga tidak valid.");
        }
      }
    });

    btnExportCsv.addActionListener(e -> {
      try {
        exportCsv();
      } catch (Exception ex) {
        JOptionPane.showMessageDialog(this, "Gagal export: " + ex.getMessage());
      }
    });
    
    cbLayanan.addActionListener(e -> cbTipeLayanan.setEnabled(cbLayanan.isSelected()));
  }

  private void refreshBBMList() {
    cbBBM.removeAllItems();
    for (JenisBBM j : controller.getSemuaJenisBBM())
      cbBBM.addItem(j);
    updateHargaLabel();
    refreshTableFromController();
  }

  private void updateHargaLabel() {
    JenisBBM j = (JenisBBM) cbBBM.getSelectedItem();
    if (j != null)
      lblHarga.setText(String.format("Rp %.2f", j.getHargaPerLiter()));
    updateTotalLabel();
  }

  private void updateTotalLabel() {
    JenisBBM j = (JenisBBM) cbBBM.getSelectedItem();
    double liter = ((Number) spinLiter.getValue()).doubleValue();
    if (j == null) {
      lblTotal.setText("Rp 0");
      return;
    }
    lblTotal.setText(String.format("Rp %.2f", j.getHargaPerLiter() * liter));
  }

  private void saveTransaksiFromForm() {
    JenisBBM j = (JenisBBM) cbBBM.getSelectedItem();
    if (j == null)
      throw new IllegalArgumentException("Jenis BBM belum dipilih.");
    double liter = ((Number) spinLiter.getValue()).doubleValue();
    PaymentMethod pm = (PaymentMethod) cbPayment.getSelectedItem();
    String plat = txtPlat.getText();
    Layanan layanan = cbLayanan.isSelected() ? (Layanan) cbTipeLayanan.getSelectedItem() : null;
    Transaksi t = controller.buatTransaksi(j.getNama(), liter, pm, plat, layanan);
    tableModel.addRow(t.toTableRow());

    txtInfo.append(
        "Transaksi tersimpan: " + t.getPlatNomor() + " | " + t.getBbm().getNama() + " | Liter: " + t.getLiter()
            + " | Rp "
            + t.getTotalBayar() + "\n");

    if (cbCetak.isSelected()) {
      showStruk(t);
    }
    
  }

  private void showStruk(Transaksi t) {
    String struk = """
                   ------ STRUK TRANSAKSI ------
                   ID: """ + t.getId() + "\n"
        + "Waktu: " + t.getFormattedWaktu() + "\n"
        + "Jenis BBM: " + t.getBbm().getNama() + "\n"
        + "Liter: " + t.getLiter() + "\n"
        + "Harga/L: Rp " + t.getBbm().getHargaPerLiter() + "\n"
        + "Total: Rp " + t.getTotalBayar() + "\n"
        + "Metode: " + t.getMetodePembayaran() + "\n"
        + "Plat: " + t.getPlatNomor() + "\n"
        + "----------------------------\n";
    JTextArea area = new JTextArea(struk);
    area.setEditable(false);
    JOptionPane.showMessageDialog(this, new JScrollPane(area), "Struk", JOptionPane.INFORMATION_MESSAGE);
    // JScrollPane Komponen untuk membungkus komponen agar bisa di scroll
  }

  private void resetForm() {
    spinLiter.setValue(1.0);
    cbPayment.setSelectedIndex(0);
    txtPlat.setText("");
    cbCetak.setSelected(false);
    updateTotalLabel();
    txtInfo.append("Form direset\n");
  }

  private void refreshTableFromController() {
    // clear model
    tableModel.setRowCount(0);
    for (Transaksi t : controller.getDaftarTransaksi()) {
      tableModel.addRow(t.toTableRow());
    }
  }

  private void exportCsv() throws Exception {
    JFileChooser chooser = new JFileChooser(); // JFileChooser Komponen untuk memilih file
    if (chooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION)
      return;
    String path = chooser.getSelectedFile().getAbsolutePath();
    try (PrintWriter pw = new PrintWriter(new FileWriter(path))) {
      // header
      String[] headers = controller.getTableHeader();
      pw.println(String.join(",", headers));
      for (Transaksi t : controller.getDaftarTransaksi()) {
        Object[] row = t.toTableRow();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < row.length; i++) {
          String s = row[i] == null ? "" : row[i].toString();
          // escape simple comma
          if (s.contains(","))
            s = "\"" + s.replace("\"", "\"\"") + "\"";
          sb.append(s);
          if (i < row.length - 1)
            sb.append(",");
        }
        pw.println(sb.toString());
      }
    }
    JOptionPane.showMessageDialog(this, "Export CSV selesai: " + path);
  }
}
