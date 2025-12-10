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

public class MainFrame extends JFrame {
  public MainFrame() {
    setTitle("SPBU System");
    setSize(1000, 600);
    setLocationRelativeTo(null);
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    BBMManager harga = BBMManager.defaultHarga();
    TransaksiController controller = new TransaksiController(harga);

    JTabbedPane tabs = new JTabbedPane();
    tabs.addTab("Transaksi", new PanelTransaksi(controller));
    tabs.addTab("Report", new PanelReport(controller));

    add(tabs);
    setVisible(true);
  }
}