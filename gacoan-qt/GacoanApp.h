#ifndef GACOANAPP_H
#define GACOANAPP_H

#include <QMainWindow>
#include <QStackedWidget>
#include <QVBoxLayout>
#include <QHBoxLayout>
#include <QGridLayout>
#include <QLabel>
#include <QPushButton>
#include <QLineEdit>
#include <QComboBox>
#include <QTableWidget>
#include <QScrollArea>
#include <QTextEdit>
#include <QDialog>
#include <QFrame>
#include <QGraphicsDropShadowEffect>
#include <QVector>
#include <QRandomGenerator>

#include "Menu.h"
#include "ItemPesanan.h"
#include "TransaksiPesanan.h"
#include "GacoanEngine.h"

class GacoanApp : public QMainWindow {
    Q_OBJECT

public:
    explicit GacoanApp(QWidget* parent = nullptr);
    ~GacoanApp() = default;

private:
    int nomorMejaLocked = 0;
    QVector<Menu> daftarMenu;
    QVector<ItemPesanan> keranjangBelanja;
    QVector<TransaksiPesanan> antreanDapur;
    QVector<TransaksiPesanan> riwayatPanggilan;
    GacoanEngine billingEngine;
    QString currentCategoryFilter = "Semua";

    QStackedWidget* mainStack = nullptr;
    QStackedWidget* appTabStack = nullptr;

    QPushButton* btnTabPelanggan = nullptr;
    QPushButton* btnTabKds = nullptr;

    QLabel* lblTableStatus = nullptr;
    QPushButton* btnScanQr = nullptr;

    QTableWidget* tableKeranjang = nullptr;
    QLabel* lblSubtotal = nullptr;
    QLabel* lblTax = nullptr;
    QLabel* lblTotal = nullptr;

    QWidget* panelGridMenu = nullptr;
    QGridLayout* gridMenuLayout = nullptr;
    QWidget* gridMenuContainer = nullptr;

    QWidget* panelKdsActiveQueue = nullptr;
    QVBoxLayout* layoutKdsActive = nullptr;
    QWidget* panelKdsHistoryQueue = nullptr;
    QVBoxLayout* layoutKdsHistory = nullptr;

    void initIconicMenus();
    QWidget* buildSplashScreen();
    QWidget* buildMainAppScreen();
    QWidget* buildCustomerOrderingTab();
    QWidget* buildKitchenTab();

    void renderModernCatalogGrid();
    void updateCartUI();
    void updateCartSummary();
    void refreshKdsViews();
    void showAnimatedReceiptDialog(const QString& formatStr);
    QString generateNota(const TransaksiPesanan& trans);

    static QFrame* createCard();
    static QGraphicsDropShadowEffect* createShadow();
    static QPushButton* createPillButton(const QString& text, const QString& styleClass = "pillBtn");
    QWidget* buildDiagnosticRow(const QString& title, const QString& status, bool isOk);
    QWidget* buildKdsPlaceholder(const QString& text);
    QWidget* buildOrderQueueCard(const TransaksiPesanan& trans, bool isActive);
};

#endif
