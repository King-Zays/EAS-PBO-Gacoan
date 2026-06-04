#include "GacoanApp.h"

#include <QApplication>
#include <QScrollBar>
#include <QHeaderView>
#include <QMessageBox>
#include <QDateTime>
#include <QThread>
#include <QSpacerItem>
#include <QTimer>
#include <QFont>
#include <QStyle>
#include <thread>

GacoanApp::GacoanApp(QWidget* parent)
    : QMainWindow(parent)
{
    initIconicMenus();

    setWindowTitle("MIE GACOAN - Self Ordering Kiosk & Kitchen Display System");
    resize(1240, 820);
    setMinimumSize(1024, 768);

    QWidget* central = new QWidget(this);
    central->setObjectName("centralWidget");
    setCentralWidget(central);

    QVBoxLayout* rootLayout = new QVBoxLayout(central);
    rootLayout->setContentsMargins(0, 0, 0, 0);
    rootLayout->setSpacing(0);

    mainStack = new QStackedWidget();
    rootLayout->addWidget(mainStack);

    mainStack->addWidget(buildSplashScreen());
    mainStack->addWidget(buildMainAppScreen());

    mainStack->setCurrentIndex(0);
}

void GacoanApp::initIconicMenus() {
    daftarMenu.append(Menu("MIE_HOMPIMPA", "Mie Hompimpa", 11000, "Makanan"));
    daftarMenu.append(Menu("MIE_GACOAN", "Mie Gacoan", 11000, "Makanan"));
    daftarMenu.append(Menu("DIMSUM_SIOMAY", "Dimsum Siomay", 10000, "Dimsum"));
    daftarMenu.append(Menu("DIMSUM_RAMBUTAN", "Udang Rambutan", 10000, "Dimsum"));
    daftarMenu.append(Menu("ES_GOBAC_SODOR", "Es Gobak Sodor", 9000, "Minuman"));
    daftarMenu.append(Menu("ES_TEKLEK", "Es Teklek", 9000, "Minuman"));
}

QFrame* GacoanApp::createCard() {
    QFrame* card = new QFrame();
    card->setProperty("class", "card");
    card->setGraphicsEffect(createShadow());
    return card;
}

QGraphicsDropShadowEffect* GacoanApp::createShadow() {
    auto* shadow = new QGraphicsDropShadowEffect();
    shadow->setBlurRadius(24);
    shadow->setOffset(0, 4);
    shadow->setColor(QColor(0, 0, 0, 15));
    return shadow;
}

QPushButton* GacoanApp::createPillButton(const QString& text, const QString& styleClass) {
    QPushButton* btn = new QPushButton(text);
    btn->setProperty("class", styleClass);
    btn->setCursor(Qt::PointingHandCursor);
    btn->setFixedHeight(36);
    return btn;
}

QWidget* GacoanApp::buildSplashScreen() {
    QWidget* wrapper = new QWidget();
    wrapper->setObjectName("splashWrapper");

    QVBoxLayout* outerLayout = new QVBoxLayout(wrapper);
    outerLayout->setAlignment(Qt::AlignCenter);

    QFrame* card = new QFrame();
    card->setObjectName("splashCard");
    card->setFixedSize(640, 520);
    card->setGraphicsEffect(createShadow());

    QVBoxLayout* boxLayout = new QVBoxLayout(card);
    boxLayout->setContentsMargins(45, 40, 45, 40);
    boxLayout->setSpacing(0);
    boxLayout->setAlignment(Qt::AlignCenter);

    QLabel* logo = new QLabel("MIE GACOAN");
    logo->setObjectName("splashLogo");
    logo->setAlignment(Qt::AlignCenter);
    boxLayout->addWidget(logo);

    QLabel* subtext = new QLabel("Sistem Self-Ordering & KDS - C++ Qt Edition");
    subtext->setObjectName("splashSubtext");
    subtext->setAlignment(Qt::AlignCenter);
    boxLayout->addSpacing(5);
    boxLayout->addWidget(subtext);

    boxLayout->addSpacing(30);

    boxLayout->addWidget(buildDiagnosticRow("C++ Qt Widgets Runtime",
                         QString("ACTIVE (Qt %1)").arg(QT_VERSION_STR), true));
    boxLayout->addSpacing(16);
    boxLayout->addWidget(buildDiagnosticRow("GacoanEngine (Native C++)",
                         "LINKED SUCCESSFULLY", true));
    boxLayout->addSpacing(16);
    boxLayout->addWidget(buildDiagnosticRow("Windows SAPI TTS Engine",
#ifdef _WIN32
                         "AVAILABLE", true));
#else
                         "NOT AVAILABLE (Non-Windows)", false));
