package model;

public class User {
  private int id;
  private String username;
  private String password;
  private String namaLengkap;
  private String role; // "admin" atau "kasir"
  private String noTelepon;
  private String alamat;
  private boolean aktif;

  public User(int id, String username, String password, String namaLengkap,
      String role, String noTelepon, String alamat, boolean aktif) {
    this.id = id;
    this.username = username;
    this.password = password;
    this.namaLengkap = namaLengkap;
    this.role = role;
    this.noTelepon = noTelepon;
    this.alamat = alamat;
    this.aktif = aktif;
  }

  public User(String username, String password, String namaLengkap,
      String role, String noTelepon, String alamat) {
    this(0, username, password, namaLengkap, role, noTelepon, alamat, true);
  }

  // Getter dan Setter
  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public String getNamaLengkap() {
    return namaLengkap;
  }

  public void setNamaLengkap(String namaLengkap) {
    this.namaLengkap = namaLengkap;
  }

  public String getRole() {
    return role;
  }

  public void setRole(String role) {
    this.role = role;
  }

  public String getNoTelepon() {
    return noTelepon;
  }

  public void setNoTelepon(String noTelepon) {
    this.noTelepon = noTelepon;
  }

  public String getAlamat() {
    return alamat;
  }

  public void setAlamat(String alamat) {
    this.alamat = alamat;
  }

  public boolean isAktif() {
    return aktif;
  }

  public void setAktif(boolean aktif) {
    this.aktif = aktif;
  }

  // Method utilitas
  public boolean isAdmin() {
    return "admin".equalsIgnoreCase(role);
  }

  public boolean isKasir() {
    return "kasir".equalsIgnoreCase(role);
  }

  public String getRoleDisplay() {
    if (isAdmin())
      return "Administrator";
    if (isKasir())
      return "Kasir";
    return role;
  }

  public String getStatusDisplay() {
    return aktif ? "Aktif" : "Non-Aktif";
  }

  public Object[] toTableRow() {
    return new Object[] {
        id,
        username,
        namaLengkap,
        getRoleDisplay(),
        noTelepon,
        alamat,
        getStatusDisplay()
    };
  }

  @Override
  public String toString() {
    return String.format("%s (%s) - %s", namaLengkap, username, getRoleDisplay());
  }
}