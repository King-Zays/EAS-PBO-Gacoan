# MIE GACOAN — C++ Qt Edition

Aplikasi Self-Ordering Kiosk & Kitchen Display System (KDS) yang di-port dari versi Java Swing ke C++ Qt Widgets secara mandiri (**tanpa JNI**).

## Prasyarat

1. **Qt Framework** (Qt5 atau Qt6)
   - Download: https://www.qt.io/download-qt-installer
   - Saat instalasi, centang komponen **Qt Widgets** dan **MinGW** (atau MSVC jika pakai Visual Studio).
   - Pastikan `qmake` atau path Qt tersedia di environment.

2. **CMake** (versi 3.16+)
   - Sudah terinstal di sistem Anda (`cmake version 4.2.3`).

3. **Compiler C++17**
   - MinGW-w64 g++ (sudah tersedia) atau MSVC.

## Cara Build & Run

### Opsi A: Menggunakan CMake CLI

```bash
cd gacoan-qt
mkdir build
cd build

# Konfigurasi (sesuaikan path Qt jika perlu)
cmake .. -DCMAKE_PREFIX_PATH="C:/Qt/6.x.x/mingw_64"

# Build
cmake --build .

# Jalankan
./GacoanQt.exe
```

### Opsi B: Menggunakan Qt Creator

1. Buka Qt Creator
2. **File > Open File or Project** → pilih `gacoan-qt/CMakeLists.txt`
3. Pilih kit compiler (MinGW atau MSVC)
4. Klik **Build** (Ctrl+B) → **Run** (Ctrl+R)

## Struktur File

```
gacoan-qt/
├── CMakeLists.txt          # Build system (CMake)
├── main.cpp                # Entry point + load QSS
├── GacoanApp.h             # Main window header (QMainWindow)
├── GacoanApp.cpp           # Main window implementation (seluruh UI)
├── GacoanEngine.h          # Billing engine + TTS header
├── GacoanEngine.cpp        # Billing engine + TTS implementation (native C++)
├── Menu.h                  # Data model: Menu
├── ItemPesanan.h           # Data model: Item Pesanan
├── TransaksiPesanan.h      # Data model: Transaksi
├── style.qss               # Qt Style Sheets (tema visual)
└── README.md               # Dokumentasi ini
```

## Pemetaan dari Java

| Java Swing                  | C++ Qt Widgets                |
|-----------------------------|-------------------------------|
| `GacoanApp.java` (JFrame)  | `GacoanApp.cpp` (QMainWindow) |
| `UITheme.java`             | `style.qss` (QSS)            |
| `Menu.java`                | `Menu.h` (struct)             |
| `ItemPesanan.java`         | `ItemPesanan.h` (class)       |
| `TransaksiPesanan.java`    | `TransaksiPesanan.h` (class)  |
| `GacoanEngine.java` (JNI)  | `GacoanEngine.cpp` (native)   |
| `SistemNotifikasi.java`    | Digabung ke `GacoanEngine`    |
| `PreFlightCheck.java`      | Splash screen sederhana       |

## Fitur

- ✅ Splash screen dengan info runtime
- ✅ Simulasi scan QR meja
- ✅ Grid menu 2 kolom dengan filter kategori
- ✅ Pemilihan level pedas (0-8) dan catatan konsumen
- ✅ Keranjang belanja dengan subtotal/pajak/total
- ✅ Struk digital (receipt dialog)
- ✅ Kitchen Display System (KDS) — Kanban board
- ✅ Text-to-Speech panggilan meja (Windows SAPI)
- ✅ Tema visual Light Mode identik dengan versi Java
- ❌ Tidak ada JNI — sepenuhnya native C++
