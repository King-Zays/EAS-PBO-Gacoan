package gacoan;

import java.util.ArrayList;
import java.util.List;

public class TransaksiPesanan {
    private String idNota;
    private int nomorMeja;
    private List<ItemPesanan> daftarBelanja;
    private String status;

    public TransaksiPesanan(String idNota, int nomorMeja) {
        this.idNota = idNota;
        this.nomorMeja = nomorMeja;
        this.daftarBelanja = new ArrayList<>();
        this.status = "PENDING";
    }

    public String getIdNota() {
        return idNota;
    }

    public void setIdNota(String idNota) {
        this.idNota = idNota;
    }

    public int getNomorMeja() {
        return nomorMeja;
    }

    public void setNomorMeja(int nomorMeja) {
        this.nomorMeja = nomorMeja;
    }

    public List<ItemPesanan> getDaftarBelanja() {
        return daftarBelanja;
    }

    public void setDaftarBelanja(List<ItemPesanan> daftarBelanja) {
        this.daftarBelanja = daftarBelanja;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void tambahItem(ItemPesanan item) {
        this.daftarBelanja.add(item);
    }
}
