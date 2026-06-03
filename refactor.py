import sys

file_path = r'c:\Users\refia\koding\PBO\EAS-PBO-Gacoan\gacoan\GacoanApp.java'
with open(file_path, 'r', encoding='utf-8') as f:
    content = f.read()

# 1. Background utama
content = content.replace('tab.setBackground(COLOR_BG);', 'tab.setBackground(UITheme.COLOR_BG_APP);')
content = content.replace('panelGridMenu.setBackground(COLOR_BG);', 'panelGridMenu.setBackground(UITheme.COLOR_BG_APP);')

# 2. QR Card 
old_qrcard = '''        ModernCard qrCard = new ModernCard(18, COLOR_CARD, COLOR_BORDER, 1);
        qrCard.setLayout(new BorderLayout(15, 15));
        qrCard.setBorder(new EmptyBorder(16, 20, 16, 20));'''
new_qrcard = '''        JPanel qrCardOuter = new JPanel(new BorderLayout());
        UITheme.applyAntigravityEffect(qrCardOuter);
        JPanel qrCard = new JPanel(new BorderLayout(15, 15));
        qrCard.setOpaque(false);
        qrCard.setBorder(new EmptyBorder(16, 20, 16, 20));
        qrCardOuter.add(qrCard, BorderLayout.CENTER);'''
content = content.replace(old_qrcard, new_qrcard)

content = content.replace('lblTableStatus.setForeground(COLOR_ACCENT);', 'lblTableStatus.setForeground(UITheme.COLOR_TEXT_SECONDARY);')

old_btnscan = '''btnScanQr = new ModernButton("PINDAI BARCODE QR MEJA", COLOR_ACCENT, COLOR_ACCENT_HOVER, 10);
        btnScanQr.setFont(new Font("Segoe UI", Font.BOLD, 11));'''
new_btnscan = '''btnScanQr = new ModernButton("PINDAI BARCODE QR MEJA", UITheme.COLOR_ACCENT_PRIMARY, UITheme.COLOR_ACCENT_PRIMARY.darker(), 12);
        btnScanQr.setFont(new Font("Segoe UI", Font.BOLD, 11));
        btnScanQr.setForeground(Color.WHITE);'''
content = content.replace(old_btnscan, new_btnscan)

content = content.replace('lblTableStatus.setForeground(COLOR_GREEN);', 'lblTableStatus.setForeground(UITheme.COLOR_ACCENT_SECONDARY);')
content = content.replace('btnScanQr.setNormalBg(COLOR_CARD_LIGHT);', 'btnScanQr.setNormalBg(UITheme.COLOR_BG_INPUT);')
content = content.replace('btnScanQr.setBackground(COLOR_CARD_LIGHT);', 'btnScanQr.setBackground(UITheme.COLOR_BG_INPUT);\n            btnScanQr.setForeground(UITheme.COLOR_TEXT_SECONDARY);')
content = content.replace('leftLayout.add(qrCard, BorderLayout.NORTH);', 'leftLayout.add(qrCardOuter, BorderLayout.NORTH);')

# 3. Filter Buttons
old_filterbtn = '''ModernButton btnF = new ModernButton(c.toUpperCase(), COLOR_CARD, COLOR_CARD_LIGHT, 12);
            btnF.setFont(new Font("Segoe UI", Font.BOLD, 11));'''
new_filterbtn = '''ModernButton btnF = new ModernButton(c.toUpperCase(), UITheme.COLOR_BG_CARD, UITheme.COLOR_BG_INPUT, 16);
            btnF.setFont(new Font("Segoe UI", Font.BOLD, 11));
            btnF.setForeground(UITheme.COLOR_TEXT_PRIMARY);'''
content = content.replace(old_filterbtn, new_filterbtn)

# 4. Cart Sidebar
old_cartcard = '''        ModernCard cartCard = new ModernCard(20, COLOR_CARD, COLOR_BORDER, 1);
        cartCard.setLayout(new BorderLayout(20, 20));
        cartCard.setPreferredSize(new Dimension(390, 0));
        cartCard.setBorder(new EmptyBorder(24, 20, 24, 20));

        JLabel lblCartTitle = new JLabel("Detail Keranjang Belanja");
        lblCartTitle.setFont(new Font("Segoe UI Black", Font.BOLD, 18));
        lblCartTitle.setForeground(COLOR_TEXT_PRIMARY);
        cartCard.add(lblCartTitle, BorderLayout.NORTH);'''
