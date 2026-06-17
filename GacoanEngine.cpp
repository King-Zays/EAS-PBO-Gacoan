#define INITGUID
#include <windows.h>
#include <mmsystem.h>
#include <stdarg.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include "gacoan_GacoanEngine.h"
#include "gacoan_SistemNotifikasi.h"

static void appendf(char* buffer, size_t capacity, size_t* length, const char* format, ...) {
    if (*length >= capacity) {
        return;
    }

    va_list args;
    va_start(args, format);
    int written = vsnprintf(buffer + *length, capacity - *length, format, args);
    va_end(args);

    if (written < 0) {
        return;
    }

    if ((size_t)written >= capacity - *length) {
        *length = capacity - 1;
        buffer[*length] = '\0';
    } else {
        *length += (size_t)written;
    }
}

static void formatRupiah(double amount, char* output, size_t outputSize) {
    long long value = (long long)amount;
    char digits[32];
    snprintf(digits, sizeof(digits), "%lld", value);

    size_t digitLen = strlen(digits);
    size_t pos = 0;
    int firstGroup = (int)(digitLen % 3);
    if (firstGroup == 0) {
        firstGroup = 3;
    }

    pos += snprintf(output + pos, outputSize - pos, "Rp ");
    for (size_t i = 0; i < digitLen && pos + 1 < outputSize; ++i) {
        if (i > 0 && ((int)i - firstGroup) % 3 == 0) {
            output[pos++] = '.';
        }
        output[pos++] = digits[i];
    }
    output[pos] = '\0';
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
    const char* idNotaChars = NULL;
    const char* idNota = "";
    if (idNotaJStr) {
        idNotaChars = env->GetStringUTFChars(idNotaJStr, NULL);
        if (idNotaChars) {
            idNota = idNotaChars;
        }
    }

    jint nomorMeja = env->CallIntMethod(transaksiObj, getNomorMejaMid);

    jobject listObj = env->CallObjectMethod(transaksiObj, getDaftarBelanjaMid);
    if (!listObj) {
        if (idNotaChars) env->ReleaseStringUTFChars(idNotaJStr, idNotaChars);
        if (idNotaJStr) env->DeleteLocalRef(idNotaJStr);
        env->DeleteLocalRef(transClass);
        return env->NewStringUTF("Error: Daftar belanja null");
    }

    jclass listClass = env->GetObjectClass(listObj);
    jmethodID sizeMid = env->GetMethodID(listClass, "size", "()I");
    jmethodID getMid = env->GetMethodID(listClass, "get", "(I)Ljava/lang/Object;");

    jint listSize = env->CallIntMethod(listObj, sizeMid);

    double subtotal = 0;
    char itemDetails[24576];
    size_t itemLen = 0;
    itemDetails[0] = '\0';

    for (int i = 0; i < listSize; ++i) {
        jobject itemObj = env->CallObjectMethod(listObj, getMid, i);
        if (!itemObj) {
            continue;
        }

        jclass itemClass = env->GetObjectClass(itemObj);
        jmethodID getMenuMid = env->GetMethodID(itemClass, "getMenu", "()Lgacoan/Menu;");
        jmethodID getQtyMid = env->GetMethodID(itemClass, "getKuantitas", "()I");
        jmethodID getLvlMid = env->GetMethodID(itemClass, "getLevelPedas", "()I");
        jmethodID getCatatanMid = env->GetMethodID(itemClass, "getCatatan", "()Ljava/lang/String;");

        jobject menuObj = env->CallObjectMethod(itemObj, getMenuMid);
        jint qty = env->CallIntMethod(itemObj, getQtyMid);
        jint lvl = env->CallIntMethod(itemObj, getLvlMid);

        if (!menuObj) {
            env->DeleteLocalRef(itemClass);
            env->DeleteLocalRef(itemObj);
            continue;
        }

        jclass menuClass = env->GetObjectClass(menuObj);
        jmethodID getMenuNamaMid = env->GetMethodID(menuClass, "getNama", "()Ljava/lang/String;");
        jmethodID getMenuHargaMid = env->GetMethodID(menuClass, "getHargaDasar", "()D");
        jmethodID getMenuKategoriMid = env->GetMethodID(menuClass, "getKategori", "()Ljava/lang/String;");

        jstring namaJStr = (jstring)env->CallObjectMethod(menuObj, getMenuNamaMid);
        jstring katJStr = (jstring)env->CallObjectMethod(menuObj, getMenuKategoriMid);
        jstring catJStr = (jstring)env->CallObjectMethod(itemObj, getCatatanMid);

        const char* namaChars = namaJStr ? env->GetStringUTFChars(namaJStr, NULL) : NULL;
        const char* kategoriChars = katJStr ? env->GetStringUTFChars(katJStr, NULL) : NULL;
        const char* catatanChars = catJStr ? env->GetStringUTFChars(catJStr, NULL) : NULL;

        const char* nama = namaChars ? namaChars : "";
        const char* kategori = kategoriChars ? kategoriChars : "";
        const char* catatan = catatanChars ? catatanChars : "";

        double hargaDasar = env->CallDoubleMethod(menuObj, getMenuHargaMid);
        double calculatedPrice = hargaDasar;

        if (strcmp(kategori, "Makanan") == 0) {
            if (lvl >= 5 && lvl <= 8) {
                calculatedPrice = 13000;
            } else {
                calculatedPrice = 11000;
            }
        } else if (strcmp(kategori, "Dimsum") == 0) {
            calculatedPrice = 10000;
        } else if (strcmp(kategori, "Minuman") == 0) {
            calculatedPrice = 9000;
        }

        double itemSubtotal = qty * calculatedPrice;
        subtotal += itemSubtotal;

        char priceText[64];
        char itemSubtotalText[64];
        formatRupiah(calculatedPrice, priceText, sizeof(priceText));
        formatRupiah(itemSubtotal, itemSubtotalText, sizeof(itemSubtotalText));

        appendf(itemDetails, sizeof(itemDetails), &itemLen, " %s", nama);
        if (strcmp(kategori, "Makanan") == 0) {
            appendf(itemDetails, sizeof(itemDetails), &itemLen, " (Lvl %d)", (int)lvl);
        }
        appendf(itemDetails, sizeof(itemDetails), &itemLen, "\n");
        appendf(itemDetails, sizeof(itemDetails), &itemLen,
                "   %d x %s                      %s\n", (int)qty, priceText, itemSubtotalText);
        if (catatan[0] != '\0') {
            appendf(itemDetails, sizeof(itemDetails), &itemLen, "   *Catatan: %s\n", catatan);
        }
        appendf(itemDetails, sizeof(itemDetails), &itemLen, "\n");

        if (namaChars) env->ReleaseStringUTFChars(namaJStr, namaChars);
        if (kategoriChars) env->ReleaseStringUTFChars(katJStr, kategoriChars);
        if (catatanChars) env->ReleaseStringUTFChars(catJStr, catatanChars);

        if (namaJStr) env->DeleteLocalRef(namaJStr);
        if (katJStr) env->DeleteLocalRef(katJStr);
        if (catJStr) env->DeleteLocalRef(catJStr);
        env->DeleteLocalRef(menuClass);
        env->DeleteLocalRef(menuObj);
        env->DeleteLocalRef(itemClass);
        env->DeleteLocalRef(itemObj);
    }

    double pb1 = subtotal * 0.10;
    double totalAkhir = subtotal + pb1;

    char subtotalText[64];
    char pb1Text[64];
    char totalText[64];
    formatRupiah(subtotal, subtotalText, sizeof(subtotalText));
    formatRupiah(pb1, pb1Text, sizeof(pb1Text));
    formatRupiah(totalAkhir, totalText, sizeof(totalText));

    char receipt[32768];
    size_t receiptLen = 0;
    receipt[0] = '\0';

    appendf(receipt, sizeof(receipt), &receiptLen, "================================================\n");
    appendf(receipt, sizeof(receipt), &receiptLen, "                  MIE GACOAN                    \n");
    appendf(receipt, sizeof(receipt), &receiptLen, "           SISTEM PEMESANAN MANDIRI             \n");
    appendf(receipt, sizeof(receipt), &receiptLen, "================================================\n");
    appendf(receipt, sizeof(receipt), &receiptLen, " ID NOTA : %s\n", idNota);
    appendf(receipt, sizeof(receipt), &receiptLen, " MEJA    : %d\n", (int)nomorMeja);
    appendf(receipt, sizeof(receipt), &receiptLen, "------------------------------------------------\n");
    appendf(receipt, sizeof(receipt), &receiptLen, "%s", itemDetails);
    appendf(receipt, sizeof(receipt), &receiptLen, "------------------------------------------------\n");
    appendf(receipt, sizeof(receipt), &receiptLen, " Subtotal            :        %s\n", subtotalText);
    appendf(receipt, sizeof(receipt), &receiptLen, " Pajak Resto (PB1 10%%):       %s\n", pb1Text);
    appendf(receipt, sizeof(receipt), &receiptLen, " Biaya Layanan       :        Rp 0\n");
    appendf(receipt, sizeof(receipt), &receiptLen, "------------------------------------------------\n");
    appendf(receipt, sizeof(receipt), &receiptLen, " TOTAL AKHIR         :        %s\n", totalText);
    appendf(receipt, sizeof(receipt), &receiptLen, "================================================\n");
    appendf(receipt, sizeof(receipt), &receiptLen, "      Terima kasih atas pesanan Anda!           \n");
    appendf(receipt, sizeof(receipt), &receiptLen, "  Silakan monitor KDS dapur untuk pengambilan.  \n");
    appendf(receipt, sizeof(receipt), &receiptLen, "================================================\n");

    if (idNotaChars) env->ReleaseStringUTFChars(idNotaJStr, idNotaChars);
    if (idNotaJStr) env->DeleteLocalRef(idNotaJStr);
    env->DeleteLocalRef(listClass);
    env->DeleteLocalRef(listObj);
    env->DeleteLocalRef(transClass);

    return env->NewStringUTF(receipt);
}

JNIEXPORT void JNICALL Java_gacoan_SistemNotifikasi_panggilAntrean(JNIEnv *env, jclass clazz, jint nomorMeja) {
    wchar_t wavPath[MAX_PATH];
    swprintf(wavPath, MAX_PATH, L"audio/meja_%d.wav", (int)nomorMeja);

    DWORD attrib = GetFileAttributesW(wavPath);
    if (attrib != INVALID_FILE_ATTRIBUTES && !(attrib & FILE_ATTRIBUTE_DIRECTORY)) {
        PlaySoundW(wavPath, NULL, SND_FILENAME | SND_SYNC);
        return;
    }

    char command[512];
    snprintf(command, sizeof(command),
             "powershell -NoProfile -ExecutionPolicy Bypass -Command \""
             "Add-Type -AssemblyName System.Speech; "
             "$speak = New-Object System.Speech.Synthesis.SpeechSynthesizer; "
             "$speak.Speak('Pesanan untuk meja nomor %d, silakan ambil.');\"",
             (int)nomorMeja);

    int exitCode = system(command);
    if (exitCode != 0) {
        OutputDebugStringA("[JNI-TTS] PowerShell TTS failed.\n");
    }
}
