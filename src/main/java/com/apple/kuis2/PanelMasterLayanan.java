/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.apple.kuis2;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

/**
 *
 * @author USER
 */
public class PanelMasterLayanan extends JPanel {
  private final LayananController controller;
  private JTable table;
  private DefaultTableModel tableModel;
  private JTextField txtNama, txtHarga;
  private JButton btnTambah, btnUpdate, btnHapus, btnRefresh, btnTambahIsiAngin;
  private JTextArea txtInfo;
  
  public PanelMasterLayanan(LayananController controller) {
    this.controller = controller;
    initComponents();
    layoutComponents();
    initEvents();
    loadDataToTable();
  }
  
  private void initComponents() {
    txtNama = new JTextField(20);
    txtHarga = new JTextField(10);
    
    btnTambah = new JButton("Tambah Layanan");
    btnUpdate = new JButton("Update");
    btnHapus = new JButton("Hapus");
    btnRefresh = new JButton("Refresh");
    btnTambahIsiAngin = new JButton("Tambah Isi Angin");
    
    txtInfo = new JTextArea(5, 30);
    txtInfo.setEditable(false);
    
    tableModel = new DefaultTableModel(controller.getTableHeader(), 0) {
      @Override
      public boolean isCellEditable(int row, int column) {
        return false;
      }
    };
    table = new JTable(tableModel);
    table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
  }
  
  private JPanel buildFormPanel() {
    JPanel form = new JPanel(new GridLayout(4, 4, 10, 10));
    form.setBorder(BorderFactory.createTitledBorder("Form Layanan"));
    
    form.add(new JLabel("Nama Layanan:"));
    form.add(txtNama);
    form.add(new JLabel("Harga:"));
    form.add(txtHarga);
    
    form.add(btnTambah);
    form.add(btnUpdate);
    form.add(btnHapus);
    form.add(btnRefresh);
    
    form.add(new JLabel());
    form.add(btnTambahIsiAngin);
    form.add(new JLabel());
    form.add(new JLabel());
    
    return form;
  }
  
  private JPanel buildInfoPanel() {
    JPanel panel = new JPanel(new BorderLayout());
    panel.setBorder(BorderFactory.createTitledBorder("Informasi"));
    panel.add(new JScrollPane(txtInfo), BorderLayout.CENTER);
    return panel;
  }
  
  private void layoutComponents() {
    setLayout(new BorderLayout(10, 10));
    
    add(buildFormPanel(), BorderLayout.NORTH);
    add(new JScrollPane(table), BorderLayout.CENTER);
    add(buildInfoPanel(), BorderLayout.SOUTH);
  }
  
  private void initEvents() {
    btnTambah.addActionListener(e -> tambahLayanan());
    btnUpdate.addActionListener(e -> updateLayanan());
    btnHapus.addActionListener(e -> hapusLayanan());
    btnRefresh.addActionListener(e -> loadDataToTable());
    btnTambahIsiAngin.addActionListener(e -> tambahIsiAngin());
    
    table.getSelectionModel().addListSelectionListener(e -> {
      if (!e.getValueIsAdjusting()) {
        loadSelectedRowToForm();
      }
    });
  }
  
  private void tambahLayanan() {
    try {
      String nama = txtNama.getText().trim();
      if (nama.isEmpty()) {
        JOptionPane.showMessageDialog(this, "Nama layanan harus diisi.");
        return;
      }
      
      double harga = Double.parseDouble(txtHarga.getText().trim());
      if (harga < 0) {
        JOptionPane.showMessageDialog(this, "Harga tidak boleh negatif.");
        return;
      }
      
      // Untuk sekarang, kita asumsikan semua layanan adalah IsiAngin
      // Anda bisa menambahkan pilihan jenis layanan jika diperlukan
      Layanan layanan = new IsiAngin(harga) {
        @Override
        public String getNama() {
          return nama; // Override nama
        }
      };
      
      controller.tambahLayanan(layanan);
      
      txtInfo.append("Layanan berhasil ditambahkan: " + nama + " (Rp " + harga + ")\n");
      loadDataToTable();
      clearForm();
      
    } catch (NumberFormatException ex) {
      JOptionPane.showMessageDialog(this, "Format harga tidak valid.");
    } catch (Exception ex) {
      JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
    }
  }
  