#endif

    boxLayout->addSpacing(35);

    QPushButton* btnGo = createPillButton("BUKA APLIKASI UTAMA", "pillBtnGreen");
    btnGo->setFixedSize(500, 48);
    btnGo->setFont(QFont("Segoe UI", 14, QFont::Bold));
    connect(btnGo, &QPushButton::clicked, this, [this]() {
        mainStack->setCurrentIndex(1);
    });
    boxLayout->addWidget(btnGo, 0, Qt::AlignCenter);

    boxLayout->addSpacing(20);

    QLabel* footer = new QLabel("EAS Pemrograman Berorientasi Objek - Teknik Informatika");
    footer->setProperty("class", "textSecondary");
    footer->setFont(QFont("Segoe UI", 11));
    footer->setAlignment(Qt::AlignCenter);
    boxLayout->addWidget(footer);

    outerLayout->addWidget(card);
    return wrapper;
}

QWidget* GacoanApp::buildDiagnosticRow(const QString& title, const QString& status, bool isOk) {
    QFrame* row = new QFrame();
    row->setProperty("class", "diagCard");
    row->setFixedHeight(52);
    row->setMaximumWidth(540);

    QHBoxLayout* hl = new QHBoxLayout(row);
    hl->setContentsMargins(18, 0, 18, 0);

    QLabel* lblTitle = new QLabel(title);
    lblTitle->setFont(QFont("Segoe UI Semibold", 13, QFont::Bold));
    lblTitle->setProperty("class", "textPrimary");
    hl->addWidget(lblTitle);

    hl->addStretch();

    QLabel* lblStatus = new QLabel(status);
    lblStatus->setFont(QFont("Segoe UI", 12, QFont::Bold));
    lblStatus->setStyleSheet(QString("color: %1;").arg(isOk ? "#22C55E" : "#EA580C"));
    hl->addWidget(lblStatus);

    QLabel* dot = new QLabel("●");
    dot->setFont(QFont("Segoe UI", 10));
    dot->setStyleSheet(QString("color: %1;").arg(isOk ? "#22C55E" : "#EA580C"));
    hl->addWidget(dot);

    return row;
}

QWidget* GacoanApp::buildMainAppScreen() {
    QWidget* workspace = new QWidget();
    QVBoxLayout* wl = new QVBoxLayout(workspace);
    wl->setContentsMargins(0, 0, 0, 0);
    wl->setSpacing(0);

    QWidget* navBar = new QWidget();
    navBar->setObjectName("navBar");
    navBar->setFixedHeight(68);
    QHBoxLayout* navLayout = new QHBoxLayout(navBar);
    navLayout->setContentsMargins(24, 0, 24, 0);

    QLabel* appLogo = new QLabel("MIE GACOAN");
    appLogo->setObjectName("appLogo");
    navLayout->addWidget(appLogo);
    navLayout->addStretch();

    btnTabPelanggan = new QPushButton("PESAN MANDIRI");
    btnTabPelanggan->setObjectName("navBtnActive");
    btnTabPelanggan->setCursor(Qt::PointingHandCursor);
    btnTabPelanggan->setFixedSize(140, 36);
    navLayout->addWidget(btnTabPelanggan);

    btnTabKds = new QPushButton("KITCHEN DISPLAY (KDS)");
    btnTabKds->setObjectName("navBtnInactive");
    btnTabKds->setCursor(Qt::PointingHandCursor);
    btnTabKds->setFixedSize(180, 36);
    navLayout->addWidget(btnTabKds);

    wl->addWidget(navBar);

    appTabStack = new QStackedWidget();
    appTabStack->addWidget(buildCustomerOrderingTab());
    appTabStack->addWidget(buildKitchenTab());
    wl->addWidget(appTabStack);

    connect(btnTabPelanggan, &QPushButton::clicked, this, [this]() {
        appTabStack->setCurrentIndex(0);
        btnTabPelanggan->setObjectName("navBtnActive");
        btnTabKds->setObjectName("navBtnInactive");
        btnTabPelanggan->style()->unpolish(btnTabPelanggan);
        btnTabPelanggan->style()->polish(btnTabPelanggan);
        btnTabKds->style()->unpolish(btnTabKds);
        btnTabKds->style()->polish(btnTabKds);
    });

    connect(btnTabKds, &QPushButton::clicked, this, [this]() {
        appTabStack->setCurrentIndex(1);
        btnTabKds->setObjectName("navBtnActive");
        btnTabPelanggan->setObjectName("navBtnInactive");
        btnTabPelanggan->style()->unpolish(btnTabPelanggan);
        btnTabPelanggan->style()->polish(btnTabPelanggan);
        btnTabKds->style()->unpolish(btnTabKds);
        btnTabKds->style()->polish(btnTabKds);
    });

    return workspace;
}

