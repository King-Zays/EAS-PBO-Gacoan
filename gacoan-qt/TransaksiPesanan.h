#ifndef TRANSAKSIPESANAN_H
#define TRANSAKSIPESANAN_H

#include <QString>
#include <QVector>
#include "ItemPesanan.h"

class TransaksiPesanan {
public:
    TransaksiPesanan()
        : nomorMeja(0), status("PENDING") {}

    TransaksiPesanan(const QString& idNota, int nomorMeja)
        : idNota(idNota), nomorMeja(nomorMeja), status("PENDING") {}

    QString getIdNota() const { return idNota; }
    int getNomorMeja() const { return nomorMeja; }
    QVector<ItemPesanan>& getDaftarBelanja() { return daftarBelanja; }
    const QVector<ItemPesanan>& getDaftarBelanja() const { return daftarBelanja; }
    QString getStatus() const { return status; }

    void setIdNota(const QString& idNota) { this->idNota = idNota; }
    void setNomorMeja(int nomorMeja) { this->nomorMeja = nomorMeja; }
    void setDaftarBelanja(const QVector<ItemPesanan>& daftarBelanja) { this->daftarBelanja = daftarBelanja; }
    void setStatus(const QString& status) { this->status = status; }

    void tambahItem(const ItemPesanan& item) {
        daftarBelanja.append(item);
    }

private:
    QString idNota;
    int nomorMeja;
    QVector<ItemPesanan> daftarBelanja;
    QString status;
};

#endif