new_cartcard = '''        JPanel cartCardOuter = new JPanel(new BorderLayout());
        cartCardOuter.setPreferredSize(new Dimension(390, 0));
        UITheme.applyAntigravityEffect(cartCardOuter);
        JPanel cartCard = new JPanel(new BorderLayout(20, 20));
        cartCard.setOpaque(false);
        cartCard.setBorder(new EmptyBorder(24, 20, 24, 20));
        cartCardOuter.add(cartCard, BorderLayout.CENTER);

        JLabel lblCartTitle = new JLabel("Detail Keranjang Belanja");
        lblCartTitle.setFont(new Font("Segoe UI Black", Font.BOLD, 18));
        lblCartTitle.setForeground(UITheme.COLOR_TEXT_PRIMARY);
        cartCard.add(lblCartTitle, BorderLayout.NORTH);'''
content = content.replace(old_cartcard, new_cartcard)

# 5. Table Style
old_table = '''        tableKeranjang = new JTable(modelTabelKeranjang);
        tableKeranjang.setBackground(COLOR_CARD);
        tableKeranjang.setForeground(COLOR_TEXT_PRIMARY);
        tableKeranjang.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 12));
        tableKeranjang.setRowHeight(32);
        tableKeranjang.setGridColor(COLOR_BG);
        tableKeranjang.setShowHorizontalLines(true);
        tableKeranjang.setShowVerticalLines(false);
        
        // Header styling
        JTableHeader header = tableKeranjang.getTableHeader();
        header.setDefaultRenderer(new ModernTableHeaderRenderer());

        // Column Renderers and Alignment
        tableKeranjang.getColumnModel().getColumn(0).setCellRenderer(new ModernTableCellRenderer(JLabel.LEFT));
        tableKeranjang.getColumnModel().getColumn(1).setCellRenderer(new ModernTableCellRenderer(JLabel.CENTER));
        tableKeranjang.getColumnModel().getColumn(2).setCellRenderer(new ModernTableCellRenderer(JLabel.RIGHT));

        JScrollPane cartScroll = new JScrollPane(tableKeranjang);
        cartScroll.setBorder(null);
        cartScroll.setBackground(COLOR_CARD);
        cartScroll.getViewport().setBackground(COLOR_CARD);
        cartScroll.getVerticalScrollBar().setUI(new ModernScrollbarUI());
        cartCard.add(cartScroll, BorderLayout.CENTER);'''
new_table = '''        tableKeranjang = new JTable(modelTabelKeranjang);
        tableKeranjang.setBackground(UITheme.COLOR_BG_CARD);
        tableKeranjang.setForeground(UITheme.COLOR_TEXT_PRIMARY);
        tableKeranjang.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 12));
        tableKeranjang.setRowHeight(32);
        tableKeranjang.setGridColor(UITheme.COLOR_BG_APP);
        tableKeranjang.setShowHorizontalLines(true);
        tableKeranjang.setShowVerticalLines(false);
        
        JTableHeader header = tableKeranjang.getTableHeader();
        header.setBackground(UITheme.COLOR_BG_CARD);
        header.setForeground(UITheme.COLOR_TEXT_SECONDARY);
        header.setFont(new Font("Segoe UI", Font.BOLD, 12));
        
        javax.swing.table.DefaultTableCellRenderer lightRendererL = new javax.swing.table.DefaultTableCellRenderer();
        lightRendererL.setHorizontalAlignment(JLabel.LEFT);
        lightRendererL.setBackground(UITheme.COLOR_BG_CARD);
        lightRendererL.setForeground(UITheme.COLOR_TEXT_PRIMARY);
        
        javax.swing.table.DefaultTableCellRenderer lightRendererC = new javax.swing.table.DefaultTableCellRenderer();
        lightRendererC.setHorizontalAlignment(JLabel.CENTER);
        lightRendererC.setBackground(UITheme.COLOR_BG_CARD);
        lightRendererC.setForeground(UITheme.COLOR_TEXT_PRIMARY);
        
        javax.swing.table.DefaultTableCellRenderer lightRendererR = new javax.swing.table.DefaultTableCellRenderer();
        lightRendererR.setHorizontalAlignment(JLabel.RIGHT);
        lightRendererR.setBackground(UITheme.COLOR_BG_CARD);
        lightRendererR.setForeground(UITheme.COLOR_TEXT_PRIMARY);

        tableKeranjang.getColumnModel().getColumn(0).setCellRenderer(lightRendererL);
        tableKeranjang.getColumnModel().getColumn(1).setCellRenderer(lightRendererC);
        tableKeranjang.getColumnModel().getColumn(2).setCellRenderer(lightRendererR);

        JScrollPane cartScroll = new JScrollPane(tableKeranjang);
        cartScroll.setBorder(null);
        cartScroll.setBackground(UITheme.COLOR_BG_CARD);
        cartScroll.getViewport().setBackground(UITheme.COLOR_BG_CARD);
        cartScroll.getVerticalScrollBar().setUI(new ModernScrollbarUI());
        cartCard.add(cartScroll, BorderLayout.CENTER);'''