QWidget* GacoanApp::buildCustomerOrderingTab() {
    QWidget* tab = new QWidget();
    tab->setObjectName("customerTab");
    QHBoxLayout* tabLayout = new QHBoxLayout(tab);
    tabLayout->setContentsMargins(24, 20, 24, 20);
    tabLayout->setSpacing(20);

    QWidget* leftSide = new QWidget();
    QVBoxLayout* leftLayout = new QVBoxLayout(leftSide);
    leftLayout->setContentsMargins(0, 0, 0, 0);
    leftLayout->setSpacing(15);

    QFrame* qrCard = createCard();
    QHBoxLayout* qrLayout = new QHBoxLayout(qrCard);
    qrLayout->setContentsMargins(20, 16, 20, 16);

    lblTableStatus = new QLabel("SILAKAN SCAN BARCODE QR PADA MEJA KIOSK");
    lblTableStatus->setFont(QFont("Segoe UI Semibold", 13, QFont::Bold));
    lblTableStatus->setProperty("class", "textSecondary");
    qrLayout->addWidget(lblTableStatus);
    qrLayout->addStretch();

    btnScanQr = createPillButton("PINDAI BARCODE QR MEJA");
    btnScanQr->setFixedSize(200, 34);
    connect(btnScanQr, &QPushButton::clicked, this, [this]() {
        nomorMejaLocked = QRandomGenerator::global()->bounded(1, 26);
        lblTableStatus->setText(QString("QR TERVERIFIKASI MEJA: MEJA %1 (TERKUNCI ✓)").arg(nomorMejaLocked));
        lblTableStatus->setStyleSheet("color: #22C55E; font-weight: bold;");
        btnScanQr->setEnabled(false);
        btnScanQr->setText("QR TERVERIFIKASI");
        btnScanQr->setProperty("class", "pillBtnGhost");
        btnScanQr->style()->unpolish(btnScanQr);
        btnScanQr->style()->polish(btnScanQr);
    });
    qrLayout->addWidget(btnScanQr);
    leftLayout->addWidget(qrCard);

    QWidget* filterRow = new QWidget();
    QHBoxLayout* filterLayout = new QHBoxLayout(filterRow);
    filterLayout->setContentsMargins(0, 0, 0, 0);
    filterLayout->setSpacing(10);
    filterLayout->setAlignment(Qt::AlignLeft);

    QStringList cats = {"Semua", "Makanan", "Dimsum", "Minuman"};
    for (const auto& cat : cats) {
        QPushButton* btnF = new QPushButton(cat.toUpper());
        btnF->setProperty("class", cat == "Semua" ? "filterBtnActive" : "filterBtn");
        btnF->setCursor(Qt::PointingHandCursor);
        btnF->setFixedSize(110, 32);
        connect(btnF, &QPushButton::clicked, this, [this, cat, filterLayout]() {
            currentCategoryFilter = cat;
            for (int i = 0; i < filterLayout->count(); ++i) {
                if (auto* btn = qobject_cast<QPushButton*>(filterLayout->itemAt(i)->widget())) {
                    bool isActive = (btn->text() == cat.toUpper());
                    btn->setProperty("class", isActive ? "filterBtnActive" : "filterBtn");
                    btn->style()->unpolish(btn);
                    btn->style()->polish(btn);
                }
            }
            renderModernCatalogGrid();
        });
        filterLayout->addWidget(btnF);
    }
    filterLayout->addStretch();
    leftLayout->addWidget(filterRow);

    QScrollArea* catalogScroll = new QScrollArea();
    catalogScroll->setWidgetResizable(true);
    catalogScroll->setHorizontalScrollBarPolicy(Qt::ScrollBarAlwaysOff);
    catalogScroll->setFrameShape(QFrame::NoFrame);

    gridMenuContainer = new QWidget();
    gridMenuLayout = new QGridLayout(gridMenuContainer);
    gridMenuLayout->setSpacing(16);
    gridMenuLayout->setContentsMargins(0, 0, 0, 0);

    catalogScroll->setWidget(gridMenuContainer);
    leftLayout->addWidget(catalogScroll);

    tabLayout->addWidget(leftSide, 1);

    QFrame* cartCard = createCard();
    cartCard->setFixedWidth(420);
    QVBoxLayout* cartLayout = new QVBoxLayout(cartCard);
    cartLayout->setContentsMargins(20, 24, 20, 24);
    cartLayout->setSpacing(20);

    QLabel* lblCartTitle = new QLabel("Detail Keranjang Belanja");
    lblCartTitle->setFont(QFont("Segoe UI Black", 18, QFont::Black));
    lblCartTitle->setProperty("class", "textPrimary");
    cartLayout->addWidget(lblCartTitle);

    tableKeranjang = new QTableWidget(0, 3);
    tableKeranjang->setHorizontalHeaderLabels({"Pesanan", "Porsi", "Harga"});
    tableKeranjang->horizontalHeader()->setStretchLastSection(true);
    tableKeranjang->horizontalHeader()->setSectionResizeMode(0, QHeaderView::Stretch);
    tableKeranjang->horizontalHeader()->setSectionResizeMode(1, QHeaderView::Fixed);
    tableKeranjang->horizontalHeader()->setSectionResizeMode(2, QHeaderView::Fixed);
    tableKeranjang->setColumnWidth(1, 60);
    tableKeranjang->setColumnWidth(2, 100);
    tableKeranjang->verticalHeader()->setVisible(false);
    tableKeranjang->setSelectionBehavior(QAbstractItemView::SelectRows);
    tableKeranjang->setEditTriggers(QAbstractItemView::NoEditTriggers);
    tableKeranjang->setShowGrid(false);
    tableKeranjang->setAlternatingRowColors(false);
    cartLayout->addWidget(tableKeranjang, 1);

    QWidget* totalsWidget = new QWidget();
    QVBoxLayout* totalsLayout = new QVBoxLayout(totalsWidget);
    totalsLayout->setContentsMargins(0, 0, 0, 0);
    totalsLayout->setSpacing(4);

    lblSubtotal = new QLabel("Subtotal: Rp 0");
    lblSubtotal->setFont(QFont("Segoe UI", 13));
    lblSubtotal->setProperty("class", "textSecondary");
    totalsLayout->addWidget(lblSubtotal);

    lblTax = new QLabel("Pajak Restoran (PB1 10%): Rp 0");
    lblTax->setFont(QFont("Segoe UI", 13));
    lblTax->setProperty("class", "textSecondary");
    totalsLayout->addWidget(lblTax);

    totalsLayout->addSpacing(10);

    lblTotal = new QLabel("TOTAL TAGIHAN: Rp 0");
    lblTotal->setProperty("class", "totalLabel");
    totalsLayout->addWidget(lblTotal);

    totalsLayout->addSpacing(16);

    QPushButton* btnCheckout = createPillButton("KONFIRMASI BAYAR & STRUK");
    btnCheckout->setFixedHeight(48);
    btnCheckout->setFont(QFont("Segoe UI", 14, QFont::Bold));
    connect(btnCheckout, &QPushButton::clicked, this, [this]() {
        if (nomorMejaLocked == 0) {
            QMessageBox::warning(this, "QR Scan Required",
                                 "Silakan lakukan simulasi scan meja terlebih dahulu!");
            return;
        }
        if (keranjangBelanja.isEmpty()) {
            QMessageBox::warning(this, "Empty Cart",
                                 "Keranjang belanja masih kosong!");
            return;
        }

        QString timeStamp = QDateTime::currentDateTime().toString("yyyyMMdd-HHmmss");
        QString idNota = "GCN-" + timeStamp;

        TransaksiPesanan transaksi(idNota, nomorMejaLocked);
        for (const auto& it : keranjangBelanja) {
            transaksi.tambahItem(it);
        }

        QString receiptText = billingEngine.hitungNota(transaksi);

        antreanDapur.append(transaksi);

        showAnimatedReceiptDialog(receiptText);

        keranjangBelanja.clear();
        tableKeranjang->setRowCount(0);
        updateCartSummary();

        nomorMejaLocked = 0;
        btnScanQr->setEnabled(true);
        btnScanQr->setText("PINDAI BARCODE QR MEJA");
        btnScanQr->setProperty("class", "pillBtn");
        btnScanQr->style()->unpolish(btnScanQr);
        btnScanQr->style()->polish(btnScanQr);
        lblTableStatus->setText("SILAKAN SCAN BARCODE QR PADA MEJA KIOSK");
        lblTableStatus->setStyleSheet("color: #64748B; font-weight: bold;");

        refreshKdsViews();
    });
    totalsLayout->addWidget(btnCheckout);
    cartLayout->addWidget(totalsWidget);

    tabLayout->addWidget(cartCard);

    renderModernCatalogGrid();

    return tab;
}

