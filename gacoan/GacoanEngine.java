package gacoan;

public class GacoanEngine {

    public String hitungNota(TransaksiPesanan transaksi) {
        if (transaksi == null) {
            return "Error: Transaksi null";
        }

        double subtotal = 0;
        StringBuilder itemDetails = new StringBuilder();

        for (ItemPesanan item : transaksi.getDaftarBelanja()) {
            Menu menu = item.getMenu();
            double hargaFinal = hitungHargaFinal(menu, item.getLevelPedas());
            double subtotalItem = item.getKuantitas() * hargaFinal;
            subtotal += subtotalItem;

            itemDetails.append(" ").append(menu.getNama());
            if (menu.getKategori().equalsIgnoreCase("Makanan")) {
                itemDetails.append(" (Lvl ").append(item.getLevelPedas()).append(")");
            }
            itemDetails.append("\n");
            itemDetails.append("   ")
                    .append(item.getKuantitas())
                    .append(" x ")
                    .append(formatRupiah(hargaFinal))
                    .append("                      ")
                    .append(formatRupiah(subtotalItem))
                    .append("\n");

            if (!item.getCatatan().isEmpty()) {
                itemDetails.append("   *Catatan: ").append(item.getCatatan()).append("\n");
            }
            itemDetails.append("\n");
        }

        double pb1 = subtotal * 0.10;
        double totalAkhir = subtotal + pb1;

        StringBuilder receipt = new StringBuilder();
        receipt.append("================================================\n");
        receipt.append("                  MIE GACOAN                    \n");
        receipt.append("           SISTEM PEMESANAN MANDIRI             \n");
        receipt.append("================================================\n");
        receipt.append(" ID NOTA : ").append(transaksi.getIdNota()).append("\n");
        receipt.append(" MEJA    : ").append(transaksi.getNomorMeja()).append("\n");
        receipt.append("------------------------------------------------\n");
        receipt.append(itemDetails);
        receipt.append("------------------------------------------------\n");
        receipt.append(" Subtotal            :        ").append(formatRupiah(subtotal)).append("\n");
        receipt.append(" Pajak Resto (PB1 10%):       ").append(formatRupiah(pb1)).append("\n");
        receipt.append(" Biaya Layanan       :        Rp 0\n");
        receipt.append("------------------------------------------------\n");
        receipt.append(" TOTAL AKHIR         :        ").append(formatRupiah(totalAkhir)).append("\n");
        receipt.append("================================================\n");
        receipt.append("      Terima kasih atas pesanan Anda!           \n");
        receipt.append("  Silakan monitor KDS dapur untuk pengambilan.  \n");
        receipt.append("================================================\n");

        return receipt.toString();
    }

    private double hitungHargaFinal(Menu menu, int levelPedas) {
        if (menu.getKategori().equalsIgnoreCase("Makanan")) {
            return levelPedas >= 5 ? 13000 : 11000;
        }
        if (menu.getKategori().equalsIgnoreCase("Dimsum")) {
            return 10000;
        }
        if (menu.getKategori().equalsIgnoreCase("Minuman")) {
            return 9000;
        }
        return menu.getHargaDasar();
    }

    private String formatRupiah(double amount) {
        long value = (long) amount;
        String digits = Long.toString(value);
        StringBuilder result = new StringBuilder();

        int counter = 0;
        for (int i = digits.length() - 1; i >= 0; i--) {
            if (counter > 0 && counter % 3 == 0) {
                result.insert(0, ".");
            }
            result.insert(0, digits.charAt(i));
            counter++;
        }

        return "Rp " + result;
    }
}
