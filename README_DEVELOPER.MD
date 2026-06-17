# DEVELOPER DOCUMENTATION
**Sistem Self-Ordering & Kitchen Display System Mie Gacoan - Pure Java NetBeans**

Dokumen ini menjelaskan versi final aplikasi yang berjalan penuh di Java. Aplikasi tidak lagi membutuhkan Qt, C++, JNI, DLL, MinGW, atau terminal untuk membangun library native.

---

## 1. Ikhtisar Sistem

Aplikasi ini adalah simulasi pemesanan mandiri Mie Gacoan berbasis Java Swing. Semua logika utama berjalan di Java:

- GUI pelanggan dan dapur memakai `javax.swing` dan `java.awt`.
- Data menu, item pesanan, dan transaksi memakai class Java biasa.
- Kalkulasi nota dilakukan oleh `GacoanEngine.java`.
- Panggilan antrean dilakukan oleh `SistemNotifikasi.java` memakai audio WAV lokal jika tersedia, lalu fallback ke beep/log Java.

---

## 2. Struktur Class

1. `Menu.java`
   - Menyimpan data menu: id, nama, harga dasar, dan kategori.

2. `ItemPesanan.java`
   - Menyimpan satu item keranjang: `Menu`, kuantitas, level pedas, dan catatan.

3. `TransaksiPesanan.java`
   - Menyimpan id nota, nomor meja, daftar belanja, dan status pesanan.

4. `GacoanEngine.java`
   - Engine Java murni untuk menghitung harga, PB1 10%, total akhir, dan format struk.

5. `SistemNotifikasi.java`
   - Notifikasi Java murni untuk panggilan meja. Jika `audio/meja_X.wav` ada, file itu diputar.

6. `PreFlightCheck.java`
   - Mengecek kesiapan runtime Java, Swing GUI, dan Java billing engine.

7. `UITheme.java`
   - Warna dan efek visual untuk tampilan Swing.

8. `GacoanApp.java`
   - Class utama GUI: halaman pre-flight, tab pelanggan, keranjang, struk, dan KDS dapur.

---

## 3. Alur Aplikasi

1. User membuka aplikasi dari NetBeans atau `compile.bat`.
2. Pre-flight screen memastikan runtime Java siap.
3. Pelanggan melakukan simulasi scan QR meja.
4. Pelanggan memilih menu, level pedas, kuantitas, dan catatan.
5. Keranjang menampilkan subtotal, PB1 10%, dan total.
6. Saat proses bayar, `TransaksiPesanan` dibuat dan dihitung oleh `GacoanEngine`.
7. Struk tampil di dialog.
8. Pesanan masuk ke Kitchen Display System.
9. Kru dapur klik selesai masak, lalu `SistemNotifikasi` memanggil nomor meja.

---

## 4. Cara Jalankan di NetBeans

1. Buka NetBeans.
2. Pilih `File > New Project`.
3. Pilih `Java with Ant > Java Project with Existing Sources`.
4. Source folder arahkan ke:

```text
C:\EAS-PBO-Gacoan
```

5. Main class:

```text
gacoan.GacoanApp
```

6. VM Options tidak perlu diisi. Tidak perlu `-Djava.library.path`.
7. Klik Run.

---

## 5. Cara Jalankan dari Terminal Opsional

Terminal tidak wajib untuk NetBeans, tapi `compile.bat` tersedia untuk build cepat:

```bat
compile.bat
```

Script ini hanya melakukan:

1. compile file `.java` ke folder `bin`;
2. menghapus artifact DLL lama jika masih ada;
3. menjalankan `java -cp bin gacoan.GacoanApp`.

---

## 6. Catatan Penting

- Versi final ini pure Java.
- Tidak ada method `native`.
- Tidak ada JNI header.
- Tidak ada build C++.
- Tidak perlu compiler MinGW.
- Tidak perlu file `GacoanEngine.dll`.
- Folder/file C++ lama boleh dianggap arsip atau versi eksperimen, bukan jalur build utama aplikasi final.
