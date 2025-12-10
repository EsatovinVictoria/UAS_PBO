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
public class PanelMasterBBM extends JPanel {
  private final JenisBBMController controller;
  private JTable table;
  private DefaultTableModel tableModel;
  private JTextField txtNama, txtHarga;
  private JButton btnTambah, btnUpdate, btnHapus, btnRefresh;
  private JTextArea txtInfo;
  
  public PanelMasterBBM(JenisBBMController controller) {
    this.controller = controller;
    initComponents();
    layoutComponents();
    initEvents();
    loadDataToTable();
  }
  
  private void initComponents() {
    txtNama = new JTextField(20);
    txtHarga = new JTextField(10);
    
    btnTambah = new JButton("Tambah");
    btnUpdate = new JButton("Update");
    btnHapus = new JButton("Hapus");
    btnRefresh = new JButton("Refresh");
    
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
    JPanel form = new JPanel(new GridLayout(3, 4, 10, 10));
    form.setBorder(BorderFactory.createTitledBorder("Form BBM"));
    
    form.add(new JLabel("Nama BBM:"));
    form.add(txtNama);
    form.add(new JLabel("Harga per Liter:"));
    form.add(txtHarga);
    
    form.add(btnTambah);
    form.add(btnUpdate);
    form.add(btnHapus);
    form.add(btnRefresh);
    
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
    btnTambah.addActionListener(e -> tambahBBM());
    btnUpdate.addActionListener(e -> updateBBM());
    btnHapus.addActionListener(e -> hapusBBM());
    btnRefresh.addActionListener(e -> loadDataToTable());
    
    table.getSelectionModel().addListSelectionListener(e -> {
      if (!e.getValueIsAdjusting()) {
        loadSelectedRowToForm();
      }
    });
  }
  
  private void tambahBBM() {
    try {
      String nama = txtNama.getText().trim();
      if (nama.isEmpty()) {
        JOptionPane.showMessageDialog(this, "Nama BBM harus diisi.");
        return;
      }
      
      double harga = Double.parseDouble(txtHarga.getText().trim());
      if (harga <= 0) {
        JOptionPane.showMessageDialog(this, "Harga harus lebih dari 0.");
        return;
      }
      
      JenisBBM bbm = new JenisBBM(nama, harga);
      controller.tambahJenisBBM(bbm);
      
      txtInfo.append("BBM berhasil ditambahkan: " + nama + " (Rp " + harga + "/L)\n");
      loadDataToTable();
      clearForm();
      
    } catch (NumberFormatException ex) {
      JOptionPane.showMessageDialog(this, "Format harga tidak valid.");
    } catch (Exception ex) {
      JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
    }
  }
  
  private void updateBBM() {
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
        JOptionPane.showMessageDialog(this, "Nama BBM harus diisi.");
        return;
      }
      
      // Jika nama berubah, hapus yang lama, tambah yang baru
      if (!namaLama.equals(namaBaru)) {
        controller.hapusJenisBBM(namaLama);
        JenisBBM bbmBaru = new JenisBBM(namaBaru, hargaBaru);
        controller.tambahJenisBBM(bbmBaru);
      } else {
        // Jika nama sama, cukup update harga
        controller.updateHarga(namaBaru, hargaBaru);
      }
      
      txtInfo.append("BBM berhasil diupdate: " + namaBaru + " (Rp " + hargaBaru + "/L)\n");
      loadDataToTable();
      clearForm();
      
    } catch (NumberFormatException ex) {
      JOptionPane.showMessageDialog(this, "Format harga tidak valid.");
    } catch (Exception ex) {
      JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
    }
  }
  
    private void hapusBBM() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Pilih baris yang akan dihapus.");
            return;
        }
        
        String nama = tableModel.getValueAt(selectedRow, 1).toString();
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Hapus BBM '" + nama + "'?", "Konfirmasi", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                boolean berhasil = controller.hapusJenisBBM(nama);
                if (berhasil) {
                    txtInfo.append("BBM berhasil dihapus: " + nama + "\n");
                    loadDataToTable();
                    clearForm();
                } else {
                    JOptionPane.showMessageDialog(this, 
                        "Gagal menghapus BBM '" + nama + "'.\n" +
                        "Mungkin BBM ini masih digunakan dalam transaksi atau tidak ditemukan.",
                        "Error", JOptionPane.ERROR_MESSAGE);
                    txtInfo.append("Gagal menghapus BBM: " + nama + "\n");
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, 
                    "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                txtInfo.append("Error saat menghapus BBM: " + ex.getMessage() + "\n");
            }
        }
    }
  
  private void loadDataToTable() {
    tableModel.setRowCount(0);
    Object[][] data = controller.getTableData();
    for (Object[] row : data) {
      tableModel.addRow(row);
    }
    txtInfo.append("Data BBM direfresh (" + data.length + " item)\n");
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