void GacoanApp::renderModernCatalogGrid() {
    QLayoutItem* child;
    while ((child = gridMenuLayout->takeAt(0)) != nullptr) {
        if (child->widget()) {
            child->widget()->deleteLater();
        }
        delete child;
    }

    int col = 0, row = 0;

    for (const Menu& item : daftarMenu) {
        if (currentCategoryFilter != "Semua" &&
            item.getKategori().compare(currentCategoryFilter, Qt::CaseInsensitive) != 0) {
            continue;
        }

        QFrame* cardOuter = createCard();
        QVBoxLayout* cardLayout = new QVBoxLayout(cardOuter);
        cardLayout->setContentsMargins(24, 24, 24, 24);
        cardLayout->setSpacing(12);

        QHBoxLayout* topRow = new QHBoxLayout();
        QVBoxLayout* textCol = new QVBoxLayout();
        textCol->setSpacing(4);

        QLabel* name = new QLabel(item.getNama());
        name->setFont(QFont("Segoe UI Black", 17, QFont::Black));
        name->setProperty("class", "textPrimary");
        textCol->addWidget(name);

        QString catClass;
        if (item.getKategori().compare("Makanan", Qt::CaseInsensitive) == 0) catClass = "catPillMakanan";
        else if (item.getKategori().compare("Dimsum", Qt::CaseInsensitive) == 0) catClass = "catPillDimsum";
        else catClass = "catPillMinuman";

        QLabel* lblCat = new QLabel(item.getKategori().toUpper());
        lblCat->setProperty("class", catClass);
        lblCat->setFixedHeight(20);
        lblCat->setSizePolicy(QSizePolicy::Maximum, QSizePolicy::Fixed);
        textCol->addWidget(lblCat);

        topRow->addLayout(textCol);
        topRow->addStretch();

        QString priceText;
        if (item.getKategori().compare("Makanan", Qt::CaseInsensitive) == 0) priceText = "Rp 11K-13K";
        else if (item.getKategori().compare("Dimsum", Qt::CaseInsensitive) == 0) priceText = "Rp 10K";
        else priceText = "Rp 9K";

        QLabel* price = new QLabel(priceText);
        price->setProperty("class", "priceLabel");
        topRow->addWidget(price, 0, Qt::AlignTop);

        cardLayout->addLayout(topRow);

        bool isFood = item.getKategori().compare("Makanan", Qt::CaseInsensitive) == 0;

        QComboBox* cmbLvl = new QComboBox();
        if (isFood) {
            QLabel* lblLvl = new QLabel("Pilih Level Pedas:");
            lblLvl->setFont(QFont("Segoe UI Semibold", 12));
            lblLvl->setProperty("class", "textSecondary");
            cardLayout->addWidget(lblLvl);

            for (int i = 0; i <= 8; ++i) {
                if (i == 0) cmbLvl->addItem("Original (Lvl 0) - Rp 11.000");
                else if (i <= 4) cmbLvl->addItem(QString("Level %1 - Rp 11.000").arg(i));
                else cmbLvl->addItem(QString("Level %1 - Rp 13.000").arg(i));
            }
            cmbLvl->setFixedHeight(36);
            cardLayout->addWidget(cmbLvl);
            cardLayout->addSpacing(6);
        }

        QLabel* lblNotes = new QLabel("Catatan Konsumen:");
        lblNotes->setFont(QFont("Segoe UI Semibold", 12));
        lblNotes->setProperty("class", "textSecondary");
        cardLayout->addWidget(lblNotes);

        QLineEdit* txtNote = new QLineEdit();
        txtNote->setPlaceholderText("Contoh: Tanpa kuah, sendok...");
        txtNote->setFixedHeight(44);
        cardLayout->addWidget(txtNote);

        cardLayout->addSpacing(8);

        QPushButton* btnAdd = createPillButton("TAMBAHKAN KAN");
        btnAdd->setFixedHeight(40);

        const QString itemId = item.getId();
        connect(btnAdd, &QPushButton::clicked, this, [this, itemId, isFood, cmbLvl, txtNote]() {
            int selectedLevel = isFood ? cmbLvl->currentIndex() : 0;
            QString userNote = txtNote->text().trimmed();

            Menu foundMenu;
            for (const auto& m : daftarMenu) {
                if (m.getId() == itemId) {
                    foundMenu = m;
                    break;
                }
            }

            bool duplicate = false;
            for (auto& it : keranjangBelanja) {
                if (it.getMenu().getId() == itemId && it.getLevelPedas() == selectedLevel) {
                    it.setKuantitas(it.getKuantitas() + 1);
                    if (!userNote.isEmpty()) {
                        it.setCatatan(it.getCatatan() + "; " + userNote);
                    }
                    duplicate = true;
                    break;
                }
            }

            if (!duplicate) {
                double finalPrice = foundMenu.getHargaDasar();
                if (isFood) {
                    finalPrice = (selectedLevel >= 5) ? 13000 : 11000;
                } else if (foundMenu.getKategori().compare("Dimsum", Qt::CaseInsensitive) == 0) {
                    finalPrice = 10000;
                } else if (foundMenu.getKategori().compare("Minuman", Qt::CaseInsensitive) == 0) {
                    finalPrice = 9000;
                }

                Menu localMenu(foundMenu.getId(), foundMenu.getNama(), finalPrice, foundMenu.getKategori());
                keranjangBelanja.append(ItemPesanan(localMenu, 1, selectedLevel, userNote));
            }

            txtNote->clear();
            updateCartUI();
        });

        cardLayout->addWidget(btnAdd);

        gridMenuLayout->addWidget(cardOuter, row, col);
        col++;
        if (col >= 2) {
            col = 0;
            row++;
        }
    }

    gridMenuLayout->setRowStretch(row + 1, 1);
}

