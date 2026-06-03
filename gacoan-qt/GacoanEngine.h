#ifndef GACOANENGINE_H
#define GACOANENGINE_H

#include <QString>
#include "TransaksiPesanan.h"

class GacoanEngine {
public:
    GacoanEngine() = default;

    
    QString hitungNota(const TransaksiPesanan& transaksi) const;

    
    static void panggilAntrean(int nomorMeja);

private:
    static QString formatRupiah(double amount);
};

#endif
