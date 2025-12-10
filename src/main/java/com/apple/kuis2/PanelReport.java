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
import java.awt.*;
import java.util.Map;

public class PanelReport extends JPanel {
  private final TransaksiController controller;
  private final JTextArea area;

  public PanelReport(TransaksiController controller) {
    this.controller = controller;
    setLayout(new BorderLayout());
    area = new JTextArea();
    area.setEditable(false);
    add(new JScrollPane(area), BorderLayout.CENTER);

    JButton btnRefresh = new JButton("Refresh Report (By Month)");
    btnRefresh.addActionListener(e -> loadReport());
    add(btnRefresh, BorderLayout.NORTH);

    loadReport();
  }

  private void loadReport() {
    area.setText("");
    MySQLReportGenerator rg = new MySQLReportGenerator();
    var map = rg.generateReport();
    if (map.isEmpty()) {
      area.append("Tidak ada data.\n");
      return;
    }
    area.append(String.format("%-10s | %s%n", "BULAN", "TOTAL (Rp)"));
    area.append("---------------------------\n");
    for (Map.Entry<String, Double> en : map.entrySet()) {
      area.append(String.format("%-10s | %.2f%n", en.getKey(), en.getValue()));
    }
  }
}