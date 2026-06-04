# PRODUCT REQUIREMENT DOCUMENT (PRD)
**TUGAS BESAR UAS - PEMROGRAMAN BERORIENTASI OBJEK (PBO)**

---

## IDENTITAS PROYEK
* **Judul Proyek:** Penerapan JNI pada Sistem Self-Ordering Mie Gacoan: Integrasi NetBeans Java dan Qt C++ untuk Kalkulasi Nota hingga Otomatisasi Panggilan
* **Target Sistem Operasi:** Windows (Output Native: `.dll`)
* **Arsitektur Utama:** Java Native Interface (JNI) Hybrid

---

## 1. TUJUAN & CAKUPAN SISTEM
Aplikasi ini mereplikasi ekosistem pemesanan mandiri (*self-ordering*) pada gerai Mie Gacoan secara *end-to-end*. Sistem mengintegrasikan frontend berbasis **Java Swing (NetBeans)** untuk antarmuka pengguna (Pelanggan & Dapur) dengan backend berperforma tinggi berbasis **Qt C++ Engine** melalui **JNI** untuk komputasi finansial serta otomatisasi audio panggilan berbasis *Text-to-Speech* (TTS).

---

## 2. SPESIFIKASI ATURAN BISNIS & MENU
Sistem menerapkan aturan bisnis spesifik berdasarkan fakta lapangan gerai Mie Gacoan:

### A. Daftar Menu Ikonik & Struktur Harga
* **Kategori Makanan:**
  * Mie Gacoan / Hompimpa (Level 1 - 4): Rp 11.000
  * Mie Gacoan / Hompimpa (Level 5 - 8): Rp 13.000 *(Kenaikan harga progresif)*
  * Dimsum (Siomay / Udang Rambutan): Rp 10.000
* **Kategori Minuman:**
  * Es Gobak Sodor / Es Teklek: Rp 9.000

### B. Aturan Finansial & Operasional
* **Pajak Restoran (PB1):** Dikenakan sebesar **10%** dari total belanja (*subtotal*).
* **Biaya Tambahan:** Biaya *take-away* atau *service charge* ditiadakan (**Rp 0**).
* **Nomor Meja Otomatis:** Nomor meja diidentifikasi secara otomatis berdasarkan simulasi pemindaian QR Code yang unik per meja.

---

## 3. MEKANISME ENVIRONMENT CHECK
Sebelum masuk ke menu utama, sistem akan menjalankan fungsi **Pre-Flight Environment Check** untuk memastikan semua dependensi siap di laptop:

1. **Java Runtime Check:** Memverifikasi ketersediaan dan versi *Java Development Kit* (JDK/JRE).
2. **C++ & Qt Compatibility Check:** Memastikan arsitektur compiler (MinGW 64-bit) kompatibel dengan ekosistem Windows.
3. **JNI Library Verification:** Mengecek apakah file pustaka dinamis `GacoanEngine.dll` telah terkompilasi dan berada di *library path* sebelum GUI diinisialisasi.

---

## 4. RANCANGAN 5 KELAS BERORIENTASI OBJEK (OOP)
Proyek ini mengimplementasikan minimal 5 kelas yang saling membutuhkan:

1. **Kelas Menu (Java):** Representasi data dasar dari setiap item hidangan (ID, nama, harga dasar, kategori).
2. **Kelas ItemPesanan (Java):** Menggabungkan objek `Menu` dengan kuantitas, level pedas, dan catatan konsumen. *(Membutuhkan Kelas Menu)*.
3. **Kelas TransaksiPesanan (Java):** Menyimpan seluruh informasi pesanan (ID nota, nomor meja, daftar belanja, status). *(Membutuhkan Kelas ItemPesanan)*.
4. **Kelas GacoanEngine (C++ via JNI):** Menerima data objek transaksi dari Java, mengkalkulasi harga berdasarkan aturan level pedas, menambahkan PB1 10%, dan mengembalikan string nota terformat ke Java. *(Membutuhkan Kelas TransaksiPesanan)*.
5. **Kelas SistemNotifikasi (C++ via JNI):** Mengatur antrean panggilan berbasis audio menggunakan **Opsi B (Text-to-Speech)** memanfaatkan pustaka native Windows (`<sapi.h>` atau Qt TextToSpeech) untuk mengeja kalimat panggilan: *"Pesanan untuk meja nomor [X], silakan ambil."*

---

## 5. RANCANGAN GUI (2 PANEL UTAMA - NETBEANS SWING)

Aplikasi menggunakan komponen **`JTabbedPane`** untuk memisahkan hak akses aktor:

### PANEL 1: Sisi Pelanggan (Simulasi QR Screen)
* Mensimulasikan tampilan *web-view* pemesanan meja (Nomor meja langsung terkunci otomatis dari sistem).
* Berisi daftar pilihan menu mie beserta level pedasnya, dimsum, dan minuman.
* Dilengkapi keranjang belanja yang menampilkan *Subtotal*, *PB1 (10%)*, *Total Akhir*, serta tombol **"PROSES BAYAR"**.

### PANEL 2: Sisi Dapur & Monitor Pengambilan (Kitchen Display System)
* Digunakan oleh kru masak dan kasir.
* Menampilkan kartu antrean makanan masuk secara mendetail.
* Dilengkapi tombol **"SELESAI MASAK"** yang jika diklik akan mengubah status pesanan sekaligus memanggil fungsi *native* C++ untuk membunyikan suara panggilan TOA secara otomatis.

---

## 6. PUSTAKA INTERNAL YANG DIGUNAKAN

### Pustaka Internal Java (NetBeans):
* `java.util.ArrayList` atau `java.util.Vector` (Struktur data penampungan dinamis).
* `java.awt` / `javax.swing` (Fondasi komponen grafis GUI).

### Pustaka Internal C++ & Qt:
* `<string>` / `<string_view>` (Manipulasi teks kecepatan tinggi pada memori JNI).
* `Qt Multimedia` atau Windows Native SDK `<sapi.h>` (Menggerakkan mesin suara Text-to-Speech).