/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.apple.kuis2;

/**
 *
 * @author USER
 */
import java.util.Map;

public class MySQLReportGenerator extends ReportGenerator {
  private final TransaksiDAO dao = new TransaksiDAO();

  @Override
  public Map<String, Double> generateReport() {
    try {
      return dao.getPendapatanPerBulan();
    } catch (Exception e) {
      e.printStackTrace();
      return Map.of();
    }
  }
}