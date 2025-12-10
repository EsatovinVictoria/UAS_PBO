/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.apple.kuis2;

/**
 *
 * @author USER
 */
public enum PaymentMethod {
  CASH("Cash"), EWALLET("E-Wallet"), DEBIT("Debit Card"), CREDIT("Credit Card"), QRIS("QRIS"), BANK_TRANSFER(
      "Bank Transfer");

  private final String label;

  PaymentMethod(String label) {
    this.label = label;
  }

  @Override
  public String toString() {
    return label;
  }
}
