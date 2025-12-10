package service;

import model.BBM;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class BBMManager {
    private final List<BBM> daftarBBM;

    public BBMManager() {
        this.daftarBBM = new ArrayList<>();
    }

    public BBMManager(List<BBM> initial) {
        this.daftarBBM = new ArrayList<>();
        if (initial != null) {
            this.daftarBBM.addAll(initial);
        }
    }

    public static BBMManager defaultHarga() {
        List<BBM> init = new ArrayList<>();
        init.add(new BBM("Pertalite", 10000, "Pertalite", 90));
        init.add(new BBM("Pertamax", 14000, "Pertamax", 92));
        init.add(new BBM("Solar", 7000, "Solar", 0));
        init.add(new BBM("Pertamax Turbo", 15000, "Pertamax Turbo", 95));
        return new BBMManager(init);
    }

    public List<BBM> getDaftarBBM() {
        return Collections.unmodifiableList(new ArrayList<>(daftarBBM));
    }

    public void tambahBBM(BBM bbm) {
        if (bbm == null) {
            throw new IllegalArgumentException("BBM tidak boleh null");
        }
        // Cek apakah nama sudah ada
        if (findByNama(bbm.getNama()).isPresent()) {
            throw new IllegalArgumentException("BBM dengan nama " + bbm.getNama() + " sudah ada");
        }
        daftarBBM.add(bbm);
    }

    public Optional<BBM> findByNama(String nama) {
        if (nama == null || nama.trim().isEmpty()) {
            return Optional.empty();
        }
        return daftarBBM.stream()
                .filter(bbm -> bbm.getNama().equalsIgnoreCase(nama.trim()))
                .findFirst();
    }

    public Optional<BBM> findById(int id) {
        return daftarBBM.stream()
                .filter(bbm -> bbm.getId() == id)
                .findFirst();
    }

    public boolean updateBBM(int id, BBM bbmBaru) {
        Optional<BBM> opt = findById(id);
        if (opt.isPresent()) {
            BBM bbm = opt.get();
            bbm.setNama(bbmBaru.getNama());
            bbm.setHarga(bbmBaru.getHarga());
            bbm.setJenis(bbmBaru.getJenis());
            bbm.setOktan(bbmBaru.getOktan());
            bbm.setAktif(bbmBaru.isAktif());
            return true;
        }
        return false;
    }

    public boolean updateHarga(String nama, double hargaBaru) {
        Optional<BBM> opt = findByNama(nama);
        if (opt.isPresent()) {
            BBM bbm = opt.get();
            bbm.setHarga(hargaBaru);
            return true;
        }
        return false;
    }

    public boolean hapusBBM(int id) {
        return daftarBBM.removeIf(bbm -> bbm.getId() == id);
    }

    public boolean hapusBBMByNama(String nama) {
        return daftarBBM.removeIf(bbm -> bbm.getNama().equalsIgnoreCase(nama));
    }

    public List<BBM> cariBBM(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return new ArrayList<>(daftarBBM);
        }
        
        String lowerKeyword = keyword.toLowerCase();
        List<BBM> hasil = new ArrayList<>();
        
        for (BBM bbm : daftarBBM) {
            if (bbm.getNama().toLowerCase().contains(lowerKeyword) ||
                bbm.getJenis().toLowerCase().contains(lowerKeyword)) {
                hasil.add(bbm);
            }
        }
        
        return hasil;
    }

    public void setDaftarBBM(List<BBM> baru) {
        daftarBBM.clear();
        if (baru != null) {
            daftarBBM.addAll(baru);
        }
    }

    public int getJumlahBBM() {
        return daftarBBM.size();
    }

    public double getHargaBBM(String nama) {
        Optional<BBM> opt = findByNama(nama);
        return opt.map(BBM::getHarga).orElse(0.0);
    }
}