void GacoanApp::updateCartUI() {
    tableKeranjang->setRowCount(0);
    for (const auto& it : keranjangBelanja) {
        int r = tableKeranjang->rowCount();
        tableKeranjang->insertRow(r);

        QString title = it.getMenu().getNama();
        if (it.getMenu().getKategori().compare("Makanan", Qt::CaseInsensitive) == 0) {
            title += QString(" (Lvl %1)").arg(it.getLevelPedas());
        }
        double itemTotal = it.getKuantitas() * it.getMenu().getHargaDasar();

        tableKeranjang->setItem(r, 0, new QTableWidgetItem(title));
        QTableWidgetItem* qtyItem = new QTableWidgetItem(QString("%1x").arg(it.getKuantitas()));
        qtyItem->setTextAlignment(Qt::AlignCenter);
        tableKeranjang->setItem(r, 1, qtyItem);
        QTableWidgetItem* priceItem = new QTableWidgetItem(QString("Rp %1").arg(static_cast<long long>(itemTotal)));
        priceItem->setTextAlignment(Qt::AlignRight | Qt::AlignVCenter);
        tableKeranjang->setItem(r, 2, priceItem);
    }
    updateCartSummary();
}

void GacoanApp::updateCartSummary() {
    double subtotal = 0;
    for (const auto& it : keranjangBelanja) {
        subtotal += it.getKuantitas() * it.getMenu().getHargaDasar();
    }
    double tax = subtotal * 0.10;
    double total = subtotal + tax;

    lblSubtotal->setText(QString("Subtotal: Rp %1").arg(static_cast<long long>(subtotal)));
    lblTax->setText(QString("Pajak Restoran (PB1 10%): Rp %1").arg(static_cast<long long>(tax)));
    lblTotal->setText(QString("TOTAL TAGIHAN: Rp %1").arg(static_cast<long long>(total)));
}

