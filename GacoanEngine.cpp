#define INITGUID
#include <windows.h>
#include <mmsystem.h>
#include <sapi.h>
#include <iostream>
#include <string>
#include <sstream>
#include <iomanip>
#include "gacoan_GacoanEngine.h"
#include "gacoan_SistemNotifikasi.h"

std::string formatRupiah(double amount) {
    long long val = (long long)amount;
    std::string s = std::to_string(val);
    int n = s.length() - 3;
    while (n > 0) {
        s.insert(n, ".");
        n -= 3;
    }
    return "Rp " + s;
}

std::string jstringToString(JNIEnv* env, jstring jstr) {
    if (!jstr) return "";
    const char* strChars = env->GetStringUTFChars(jstr, NULL);
    std::string result(strChars);
    env->ReleaseStringUTFChars(jstr, strChars);
    return result;
}

JNIEXPORT jstring JNICALL Java_gacoan_GacoanEngine_hitungNota(JNIEnv *env, jobject obj, jobject transaksiObj) {
    if (!transaksiObj) {
        return env->NewStringUTF("Error: Transaksi null");
    }

    jclass transClass = env->GetObjectClass(transaksiObj);
    
    jmethodID getIdNotaMid = env->GetMethodID(transClass, "getIdNota", "()Ljava/lang/String;");
    jmethodID getNomorMejaMid = env->GetMethodID(transClass, "getNomorMeja", "()I");
    jmethodID getDaftarBelanjaMid = env->GetMethodID(transClass, "getDaftarBelanja", "()Ljava/util/List;");
    
    jstring idNotaJStr = (jstring)env->CallObjectMethod(transaksiObj, getIdNotaMid);
    std::string idNota = jstringToString(env, idNotaJStr);
    
    jint nomorMeja = env->CallIntMethod(transaksiObj, getNomorMejaMid);
    
    jobject listObj = env->CallObjectMethod(transaksiObj, getDaftarBelanjaMid);
    if (!listObj) {
        return env->NewStringUTF("Error: Daftar belanja null");
    }

    jclass listClass = env->GetObjectClass(listObj);
    jmethodID sizeMid = env->GetMethodID(listClass, "size", "()I");
    jmethodID getMid = env->GetMethodID(listClass, "get", "(I)Ljava/lang/Object;");
    
    jint listSize = env->CallIntMethod(listObj, sizeMid);
    
    double subtotal = 0;
    std::stringstream itemDetails;

    for (int i = 0; i < listSize; ++i) {
        jobject itemObj = env->CallObjectMethod(listObj, getMid, i);
        if (!itemObj) continue;

        jclass itemClass = env->GetObjectClass(itemObj);
        jmethodID getMenuMid = env->GetMethodID(itemClass, "getMenu", "()Lgacoan/Menu;");
        jmethodID getQtyMid = env->GetMethodID(itemClass, "getKuantitas", "()I");
        jmethodID getLvlMid = env->GetMethodID(itemClass, "getLevelPedas", "()I");
        jmethodID getCatatanMid = env->GetMethodID(itemClass, "getCatatan", "()Ljava/lang/String;");

        jobject menuObj = env->CallObjectMethod(itemObj, getMenuMid);
        jint qty = env->CallIntMethod(itemObj, getQtyMid);
        jint lvl = env->CallIntMethod(itemObj, getLvlMid);
        jstring catJStr = (jstring)env->CallObjectMethod(itemObj, getCatatanMid);
        std::string catatan = jstringToString(env, catJStr);

        if (!menuObj) {
            env->DeleteLocalRef(itemObj);
            continue;
        }

        jclass menuClass = env->GetObjectClass(menuObj);
        jmethodID getMenuNamaMid = env->GetMethodID(menuClass, "getNama", "()Ljava/lang/String;");
        jmethodID getMenuHargaMid = env->GetMethodID(menuClass, "getHargaDasar", "()D");
        jmethodID getMenuKategoriMid = env->GetMethodID(menuClass, "getKategori", "()Ljava/lang/String;");

        jstring namaJStr = (jstring)env->CallObjectMethod(menuObj, getMenuNamaMid);
        std::string nama = jstringToString(env, namaJStr);
        double hargaDasar = env->CallDoubleMethod(menuObj, getMenuHargaMid);
        jstring katJStr = (jstring)env->CallObjectMethod(menuObj, getMenuKategoriMid);
        std::string kategori = jstringToString(env, katJStr);

        double calculatedPrice = hargaDasar;
        if (kategori == "Makanan") {
            if (lvl >= 1 && lvl <= 4) {
                calculatedPrice = 11000;
            } else if (lvl >= 5 && lvl <= 8) {
                calculatedPrice = 13000;
            } else if (lvl == 0) {
                calculatedPrice = 11000;
            }
        } else if (kategori == "Dimsum") {
            calculatedPrice = 10000;
        } else if (kategori == "Minuman") {
            calculatedPrice = 9000;
        }

        double itemSubtotal = qty * calculatedPrice;
        subtotal += itemSubtotal;

        itemDetails << " " << nama;
        if (kategori == "Makanan") {
            itemDetails << " (Lvl " << lvl << ")";
        }
        itemDetails << "\n";
        itemDetails << "   " << qty << " x " << formatRupiah(calculatedPrice) 
                    << "                      " << formatRupiah(itemSubtotal) << "\n";
        if (!catatan.empty()) {
            itemDetails << "   *Catatan: " << catatan << "\n";
        }
        itemDetails << "\n";

        env->DeleteLocalRef(namaJStr);
        env->DeleteLocalRef(katJStr);
        env->DeleteLocalRef(menuClass);
        env->DeleteLocalRef(menuObj);
        env->DeleteLocalRef(catJStr);
        env->DeleteLocalRef(itemClass);
        env->DeleteLocalRef(itemObj);
    }

    double pb1 = subtotal * 0.10;
    double totalAkhir = subtotal + pb1;

    std::stringstream receipt;
    receipt << "================================================\n";
    receipt << "                  MIE GACOAN                    \n";
    receipt << "           SISTEM PEMESANAN MANDIRI             \n";
    receipt << "================================================\n";
    receipt << " ID NOTA : " << idNota << "\n";
    receipt << " MEJA    : " << nomorMeja << "\n";
    receipt << "------------------------------------------------\n";
    receipt << itemDetails.str();
    receipt << "------------------------------------------------\n";
    receipt << " Subtotal            :        " << formatRupiah(subtotal) << "\n";
    receipt << " Pajak Resto (PB1 10%):       " << formatRupiah(pb1) << "\n";
    receipt << " Biaya Layanan       :        Rp 0\n";
    receipt << "------------------------------------------------\n";
    receipt << " TOTAL AKHIR         :        " << formatRupiah(totalAkhir) << "\n";
    receipt << "================================================\n";
    receipt << "      Terima kasih atas pesanan Anda!           \n";
    receipt << "  Silakan monitor KDS dapur untuk pengambilan.  \n";
    receipt << "================================================\n";

    env->DeleteLocalRef(listClass);
    env->DeleteLocalRef(listObj);
    env->DeleteLocalRef(transClass);
    env->DeleteLocalRef(idNotaJStr);

    return env->NewStringUTF(receipt.str().c_str());
}

