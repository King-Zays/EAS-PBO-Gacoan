#ifndef ITEMPESANAN_H
#define ITEMPESANAN_H

#include <QString>
#include <memory>
#include "Menu.h"

class ItemPesanan {
public:
    ItemPesanan()
        : kuantitas(0), levelPedas(0) {}

    ItemPesanan(const Menu& menu, int kuantitas, int levelPedas, const QString& catatan)
        : menu(menu), kuantitas(kuantitas), levelPedas(levelPedas),
          catatan(catatan.isNull() ? "" : catatan) {}

    Menu getMenu() const { return menu; }
    Menu& getMenuRef() { return menu; }
    int getKuantitas() const { return kuantitas; }
    int getLevelPedas() const { return levelPedas; }
    QString getCatatan() const { return catatan; }

    void setMenu(const Menu& menu) { this->menu = menu; }
    void setKuantitas(int kuantitas) { this->kuantitas = kuantitas; }
    void setLevelPedas(int levelPedas) { this->levelPedas = levelPedas; }
    void setCatatan(const QString& catatan) { this->catatan = catatan; }

    QString getSpicyLevelName() const {
        if (menu.getKategori().compare("Makanan", Qt::CaseInsensitive) == 0) {
            if (levelPedas == 0) return "Original (Lvl 0)";
            return QString("Pedas (Lvl %1)").arg(levelPedas);
        }
        return "";
    }

private:
    Menu menu;
    int kuantitas;
    int levelPedas;
    QString catatan;
};

#endif
