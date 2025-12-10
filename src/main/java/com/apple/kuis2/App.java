package com.apple.kuis2;

import view.LoginForm;
import service.LoginService;
import javax.swing.UIManager;

public class App {
  public static void main(String[] args) {
    // Set look and feel
    try {
      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    } catch (Exception e) {
      e.printStackTrace();
    }

    // Jalankan aplikasi
    java.awt.EventQueue.invokeLater(() -> {
      LoginForm loginForm = new LoginForm();
      // Tambahkan controller
      new LoginService(loginForm);
      loginForm.setVisible(true);
    });
  }
}