JNIEXPORT void JNICALL Java_gacoan_SistemNotifikasi_panggilAntrean(JNIEnv *env, jclass clazz, jint nomorMeja) {
    std::wstring wavPath = L"audio/meja_" + std::to_wstring(nomorMeja) + L".wav";
    DWORD attrib = GetFileAttributesW(wavPath.c_str());
    if (attrib != INVALID_FILE_ATTRIBUTES && !(attrib & FILE_ATTRIBUTE_DIRECTORY)) {
        PlaySoundW(wavPath.c_str(), NULL, SND_FILENAME | SND_SYNC);
        return;
    }

    ISpVoice * pVoice = NULL;

    if (FAILED(::CoInitialize(NULL))) {
        std::cerr << "[JNI-TTS] Failed to initialize COM" << std::endl;
        return;
    }

    HRESULT hr = CoCreateInstance(CLSID_SpVoice, NULL, CLSCTX_ALL, IID_ISpVoice, (void **)&pVoice);
    if (SUCCEEDED(hr)) {
        std::wstring text = L"Pesanan untuk meja nomor " + std::to_wstring(nomorMeja) + L", silakan ambil.";
        
        pVoice->Speak(text.c_str(), 0, NULL);
        pVoice->Release();
        pVoice = NULL;
    } else {
        std::cerr << "[JNI-TTS] Failed to create SpVoice instance: " << hr << std::endl;
    }

    ::CoUninitialize();
}
