/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.apple.kuis2;

/**
 *
 * @author USER
 */
  import java.util.ArrayList;
  import java.util.Collections;
  import java.util.List;
  import java.util.Optional;

  public class BBMManager {
    private final List<JenisBBM> daftar;

    public BBMManager() {
      this.daftar = new ArrayList<>();
    }

    public BBMManager(List<JenisBBM> initial) {
      this.daftar = new ArrayList<>();
      if (initial != null)
        this.daftar.addAll(initial);
    }

    public static BBMManager defaultHarga() {
      List<JenisBBM> init = new ArrayList<>();
      init.add(new JenisBBM("Pertalite", 10000.0));
      init.add(new JenisBBM("Pertamax", 14000.0));
      init.add(new JenisBBM("Solar", 7000.0));
      init.add(new JenisBBM("Dexlite", 13000.0));
      return new BBMManager(init);
    }

    public List<JenisBBM> getDaftar() {
      return Collections.unmodifiableList(new ArrayList<>(daftar));
    }

    public void tambah(JenisBBM bbm) {
      if (bbm == null)
        throw new IllegalArgumentException("bbm null");
      daftar.add(bbm);
    }

    public Optional<JenisBBM> findByName(String nama) {
      if (nama == null)
        return Optional.empty();
      return daftar.stream()
          .filter(j -> j.getNama().equalsIgnoreCase(nama.trim()))
          .findFirst();
    }

    public boolean updateHarga(String nama, double hargaBaru) {
      Optional<JenisBBM> opt = findByName(nama);
      if (opt.isPresent()) {
        JenisBBM j = opt.get();
        j.setHargaPerLiter(hargaBaru);
        return true;
      }
      return false;
    }

    public void setDaftar(List<JenisBBM> baru) {
      daftar.clear();
      if (baru != null)
        daftar.addAll(baru);
    }
  }
