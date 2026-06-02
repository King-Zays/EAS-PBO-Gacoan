package gacoan;

public class ItemPesanan {
    private Menu menu;
    private int kuantitas;
    private int levelPedas; // 0 sampai 8
    private String catatan;

    public ItemPesanan(Menu menu, int kuantitas, int levelPedas, String catatan) {
        this.menu = menu;
        this.kuantitas = kuantitas;
        this.levelPedas = levelPedas;
        this.catatan = catatan == null ? "" : catatan;
    }

    public Menu getMenu() {
        return menu;
    }

    public void setMenu(Menu menu) {
        this.menu = menu;
    }

    public int getKuantitas() {
        return kuantitas;
    }

    public void setKuantitas(int kuantitas) {
        this.kuantitas = kuantitas;
    }

    public int getLevelPedas() {
        return levelPedas;
    }

    public void setLevelPedas(int levelPedas) {
        this.levelPedas = levelPedas;
    }

    public String getCatatan() {
        return catatan;
    }

    public void setCatatan(String catatan) {
        this.catatan = catatan;
    }

    public String getSpicyLevelName() {
        if (menu.getKategori().equalsIgnoreCase("Makanan")) {
            if (levelPedas == 0) return "Original (Lvl 0)";
            return "Pedas (Lvl " + levelPedas + ")";
        }
        return "";
    }
}
