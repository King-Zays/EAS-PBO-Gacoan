#define INITGUID
#include "GacoanEngine.h"
#include <QDebug>

#include <sstream>
#include <string>
#include <iomanip>

#ifdef _WIN32
#include <windows.h>
#include <mmsystem.h>
#include <sapi.h>
#include <iostream>
#endif

QString GacoanEngine::formatRupiah(double amount) {
    long long val = static_cast<long long>(amount);
    std::string s = std::to_string(val);
    int n = static_cast<int>(s.length()) - 3;
    while (n > 0) {
        s.insert(n, ".");
        n -= 3;
    }
    return QString("Rp %1").arg(QString::fromStdString(s));
}

QString GacoanEngine::hitungNota(const TransaksiPesanan& transaksi) const {
    double subtotal = 0;
    QString itemDetails;

    for (const auto& item : transaksi.getDaftarBelanja()) {
        const Menu& menu = item.getMenu();
        int qty = item.getKuantitas();
        int lvl = item.getLevelPedas();
        QString catatan = item.getCatatan();
        QString nama = menu.getNama();
        QString kategori = menu.getKategori();

        double calculatedPrice = menu.getHargaDasar();
        if (kategori.compare("Makanan", Qt::CaseInsensitive) == 0) {
            if (lvl >= 1 && lvl <= 4) {
                calculatedPrice = 11000;
            } else if (lvl >= 5 && lvl <= 8) {
                calculatedPrice = 13000;
            } else if (lvl == 0) {
                calculatedPrice = 11000;
            }
        } else if (kategori.compare("Dimsum", Qt::CaseInsensitive) == 0) {
            calculatedPrice = 10000;
        } else if (kategori.compare("Minuman", Qt::CaseInsensitive) == 0) {
            calculatedPrice = 9000;
        }

        double itemSubtotal = qty * calculatedPrice;
        subtotal += itemSubtotal;

        itemDetails += QString(" %1").arg(nama);
        if (kategori.compare("Makanan", Qt::CaseInsensitive) == 0) {
            itemDetails += QString(" (Lvl %1)").arg(lvl);
        }
        itemDetails += "\n";
        itemDetails += QString("   %1 x %2                      %3\n")
                           .arg(qty)
                           .arg(formatRupiah(calculatedPrice))
                           .arg(formatRupiah(itemSubtotal));
        if (!catatan.isEmpty()) {
            itemDetails += QString("   *Catatan: %1\n").arg(catatan);
        }
        itemDetails += "\n";
    }

    double pb1 = subtotal * 0.10;
    double totalAkhir = subtotal + pb1;

    QString receipt;
    receipt += "================================================\n";
    receipt += "                  MIE GACOAN                    \n";
    receipt += "           SISTEM PEMESANAN MANDIRI             \n";
    receipt += "================================================\n";
    receipt += QString(" ID NOTA : %1\n").arg(transaksi.getIdNota());
    receipt += QString(" MEJA    : %1\n").arg(transaksi.getNomorMeja());
    receipt += "------------------------------------------------\n";
    receipt += itemDetails;
    receipt += "------------------------------------------------\n";
    receipt += QString(" Subtotal            :        %1\n").arg(formatRupiah(subtotal));
    receipt += QString(" Pajak Resto (PB1 10%):       %1\n").arg(formatRupiah(pb1));
    receipt += " Biaya Layanan       :        Rp 0\n";
    receipt += "------------------------------------------------\n";
    receipt += QString(" TOTAL AKHIR         :        %1\n").arg(formatRupiah(totalAkhir));
    receipt += "================================================\n";
    receipt += "      Terima kasih atas pesanan Anda!           \n";
    receipt += "  Silakan monitor KDS dapur untuk pengambilan.  \n";
    receipt += "================================================\n";

    return receipt;
}

void GacoanEngine::panggilAntrean(int nomorMeja) {
#ifdef _WIN32
    std::wstring wavPath = L"audio/meja_" + std::to_wstring(nomorMeja) + L".wav";
    DWORD attrib = GetFileAttributesW(wavPath.c_str());
    if (attrib != INVALID_FILE_ATTRIBUTES && !(attrib & FILE_ATTRIBUTE_DIRECTORY)) {
        PlaySoundW(wavPath.c_str(), NULL, SND_FILENAME | SND_SYNC);
        return;
    }

    ISpVoice* pVoice = nullptr;

    if (FAILED(::CoInitialize(nullptr))) {
        std::cerr << "[TTS] Failed to initialize COM" << std::endl;
        return;
    }

    HRESULT hr = CoCreateInstance(CLSID_SpVoice, nullptr, CLSCTX_ALL, IID_ISpVoice, (void**)&pVoice);
    if (SUCCEEDED(hr)) {
        std::wstring text = L"Pesanan untuk meja nomor " + std::to_wstring(nomorMeja) + L", silakan ambil.";

        pVoice->Speak(text.c_str(), 0, nullptr);
        pVoice->Release();
        pVoice = nullptr;
    } else {
        std::cerr << "[TTS] Failed to create SpVoice instance: " << hr << std::endl;
    }

    ::CoUninitialize();
#else
    qDebug() << "[TTS Fallback] Panggilan Meja" << nomorMeja << "terpantau.";
#endif
}
