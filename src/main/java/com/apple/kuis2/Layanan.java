/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.apple.kuis2;

/**
 *
 * @author USER
 */
public interface Layanan {
  String getNama();
  double getHarga(); // biaya layanan flat
  default boolean isAvailable() { return true; }
}