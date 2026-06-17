# PRODUCT REQUIREMENT DOCUMENT
**Tugas Besar PBO - Sistem Self-Ordering Mie Gacoan Pure Java**

---

## Identitas Proyek

- **Judul:** Sistem Self-Ordering dan Kitchen Display System Mie Gacoan
- **Platform:** Java NetBeans
- **GUI:** Java Swing
- **Arsitektur:** Pure Java Object-Oriented Application

---

## 1. Tujuan Sistem

Aplikasi ini mensimulasikan proses pemesanan menu Mie Gacoan. Pelanggan dapat memilih menu, level pedas, dan catatan pesanan. Setelah transaksi diproses, pesanan masuk ke tampilan dapur agar kru dapat menyelesaikan dan memanggil meja pelanggan.

---

## 2. Aktor Pengguna

1. **Pelanggan**
   - Scan QR meja secara simulasi.
   - Pilih menu.
   - Pilih level pedas.
   - Tambah catatan.
   - Proses pembayaran.

2. **Kru Dapur / Kasir**
   - Melihat antrean pesanan.
   - Mengecek detail item.
   - Menandai pesanan selesai.
   - Memanggil nomor meja.

---

## 3. Aturan Bisnis

- Makanan level 0-4: Rp 11.000.
- Makanan level 5-8: Rp 13.000.
- Dimsum: Rp 10.000.
- Minuman: Rp 9.000.
- Pajak restoran PB1: 10%.
- Biaya layanan: Rp 0.

---

## 4. Class Utama

1. `Menu`
   - Data menu: id, nama, harga dasar, kategori.

2. `ItemPesanan`
   - Data item keranjang: menu, kuantitas, level pedas, catatan.

3. `TransaksiPesanan`
   - Data transaksi: id nota, nomor meja, daftar belanja, status.

4. `GacoanEngine`
   - Menghitung subtotal, pajak, total akhir, dan membuat struk.

5. `SistemNotifikasi`
   - Memutar audio panggilan meja atau fallback beep/log Java.

6. `PreFlightCheck`
   - Mengecek kesiapan runtime Java dan engine aplikasi.

7. `UITheme`
   - Mengatur style visual Swing.

8. `GacoanApp`
   - GUI utama aplikasi.

---

## 5. Rancangan GUI

### Panel Pelanggan

- Simulasi QR meja.
- Daftar menu.
- Filter kategori.
- Pilihan level pedas.
- Input catatan.
- Tabel keranjang.
- Subtotal, PB1 10%, dan total tagihan.
- Tombol proses bayar.

### Panel Dapur / KDS

- Antrean pesanan aktif.
- Detail item pesanan.
- Tombol selesai masak.
- Riwayat panggilan meja.
- Tombol panggil ulang.

---

## 6. Pustaka Internal Java

- `javax.swing` untuk GUI.
- `java.awt` untuk layout, warna, event, dan beep.
- `java.util.ArrayList` dan `java.util.List` untuk penampung data.
- `javax.sound.sampled` untuk memutar audio WAV lokal.

---

## 7. Cara Menjalankan

### NetBeans

- Import sebagai `Java Project with Existing Sources`.
- Source folder: `C:\EAS-PBO-Gacoan`.
- Main class: `gacoan.GacoanApp`.
- VM Options: kosong.

### Terminal Opsional

```bat
compile.bat
```

Terminal hanya opsional untuk build cepat. Aplikasi tetap dapat dijalankan dari NetBeans karena seluruh logic sudah pure Java.