content = content.replace(old_table, new_table)

# 6. Totals Panel
old_totals = '''        lblSubtotal.setForeground(COLOR_TEXT_MUTED);

        lblTax = new JLabel("Pajak Restoran (PB1 10%): Rp 0");
        lblTax.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblTax.setForeground(COLOR_TEXT_MUTED);

        lblTotal = new JLabel("TOTAL TAGIHAN: Rp 0");
        lblTotal.setFont(new Font("Segoe UI Black", Font.BOLD, 20));
        lblTotal.setForeground(COLOR_GREEN);'''
new_totals = '''        lblSubtotal.setForeground(UITheme.COLOR_TEXT_SECONDARY);

        lblTax = new JLabel("Pajak Restoran (PB1 10%): Rp 0");
        lblTax.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblTax.setForeground(UITheme.COLOR_TEXT_SECONDARY);

        lblTotal = new JLabel("TOTAL TAGIHAN: Rp 0");
        lblTotal.setFont(new Font("Segoe UI Black", Font.BOLD, 26));
        lblTotal.setForeground(UITheme.COLOR_TEXT_PRIMARY);'''
content = content.replace(old_totals, new_totals)

old_checkout = '''        ModernButton btnCheckout = new ModernButton("KONFIRMASI BAYAR & STRUK", COLOR_ACCENT, COLOR_ACCENT_HOVER, 14);
        btnCheckout.setFont(new Font("Segoe UI", Font.BOLD, 14));'''
new_checkout = '''        ModernButton btnCheckout = new ModernButton("KONFIRMASI BAYAR & STRUK", UITheme.COLOR_ACCENT_PRIMARY, UITheme.COLOR_ACCENT_PRIMARY.darker(), 16);
        btnCheckout.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnCheckout.setForeground(Color.WHITE);'''
content = content.replace(old_checkout, new_checkout)

content = content.replace('tab.add(cartCard, BorderLayout.EAST);', 'tab.add(cartCardOuter, BorderLayout.EAST);')

# 7. Menu Grid Card Refactoring
old_menucard = '''            // Clean modern product card
            ModernCard card = new ModernCard(18, COLOR_CARD, COLOR_BORDER, 1);
            card.setLayout(new BorderLayout(15, 12));
            card.setBorder(new EmptyBorder(16, 16, 16, 16));'''
new_menucard = '''            // Clean modern product card
            JPanel cardOuter = new JPanel(new BorderLayout());
            UITheme.applyAntigravityEffect(cardOuter);
            JPanel card = new JPanel(new BorderLayout(15, 12));
            card.setOpaque(false);
            card.setBorder(new EmptyBorder(16, 16, 16, 16));
            cardOuter.add(card, BorderLayout.CENTER);'''
content = content.replace(old_menucard, new_menucard)

old_menutext = '''            JLabel name = new JLabel(item.getNama());
            name.setFont(new Font("Segoe UI Semibold", Font.BOLD, 15));
            name.setForeground(COLOR_TEXT_PRIMARY);'''
new_menutext = '''            JLabel name = new JLabel(item.getNama());
            name.setFont(new Font("Segoe UI Black", Font.BOLD, 17));
            name.setForeground(UITheme.COLOR_TEXT_PRIMARY);'''
content = content.replace(old_menutext, new_menutext)

old_catpill = '''            Color tagColor = COLOR_ACCENT;
            if (item.getKategori().equalsIgnoreCase("Dimsum")) tagColor = COLOR_CYAN;
            if (item.getKategori().equalsIgnoreCase("Minuman")) tagColor = COLOR_PURPLE;

            JLabel lblCat = new JLabel("  " + item.getKategori().toUpperCase() + "  ");
            lblCat.setFont(new Font("Segoe UI Black", Font.BOLD, 9));
            lblCat.setForeground(COLOR_TEXT_PRIMARY);'''
new_catpill = '''            Color tagColor = UITheme.COLOR_ACCENT_PRIMARY;
            if (item.getKategori().equalsIgnoreCase("Dimsum")) tagColor = new Color(6, 182, 212);
            if (item.getKategori().equalsIgnoreCase("Minuman")) tagColor = new Color(168, 85, 247);

            JLabel lblCat = new JLabel("  " + item.getKategori().toUpperCase() + "  ");
            lblCat.setFont(new Font("Segoe UI Black", Font.BOLD, 9));
            lblCat.setForeground(Color.WHITE);'''
