package service;

import dao.ProdukDAO;
import model.BBM;
import java.util.List;

public class ProdukService {
  private final ProdukDAO produkDAO;

  public ProdukService() {
    this.produkDAO = new ProdukDAO();
  }

  public List<BBM> getAllBBM() {
    return produkDAO.getAllBBM();
  }

  public BBM getBBMById(int id) {
    return produkDAO.getBBMById(id);
  }

  public boolean tambahBBM(BBM bbm) {
    return produkDAO.addBBM(bbm);
  }

  public boolean updateBBM(BBM bbm) {
    return produkDAO.updateBBM(bbm);
  }

  public boolean hapusBBM(int id) {
    return produkDAO.deleteBBM(id);
  }

  public double getHargaLayananAngin() {
    return produkDAO.getHargaLayananAngin();
  }

  public boolean setHargaLayananAngin(double harga) {
    return produkDAO.updateHargaLayananAngin(harga);
  }

  public List<BBM> cariBBM(String keyword) {
    return produkDAO.searchBBM(keyword);
  }

  public boolean isBBMExist(String nama) {
    return produkDAO.searchBBM(nama).size() > 0;
  }
}