/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.apple.kuis2;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.UUID;
import java.util.List;

public class PanelTransaksi extends JPanel {

  private final TransaksiController controller;
  private final LayananController layananController;

  private JComboBox<JenisBBM> cbBBM;
  private JSpinner spinLiter;
  private JLabel lblHarga, lblTotal;
  private JComboBox<PaymentMethod> cbPayment;
  private JTextField txtPlat;
  private JButton btnSimpan, btnReset, btnHapus, btnUpdateHarga, btnExportCsv;
  private JTable table;
  private DefaultTableModel tableModel;
  private JCheckBox cbCetak;
  private JTextArea txtInfo;
  private JCheckBox cbLayanan;
  private JComboBox<Layanan> cbTipeLayanan;

  public PanelTransaksi(TransaksiController controller, LayananController layananController) {
    this.controller = controller;
    this.layananController = layananController;
    initComponents();
    layoutComponents();
    initEvents();
    refreshBBMList();
    refreshLayananList();
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
    JPanel form = new JPanel(new GridLayout(6, 4, 10, 10));
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
    form.add(cbLayanan);
    form.add(cbTipeLayanan);
    form.add(cbCetak);
    form.add(new JLabel()); // pengisi kosong

    // Baris 5
    form.add(btnSimpan);
    form.add(btnReset);
    form.add(new JLabel());
    form.add(new JLabel());

    return form;
  }

  private JPanel buildRightPanel() {
    JPanel right = new JPanel(new GridLayout(6, 1, 6, 6));
    right.add(btnHapus);
    right.add(btnUpdateHarga);
    right.add(btnExportCsv);

    // Tambahkan button untuk refresh dari database
    JButton btnRefresh = new JButton("Refresh Data");
    btnRefresh.addActionListener(e -> {
      refreshBBMList();
      refreshLayananList();
      refreshTableFromController();
      txtInfo.append("Data direfresh dari database\n");
    });
    right.add(btnRefresh);

    JPanel wrapper = new JPanel(new BorderLayout());
    wrapper.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    wrapper.add(right);

    return wrapper;
  }

  private JPanel buildInfoPanel() {
    JPanel south = new JPanel(new BorderLayout());
    south.setBorder(BorderFactory.createTitledBorder("Informasi"));
    south.add(new JScrollPane(txtInfo), BorderLayout.CENTER);
    return south;
  }

  private JLabel label(String text) {
    return new JLabel(text);
  }

