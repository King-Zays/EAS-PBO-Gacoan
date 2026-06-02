package gacoan;

public class Menu {
    private String id;
    private String nama;
    private double hargaDasar;
    private String kategori; // Makanan, Dimsum, Minuman

    public Menu(String id, String nama, double hargaDasar, String kategori) {
        this.id = id;
        this.nama = nama;
        this.hargaDasar = hargaDasar;
        this.kategori = kategori;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public double getHargaDasar() {
        return hargaDasar;
    }

    public void setHargaDasar(double hargaDasar) {
        this.hargaDasar = hargaDasar;
    }

    public String getKategori() {
        return kategori;
    }

    public void setKategori(String kategori) {
        this.kategori = kategori;
    }
}