void GacoanApp::showAnimatedReceiptDialog(const QString& formatStr) {
    QDialog* dialog = new QDialog(this);
    dialog->setWindowTitle("Nota Belanja Resmi Mie Gacoan");
    dialog->setFixedSize(480, 580);
    dialog->setWindowFlags(Qt::Dialog | Qt::FramelessWindowHint);
    dialog->setAttribute(Qt::WA_DeleteOnClose);
    dialog->setAttribute(Qt::WA_TranslucentBackground);

    QFrame* container = new QFrame(dialog);
    container->setObjectName("splashCard");
    container->setGeometry(0, 0, 480, 580);
    container->setGraphicsEffect(createShadow());

    QVBoxLayout* cl = new QVBoxLayout(container);
    cl->setContentsMargins(24, 24, 24, 24);
    cl->setSpacing(15);

    QLabel* title = new QLabel("TRANSAKSI BERHASIL");
    title->setFont(QFont("Segoe UI Black", 18, QFont::Black));
    title->setStyleSheet("color: #22C55E;");
    title->setAlignment(Qt::AlignCenter);
    cl->addWidget(title);

    QTextEdit* tx = new QTextEdit();
    tx->setObjectName("receiptText");
    tx->setPlainText(formatStr);
    tx->setReadOnly(true);
    tx->setFont(QFont("Consolas", 12));
    cl->addWidget(tx, 1);

    QPushButton* btnClose = createPillButton("TUTUP STRUK BELANJA");
    btnClose->setFixedHeight(40);
    connect(btnClose, &QPushButton::clicked, dialog, &QDialog::accept);
    cl->addWidget(btnClose);

    dialog->exec();
}