  private void updateLayanan() {
    int selectedRow = table.getSelectedRow();
    if (selectedRow < 0) {
      JOptionPane.showMessageDialog(this, "Pilih baris yang akan diupdate.");
      return;
    }
    
    try {
      String namaLama = tableModel.getValueAt(selectedRow, 1).toString();
      String namaBaru = txtNama.getText().trim();
      double hargaBaru = Double.parseDouble(txtHarga.getText().trim());
      
      if (namaBaru.isEmpty()) {
        JOptionPane.showMessageDialog(this, "Nama layanan harus diisi.");
        return;
      }
      
      // Untuk update, kita hapus yang lama dan tambah yang baru
      controller.hapusLayanan(namaLama);
      
      Layanan layanan = new IsiAngin(hargaBaru) {
        @Override
        public String getNama() {
          return namaBaru;
        }
      };
      
      controller.tambahLayanan(layanan);
      
      txtInfo.append("Layanan berhasil diupdate: " + namaBaru + " (Rp " + hargaBaru + ")\n");
      loadDataToTable();
      clearForm();
      
    } catch (NumberFormatException ex) {
      JOptionPane.showMessageDialog(this, "Format harga tidak valid.");
    } catch (Exception ex) {
      JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
    }
  }
  
private void hapusLayanan() {
    int selectedRow = table.getSelectedRow();
    if (selectedRow < 0) {
        JOptionPane.showMessageDialog(this, "Pilih baris yang akan dihapus.");
        return;
    }
    
    String nama = tableModel.getValueAt(selectedRow, 1).toString();
    int confirm = JOptionPane.showConfirmDialog(this, 
        "Hapus layanan '" + nama + "'?", "Konfirmasi", JOptionPane.YES_NO_OPTION);
    
    if (confirm == JOptionPane.YES_OPTION) {
        try {
            boolean berhasil = controller.hapusLayanan(nama);
            if (berhasil) {
                txtInfo.append("Layanan berhasil dihapus: " + nama + "\n");
                loadDataToTable();
                clearForm();
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Gagal menghapus layanan '" + nama + "'.\n" +
                    "Mungkin layanan ini masih digunakan dalam transaksi atau tidak ditemukan.",
                    "Error", JOptionPane.ERROR_MESSAGE);
                txtInfo.append("Gagal menghapus layanan: " + nama + "\n");
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, 
                "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            txtInfo.append("Error saat menghapus layanan: " + ex.getMessage() + "\n");
        }
    }
}
  
  private void tambahIsiAngin() {
    String input = JOptionPane.showInputDialog(this, "Masukkan harga layanan Isi Angin:", "5000");
    if (input != null && !input.trim().isEmpty()) {
      try {
        double harga = Double.parseDouble(input.trim());
        controller.tambahIsiAngin(harga);
        txtInfo.append("Layanan Isi Angin ditambahkan: Rp " + harga + "\n");
        loadDataToTable();
      } catch (NumberFormatException ex) {
        JOptionPane.showMessageDialog(this, "Format harga tidak valid.");
      }
    }
  }
  
  private void loadDataToTable() {
    tableModel.setRowCount(0);
    Object[][] data = controller.getTableData();
    for (Object[] row : data) {
      tableModel.addRow(row);
    }
    txtInfo.append("Data layanan direfresh (" + data.length + " item)\n");
  }
  
  private void loadSelectedRowToForm() {
    int selectedRow = table.getSelectedRow();
    if (selectedRow >= 0) {
      txtNama.setText(tableModel.getValueAt(selectedRow, 1).toString());
      txtHarga.setText(tableModel.getValueAt(selectedRow, 2).toString().replace("Rp ", "").replace(",", ""));
    }
  }
  
  private void clearForm() {
    txtNama.setText("");
    txtHarga.setText("");
    table.clearSelection();
  }
}