  private void layoutComponents() {
    setLayout(new BorderLayout(8, 8));

    add(buildFormPanel(), BorderLayout.NORTH);
    add(new JScrollPane(table), BorderLayout.CENTER);
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
      if (selected == null) {
        JOptionPane.showMessageDialog(this, "Pilih jenis BBM terlebih dahulu.");
        return;
      }
      String nama = selected.getNama();
      String input = JOptionPane.showInputDialog(this, 
          "Masukkan harga baru untuk " + nama + ":", selected.getHargaPerLiter());
      if (input != null && !input.trim().isEmpty()) {
        try {
          double hargaBaru = Double.parseDouble(input);
          if (controller.updateHarga(nama, hargaBaru)) {
            refreshBBMList();
            txtInfo.append("Harga " + nama + " diupdate menjadi Rp " + hargaBaru + "\n");
          } else {
            JOptionPane.showMessageDialog(this, "Gagal mengupdate harga.");
          }
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
    SwingUtilities.invokeLater(() -> {
        cbBBM.removeAllItems();
        List<JenisBBM> semuaBBM = controller.getSemuaJenisBBM();
        System.out.println("Jumlah BBM di dropdown: " + semuaBBM.size()); // Debug
        for (JenisBBM j : semuaBBM) {
            System.out.println("Menambahkan BBM: " + j.getNama()); // Debug
            cbBBM.addItem(j);
        }
        if (cbBBM.getItemCount() > 0) {
            cbBBM.setSelectedIndex(0);
        }
        updateHargaLabel();
        repaint();
    });
}
private void refreshLayananList() {
    SwingUtilities.invokeLater(() -> {
        cbTipeLayanan.removeAllItems();
        List<Layanan> semuaLayanan = layananController.getSemuaLayanan();
        System.out.println("Jumlah Layanan di dropdown: " + semuaLayanan.size()); // Debug
        for (Layanan layanan : semuaLayanan) {
            System.out.println("Menambahkan Layanan: " + layanan.getNama()); // Debug
            cbTipeLayanan.addItem(layanan);
        }
        if (cbTipeLayanan.getItemCount() > 0) {
            cbTipeLayanan.setEnabled(true);
        }
        repaint();
    });
}

  private void updateHargaLabel() {
    JenisBBM j = (JenisBBM) cbBBM.getSelectedItem();
    if (j != null)
      lblHarga.setText(String.format("Rp %,.0f", j.getHargaPerLiter()));
    updateTotalLabel();
  }

  private void updateTotalLabel() {
    JenisBBM j = (JenisBBM) cbBBM.getSelectedItem();
    double liter = ((Number) spinLiter.getValue()).doubleValue();
    if (j == null) {
      lblTotal.setText("Rp 0");
      return;
    }
    
    double total = j.getHargaPerLiter() * liter;
    
    // Tambahkan biaya layanan jika dipilih
    if (cbLayanan.isSelected() && cbTipeLayanan.getSelectedItem() != null) {
      Layanan layanan = (Layanan) cbTipeLayanan.getSelectedItem();
      total += layanan.getHarga();
    }
    
    lblTotal.setText(String.format("Rp %,.0f", total));
  }

  private void saveTransaksiFromForm() {
    JenisBBM j = (JenisBBM) cbBBM.getSelectedItem();
    if (j == null)
      throw new IllegalArgumentException("Jenis BBM belum dipilih.");
      
    double liter = ((Number) spinLiter.getValue()).doubleValue();
    if (liter < 0.5)
      throw new IllegalArgumentException("Liter harus minimal 0.5");
      
    PaymentMethod pm = (PaymentMethod) cbPayment.getSelectedItem();
    String plat = txtPlat.getText().trim();
    
    if (plat.isEmpty())
      throw new IllegalArgumentException("Plat nomor harus diisi.");
    
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
    
    // Reset form setelah simpan
    resetForm();
  }

  private void showStruk(Transaksi t) {
    String struk = """
                   ------ STRUK TRANSAKSI ------
                   ID: """ + t.getId() + "\n"
        + "Waktu: " + t.getFormattedWaktu() + "\n"
        + "Jenis BBM: " + t.getBbm().getNama() + "\n"
        + "Liter: " + t.getLiter() + "\n"
        + "Harga/L: Rp " + String.format("%,.0f", t.getBbm().getHargaPerLiter()) + "\n";
    
    if (t.getLayanan() != null) {
      struk += "Layanan: " + t.getLayanan().getNama() + " (Rp " + 
               String.format("%,.0f", t.getLayanan().getHarga()) + ")\n";
    }
    
    struk += "Total: Rp " + String.format("%,.0f", t.getTotalBayar()) + "\n"
        + "Metode: " + t.getMetodePembayaran() + "\n"
        + "Plat: " + t.getPlatNomor() + "\n"
        + "----------------------------\n";
        
    JTextArea area = new JTextArea(struk);
    area.setEditable(false);
    JOptionPane.showMessageDialog(this, new JScrollPane(area), "Struk", JOptionPane.INFORMATION_MESSAGE);
  }

  private void resetForm() {
    spinLiter.setValue(1.0);
    cbPayment.setSelectedIndex(0);
    txtPlat.setText("");
    cbLayanan.setSelected(false);
    cbTipeLayanan.setEnabled(false);
    cbCetak.setSelected(false);
    updateTotalLabel();
    txtInfo.append("Form direset\n");
  }

  private void refreshTableFromController() {
    tableModel.setRowCount(0);
    for (Transaksi t : controller.getDaftarTransaksi()) {
      tableModel.addRow(t.toTableRow());
    }
  }

  private void exportCsv() throws Exception {
    JFileChooser chooser = new JFileChooser();
    chooser.setSelectedFile(new java.io.File("transaksi_spbu.csv"));
    if (chooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION)
      return;
      
    String path = chooser.getSelectedFile().getAbsolutePath();
    if (!path.toLowerCase().endsWith(".csv")) {
      path += ".csv";
    }
    
    try (PrintWriter pw = new PrintWriter(new FileWriter(path))) {
      String[] headers = controller.getTableHeader();
      pw.println(String.join(",", headers));
      
      for (Transaksi t : controller.getDaftarTransaksi()) {
        Object[] row = t.toTableRow();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < row.length; i++) {
          String s = row[i] == null ? "" : row[i].toString();
          if (s.contains(",") || s.contains("\"") || s.contains("\n")) {
            s = "\"" + s.replace("\"", "\"\"") + "\"";
          }
          sb.append(s);
          if (i < row.length - 1) sb.append(",");
        }
        pw.println(sb.toString());
      }
    }
    
    JOptionPane.showMessageDialog(this, "Export CSV berhasil: " + path);
    txtInfo.append("Data diexport ke: " + path + "\n");
  }
}