QString GacoanApp::generateNota(const TransaksiPesanan& trans) {
    return billingEngine.hitungNota(trans);
}

QWidget* GacoanApp::buildKitchenTab() {
    QWidget* tab = new QWidget();
    tab->setObjectName("kitchenTab");
    QHBoxLayout* tabLayout = new QHBoxLayout(tab);
    tabLayout->setContentsMargins(24, 24, 24, 24);
    tabLayout->setSpacing(24);

    QWidget* colActive = new QWidget();
    QVBoxLayout* colActiveLayout = new QVBoxLayout(colActive);
    colActiveLayout->setContentsMargins(0, 0, 0, 0);
    colActiveLayout->setSpacing(15);

    QLabel* titleActive = new QLabel("ANTREAN MASUK MASAK (KITCHEN QUEUE)");
    titleActive->setFont(QFont("Segoe UI Black", 14, QFont::Black));
    titleActive->setStyleSheet("color: #EA580C;");
    colActiveLayout->addWidget(titleActive);

    QScrollArea* scrollActive = new QScrollArea();
    scrollActive->setWidgetResizable(true);
    scrollActive->setFrameShape(QFrame::NoFrame);
    scrollActive->setHorizontalScrollBarPolicy(Qt::ScrollBarAlwaysOff);

    panelKdsActiveQueue = new QWidget();
    layoutKdsActive = new QVBoxLayout(panelKdsActiveQueue);
    layoutKdsActive->setContentsMargins(0, 0, 0, 0);
    layoutKdsActive->setSpacing(16);
    layoutKdsActive->setAlignment(Qt::AlignTop);

    scrollActive->setWidget(panelKdsActiveQueue);
    colActiveLayout->addWidget(scrollActive);
    tabLayout->addWidget(colActive);

    QWidget* colHistory = new QWidget();
    QVBoxLayout* colHistoryLayout = new QVBoxLayout(colHistory);
    colHistoryLayout->setContentsMargins(0, 0, 0, 0);
    colHistoryLayout->setSpacing(15);

    QLabel* titleHistory = new QLabel("RIWAYAT PANGGILAN SUARA (COMPLETED)");
    titleHistory->setFont(QFont("Segoe UI Black", 14, QFont::Black));
    titleHistory->setStyleSheet("color: #22C55E;");
    colHistoryLayout->addWidget(titleHistory);

    QScrollArea* scrollHistory = new QScrollArea();
    scrollHistory->setWidgetResizable(true);
    scrollHistory->setFrameShape(QFrame::NoFrame);
    scrollHistory->setHorizontalScrollBarPolicy(Qt::ScrollBarAlwaysOff);

    panelKdsHistoryQueue = new QWidget();
    layoutKdsHistory = new QVBoxLayout(panelKdsHistoryQueue);
    layoutKdsHistory->setContentsMargins(0, 0, 0, 0);
    layoutKdsHistory->setSpacing(16);
    layoutKdsHistory->setAlignment(Qt::AlignTop);

    scrollHistory->setWidget(panelKdsHistoryQueue);
    colHistoryLayout->addWidget(scrollHistory);
    tabLayout->addWidget(colHistory);

    refreshKdsViews();
    return tab;
}

void GacoanApp::refreshKdsViews() {
    QLayoutItem* child;
    while ((child = layoutKdsActive->takeAt(0)) != nullptr) {
        if (child->widget()) child->widget()->deleteLater();
        delete child;
    }

    if (antreanDapur.isEmpty()) {
        layoutKdsActive->addWidget(buildKdsPlaceholder("Belum ada antrean makanan masuk. Dapur bersih!"));
    } else {
        for (const auto& trans : antreanDapur) {
            layoutKdsActive->addWidget(buildOrderQueueCard(trans, true));
        }
    }
    layoutKdsActive->addStretch();

    while ((child = layoutKdsHistory->takeAt(0)) != nullptr) {
        if (child->widget()) child->widget()->deleteLater();
        delete child;
    }

    if (riwayatPanggilan.isEmpty()) {
        layoutKdsHistory->addWidget(buildKdsPlaceholder("Belum ada riwayat panggilan TOA."));
    } else {
        for (int i = riwayatPanggilan.size() - 1; i >= 0; --i) {
            layoutKdsHistory->addWidget(buildOrderQueueCard(riwayatPanggilan[i], false));
        }
    }
    layoutKdsHistory->addStretch();
}

