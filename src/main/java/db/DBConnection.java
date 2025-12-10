package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    private static final String URL = "jdbc:mysql://localhost:3306/db_pombensin"; // sesuaikan dengan database kamu
    private static final String USER = "root"; // ubah jika pakai user lain
    private static final String PASS = ""; // isi password MySQL kamu

    public static Connection getConnection() {
        Connection conn = null;

        try {
            conn = DriverManager.getConnection(URL, USER, PASS);
            System.out.println("Koneksi database berhasil!");
        } catch (SQLException e) {
            System.err.println("Koneksi database gagal: " + e.getMessage());
        }

        return conn;
    }
}
