#ifndef MENU_H
#define MENU_H

#include <QString>

class Menu {
public:
    Menu() : hargaDasar(0) {}

    Menu(const QString& id, const QString& nama, double hargaDasar, const QString& kategori)
        : id(id), nama(nama), hargaDasar(hargaDasar), kategori(kategori) {}

    QString getId() const { return id; }
    QString getNama() const { return nama; }
    double getHargaDasar() const { return hargaDasar; }
    QString getKategori() const { return kategori; }

    void setId(const QString& id) { this->id = id; }
    void setNama(const QString& nama) { this->nama = nama; }
    void setHargaDasar(double hargaDasar) { this->hargaDasar = hargaDasar; }
    void setKategori(const QString& kategori) { this->kategori = kategori; }

private:
    QString id;
    QString nama;
    double hargaDasar;
    QString kategori;
};

#endif
