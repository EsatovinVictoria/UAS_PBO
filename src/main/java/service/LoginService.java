package service;

import service.UserService;
import model.User;
import view.LoginForm;
import view.DashboardAdmin;
import view.DashboardKasir;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginService {
  private final LoginForm loginForm;
  private final UserService userService;

  public LoginService(LoginForm loginForm) {
    this.loginForm = loginForm;
    this.userService = new UserService();

    // Tambahkan action listener untuk tombol login
    loginForm.getBtnLogin().addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        handleLogin();
      }
    });
  }

  private void handleLogin() {
    String username = loginForm.getUsername();
    String password = loginForm.getPassword();

    // Validasi input
    if (username.isEmpty() || password.isEmpty()) {
      loginForm.setMessage("Username dan password harus diisi!", true);
      return;
    }

    // Authenticate user
    User user = userService.authenticate(username, password);

    if (user == null) {
      loginForm.setMessage("Username atau password salah!", true);
      return;
    }

    if (!user.isAktif()) {
      loginForm.setMessage("Akun tidak aktif!", true);
      return;
    }

    // Login berhasil
    loginForm.setMessage("Login berhasil!", false);

    // Tutup login form
    loginForm.dispose();

    // Buka dashboard sesuai role
    if (user.isAdmin()) {
      DashboardAdmin dashboardAdmin = new DashboardAdmin(user);
      dashboardAdmin.setVisible(true);
    } else if (user.isKasir()) {
      DashboardKasir dashboardKasir = new DashboardKasir(user);
      dashboardKasir.setVisible(true);
    }
  }
}