content = content.replace(old_catpill, new_catpill)

old_price = '''            JLabel price = new JLabel(priceText);
            price.setFont(new Font("Segoe UI Black", Font.BOLD, 15));
            price.setForeground(COLOR_GREEN);'''
new_price = '''            JLabel price = new JLabel(priceText);
            price.setFont(new Font("Segoe UI Black", Font.BOLD, 16));
            price.setForeground(UITheme.COLOR_ACCENT_SECONDARY);'''
content = content.replace(old_price, new_price)

old_options = '''            JLabel lblNotes = new JLabel("Catatan Konsumen:");
            lblNotes.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 12));
            lblNotes.setForeground(COLOR_TEXT_MUTED);

            ModernTextField txtNote = new ModernTextField("Contoh: Tanpa kuah, sendok...");

            ModernComboBox<String> cmbLvl = new ModernComboBox<>();
            if (isFood) {
                JLabel lblLvl = new JLabel("Pilih Level Pedas:");
                lblLvl.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 12));
                lblLvl.setForeground(COLOR_TEXT_MUTED);'''
new_options = '''            JLabel lblNotes = new JLabel("Catatan Konsumen:");
            lblNotes.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 12));
            lblNotes.setForeground(UITheme.COLOR_TEXT_SECONDARY);

            ModernTextField txtNote = new ModernTextField("Contoh: Tanpa kuah, sendok...");
            txtNote.setBackground(UITheme.COLOR_BG_INPUT);
            txtNote.setForeground(UITheme.COLOR_TEXT_PRIMARY);
            txtNote.setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 12));

            ModernComboBox<String> cmbLvl = new ModernComboBox<>();
            cmbLvl.setBackground(UITheme.COLOR_BG_INPUT);
            cmbLvl.setForeground(UITheme.COLOR_TEXT_PRIMARY);
            
            if (isFood) {
                JLabel lblLvl = new JLabel("Pilih Level Pedas:");
                lblLvl.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 12));
                lblLvl.setForeground(UITheme.COLOR_TEXT_SECONDARY);'''
content = content.replace(old_options, new_options)

old_addbtn = '''            // Card Bottom: Large add button
            ModernButton btnAdd = new ModernButton("TAMBAHKAN KAN", COLOR_ACCENT, COLOR_ACCENT_HOVER, 10);
            btnAdd.setFont(new Font("Segoe UI", Font.BOLD, 12));'''
new_addbtn = '''            // Card Bottom: Large add button
            ModernButton btnAdd = new ModernButton("TAMBAHKAN KAN", UITheme.COLOR_ACCENT_PRIMARY, UITheme.COLOR_ACCENT_PRIMARY.darker(), 16);
            btnAdd.setFont(new Font("Segoe UI", Font.BOLD, 12));
            btnAdd.setForeground(Color.WHITE);'''
content = content.replace(old_addbtn, new_addbtn)

content = content.replace('txtNote.setForeground(COLOR_TEXT_MUTED);', 'txtNote.setForeground(UITheme.COLOR_TEXT_SECONDARY);')

content = content.replace('panelGridMenu.add(card);', 'panelGridMenu.add(cardOuter);')

# 8. Unlocking scanner UI
old_unlock = '''            btnScanQr.setEnabled(true);
            btnScanQr.setNormalBg(COLOR_ACCENT);
            btnScanQr.setBackground(COLOR_ACCENT);
            btnScanQr.setText("PINDAI BARCODE QR MEJA");
            lblTableStatus.setText("SILAKAN SCAN BARCODE QR PADA MEJA KIOSK");
            lblTableStatus.setForeground(COLOR_ACCENT);'''
new_unlock = '''            btnScanQr.setEnabled(true);
            btnScanQr.setNormalBg(UITheme.COLOR_ACCENT_PRIMARY);
            btnScanQr.setBackground(UITheme.COLOR_ACCENT_PRIMARY);
            btnScanQr.setForeground(Color.WHITE);
            btnScanQr.setText("PINDAI BARCODE QR MEJA");
            lblTableStatus.setText("SILAKAN SCAN BARCODE QR PADA MEJA KIOSK");
            lblTableStatus.setForeground(UITheme.COLOR_TEXT_SECONDARY);'''
content = content.replace(old_unlock, new_unlock)

with open(file_path, 'w', encoding='utf-8') as f:
    f.write(content)
print('Done refactoring GacoanApp.java')