QWidget* GacoanApp::buildKdsPlaceholder(const QString& text) {
    QFrame* card = createCard();
    card->setFixedHeight(70);

    QHBoxLayout* hl = new QHBoxLayout(card);
    hl->setContentsMargins(20, 0, 20, 0);

    QLabel* lbl = new QLabel(text);
    lbl->setFont(QFont("Segoe UI Semibold", 13, QFont::Normal, true));
    lbl->setProperty("class", "textSecondary");
    lbl->setAlignment(Qt::AlignCenter);
    hl->addWidget(lbl);

    return card;
}

QWidget* GacoanApp::buildOrderQueueCard(const TransaksiPesanan& trans, bool isActive) {
    QFrame* card = createCard();
    QVBoxLayout* cardLayout = new QVBoxLayout(card);
    cardLayout->setContentsMargins(20, 16, 20, 16);
    cardLayout->setSpacing(12);

    QHBoxLayout* headerRow = new QHBoxLayout();
    QLabel* lblTable = new QLabel(QString("NOMOR MEJA: %1").arg(trans.getNomorMeja()));
    lblTable->setFont(QFont("Segoe UI Black", 15, QFont::Black));
    lblTable->setProperty("class", "textPrimary");
    headerRow->addWidget(lblTable);
    headerRow->addStretch();

    QLabel* lblId = new QLabel(trans.getIdNota());
    lblId->setFont(QFont("Consolas", 11, QFont::Bold));
    lblId->setProperty("class", "textSecondary");
    headerRow->addWidget(lblId);
    cardLayout->addLayout(headerRow);

    for (const auto& it : trans.getDaftarBelanja()) {
        QString s = QString("  %1x  %2").arg(it.getKuantitas()).arg(it.getMenu().getNama());
        if (it.getMenu().getKategori().compare("Makanan", Qt::CaseInsensitive) == 0) {
            s += QString(" (Lvl %1)").arg(it.getLevelPedas());
        }
        if (!it.getCatatan().isEmpty()) {
            s += QString("  [Notes: %1]").arg(it.getCatatan());
        }

        QLabel* lblRow = new QLabel(s);
        lblRow->setFont(QFont("Segoe UI Semibold", 12));
        lblRow->setProperty("class", "textPrimary");
        cardLayout->addWidget(lblRow);
    }

    QHBoxLayout* footerRow = new QHBoxLayout();
    footerRow->addStretch();

    if (isActive) {
        QPushButton* btnFinish = createPillButton("SELESAI MASAK & PANGGIL TOA", "pillBtnGreen");
        btnFinish->setFixedSize(260, 36);

        TransaksiPesanan transCopy = trans;
        connect(btnFinish, &QPushButton::clicked, this, [this, transCopy]() mutable {
            for (int i = 0; i < antreanDapur.size(); ++i) {
                if (antreanDapur[i].getIdNota() == transCopy.getIdNota()) {
                    antreanDapur.removeAt(i);
                    break;
                }
            }
            transCopy.setStatus("SELESAI");
            riwayatPanggilan.append(transCopy);

            int meja = transCopy.getNomorMeja();
            std::thread t([meja]() {
                GacoanEngine::panggilAntrean(meja);
            });
            t.detach();

            refreshKdsViews();
        });
        footerRow->addWidget(btnFinish);
    } else {
        QLabel* lblStatus = new QLabel("PANGGILAN DIKIRIM ✓");
        lblStatus->setFont(QFont("Segoe UI Semibold", 12, QFont::Bold));
        lblStatus->setStyleSheet("color: #22C55E;");
        footerRow->addWidget(lblStatus);

        QPushButton* btnRecall = createPillButton("PANGGIL ULANG", "pillBtnGhost");
        btnRecall->setFixedSize(130, 30);

        int meja = trans.getNomorMeja();
        connect(btnRecall, &QPushButton::clicked, this, [meja]() {
            std::thread t([meja]() {
                GacoanEngine::panggilAntrean(meja);
            });
            t.detach();
        });
        footerRow->addWidget(btnRecall);
    }

    cardLayout->addLayout(footerRow);
    return card;
}
