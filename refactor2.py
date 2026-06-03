import sys

file_path = r'c:\Users\refia\koding\PBO\EAS-PBO-Gacoan\gacoan\GacoanApp.java'
with open(file_path, 'r', encoding='utf-8') as f:
    content = f.read()

# 1. Menu Cards stretch fix
old_grid_scroll = '''        // Food Grid Scroll Pane
        panelGridMenu = new JPanel(new GridLayout(0, 2, 16, 16));
        panelGridMenu.setBackground(UITheme.COLOR_BG_APP);

        JScrollPane catalogScroll = new JScrollPane(panelGridMenu);'''
new_grid_scroll = '''        // Food Grid Scroll Pane
        panelGridMenu = new JPanel(new GridLayout(0, 2, 16, 16));
        panelGridMenu.setBackground(UITheme.COLOR_BG_APP);
        
        JPanel gridWrapper = new JPanel(new BorderLayout());
        gridWrapper.setBackground(UITheme.COLOR_BG_APP);
        gridWrapper.add(panelGridMenu, BorderLayout.NORTH);

        JScrollPane catalogScroll = new JScrollPane(gridWrapper);'''
content = content.replace(old_grid_scroll, new_grid_scroll)

# 2. ModernComboBox styling (replace COLOR_CARD_LIGHT with UITheme.COLOR_BG_CARD)
old_cmb1 = '''        public ModernComboBox() {
            setBackground(COLOR_CARD_LIGHT);
            setForeground(COLOR_TEXT_PRIMARY);
            setBorder(BorderFactory.createLineBorder(COLOR_BORDER, 1));'''
new_cmb1 = '''        public ModernComboBox() {
            setBackground(UITheme.COLOR_BG_CARD);
            setForeground(UITheme.COLOR_TEXT_PRIMARY);
            setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));'''
content = content.replace(old_cmb1, new_cmb1)

old_cmb2 = '''                            g2.setColor(COLOR_CARD_LIGHT);
                            g2.fillRect(0, 0, getWidth(), getHeight());
                            g2.setColor(COLOR_TEXT_MUTED);'''
new_cmb2 = '''                            g2.setColor(UITheme.COLOR_BG_CARD);
                            g2.fillRect(0, 0, getWidth(), getHeight());
                            g2.setColor(UITheme.COLOR_TEXT_SECONDARY);'''
content = content.replace(old_cmb2, new_cmb2)

old_cmb3 = '''                public void paintCurrentValueBackground(Graphics g, Rectangle bounds, boolean hasFocus) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setColor(COLOR_CARD_LIGHT);'''
new_cmb3 = '''                public void paintCurrentValueBackground(Graphics g, Rectangle bounds, boolean hasFocus) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setColor(UITheme.COLOR_BG_CARD);'''
content = content.replace(old_cmb3, new_cmb3)

old_cmb4 = '''                    label.setBackground(isSelected ? COLOR_ACCENT : COLOR_CARD_LIGHT);
                    label.setForeground(COLOR_TEXT_PRIMARY);'''
new_cmb4 = '''                    label.setBackground(isSelected ? UITheme.COLOR_ACCENT_PRIMARY : UITheme.COLOR_BG_CARD);
                    label.setForeground(UITheme.COLOR_TEXT_PRIMARY);'''
content = content.replace(old_cmb4, new_cmb4)

old_cmb5 = '''                    JList<?> list = ((javax.swing.plaf.basic.ComboPopup) child).getList();
                    list.setBackground(COLOR_CARD_LIGHT);
                    list.setForeground(COLOR_TEXT_PRIMARY);'''
new_cmb5 = '''                    JList<?> list = ((javax.swing.plaf.basic.ComboPopup) child).getList();
                    list.setBackground(UITheme.COLOR_BG_CARD);
                    list.setForeground(UITheme.COLOR_TEXT_PRIMARY);'''
content = content.replace(old_cmb5, new_cmb5)

# Also update the instantiation inside renderModernCatalogGrid
old_cmb_inst = '''            ModernComboBox<String> cmbLvl = new ModernComboBox<>();
            cmbLvl.setBackground(UITheme.COLOR_BG_INPUT);
            cmbLvl.setForeground(UITheme.COLOR_TEXT_PRIMARY);'''
new_cmb_inst = '''            ModernComboBox<String> cmbLvl = new ModernComboBox<>();
            cmbLvl.setBackground(UITheme.COLOR_BG_CARD);
            cmbLvl.setForeground(UITheme.COLOR_TEXT_PRIMARY);
            cmbLvl.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));'''
content = content.replace(old_cmb_inst, new_cmb_inst)

# 3. Add Button Margin
old_add_btn = '''            // Card Bottom: Large add button
            ModernButton btnAdd = new ModernButton("TAMBAHKAN KAN", UITheme.COLOR_ACCENT_PRIMARY, UITheme.COLOR_ACCENT_PRIMARY.darker(), 16);
            btnAdd.setFont(new Font("Segoe UI", Font.BOLD, 12));
            btnAdd.setForeground(Color.WHITE);
            btnAdd.setPreferredSize(new Dimension(0, 36));
            btnAdd.addActionListener(e -> {'''
new_add_btn = '''            // Card Bottom: Large add button
            ModernButton btnAdd = new ModernButton("TAMBAHKAN KAN", UITheme.COLOR_ACCENT_PRIMARY, UITheme.COLOR_ACCENT_PRIMARY.darker(), 16);
            btnAdd.setFont(new Font("Segoe UI", Font.BOLD, 12));
            btnAdd.setForeground(Color.WHITE);
            btnAdd.setPreferredSize(new Dimension(0, 36));
            
            JPanel btnWrapper = new JPanel(new BorderLayout());
            btnWrapper.setOpaque(false);
            btnWrapper.setBorder(new EmptyBorder(8, 12, 8, 12));
            btnWrapper.add(btnAdd, BorderLayout.CENTER);
            
            btnAdd.addActionListener(e -> {'''
content = content.replace(old_add_btn, new_add_btn)

# Replace the place where btnAdd is added to the card
old_add_btn_card = '''            card.add(btnAdd, BorderLayout.SOUTH);'''
new_add_btn_card = '''            card.add(btnWrapper, BorderLayout.SOUTH);'''
content = content.replace(old_add_btn_card, new_add_btn_card)

# KDS SYSTEM REFACTORING
# 1. Backgrounds
content = content.replace('tab.setBackground(COLOR_BG);', 'tab.setBackground(UITheme.COLOR_BG_APP);')
content = content.replace('panelKdsActiveQueue.setBackground(COLOR_BG);', 'panelKdsActiveQueue.setBackground(UITheme.COLOR_BG_APP);')
content = content.replace('scrollActive.setBackground(COLOR_BG);', 'scrollActive.setBackground(UITheme.COLOR_BG_APP);')
content = content.replace('scrollActive.getViewport().setBackground(COLOR_BG);', 'scrollActive.getViewport().setBackground(UITheme.COLOR_BG_APP);')
content = content.replace('panelKdsHistoryQueue.setBackground(COLOR_BG);', 'panelKdsHistoryQueue.setBackground(UITheme.COLOR_BG_APP);')
content = content.replace('scrollHistory.setBackground(COLOR_BG);', 'scrollHistory.setBackground(UITheme.COLOR_BG_APP);')
content = content.replace('scrollHistory.getViewport().setBackground(COLOR_BG);', 'scrollHistory.getViewport().setBackground(UITheme.COLOR_BG_APP);')

# 2. Text Colors
content = content.replace('titleActive.setForeground(COLOR_ACCENT);', 'titleActive.setForeground(UITheme.COLOR_ACCENT_PRIMARY);')
content = content.replace('titleHistory.setForeground(COLOR_GREEN);', 'titleHistory.setForeground(UITheme.COLOR_ACCENT_SECONDARY);')

# 3. KDS Placeholders
old_kds_ph = '''    private JPanel createKdsPlaceholder(String text) {
        ModernCard ph = new ModernCard(16, COLOR_CARD, COLOR_BORDER, 1);
        ph.setLayout(new BorderLayout());
        ph.setBorder(new EmptyBorder(25, 20, 25, 20));
        ph.setMaximumSize(new Dimension(Integer.MAX_VALUE, 75));

        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI Semibold", Font.ITALIC, 13));
        lbl.setForeground(COLOR_TEXT_MUTED);
        lbl.setHorizontalAlignment(JLabel.CENTER);
        ph.add(lbl, BorderLayout.CENTER);

        return ph;
    }'''
new_kds_ph = '''    private JPanel createKdsPlaceholder(String text) {
        JPanel phOuter = new JPanel(new BorderLayout());
        UITheme.applyAntigravityEffect(phOuter);
        phOuter.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
        JPanel ph = new JPanel(new BorderLayout());
        ph.setOpaque(false);
        ph.setBorder(new EmptyBorder(25, 20, 25, 20));

        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI Semibold", Font.ITALIC, 13));
        lbl.setForeground(UITheme.COLOR_TEXT_SECONDARY);
        lbl.setHorizontalAlignment(JLabel.CENTER);
        ph.add(lbl, BorderLayout.CENTER);
        phOuter.add(ph, BorderLayout.CENTER);

        return phOuter;
    }'''
content = content.replace(old_kds_ph, new_kds_ph)

# 4. Order Queue Card
old_queue_card = '''    private JPanel buildOrderQueueCard(TransaksiPesanan trans, boolean isActive) {
        // Round Card with glowing accent border
        ModernCard card = new ModernCard(18, COLOR_CARD, isActive ? COLOR_ACCENT : COLOR_GREEN, 2);
        card.setLayout(new BorderLayout(15, 12));
        card.setBorder(new EmptyBorder(16, 20, 16, 20));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 220));'''
new_queue_card = '''    private JPanel buildOrderQueueCard(TransaksiPesanan trans, boolean isActive) {
        // Round Card with floating border
        JPanel cardOuter = new JPanel(new BorderLayout());
        UITheme.applyAntigravityEffect(cardOuter);
        cardOuter.setMaximumSize(new Dimension(Integer.MAX_VALUE, 240));
        
        JPanel card = new JPanel(new BorderLayout(15, 12));
        card.setOpaque(false);
        card.setBorder(new EmptyBorder(16, 20, 16, 20));
        cardOuter.add(card, BorderLayout.CENTER);'''
content = content.replace(old_queue_card, new_queue_card)

# Text inside Order Queue Card
content = content.replace('lblTable.setForeground(COLOR_TEXT_PRIMARY);', 'lblTable.setForeground(UITheme.COLOR_TEXT_PRIMARY);')
content = content.replace('lblId.setForeground(COLOR_TEXT_MUTED);', 'lblId.setForeground(UITheme.COLOR_TEXT_SECONDARY);')
content = content.replace('lblRow.setForeground(COLOR_TEXT_PRIMARY);', 'lblRow.setForeground(UITheme.COLOR_TEXT_PRIMARY);')
content = content.replace('lblStatus.setForeground(COLOR_GREEN);', 'lblStatus.setForeground(UITheme.COLOR_ACCENT_SECONDARY);')

old_btn_finish = '''            ModernButton btnFinish = new ModernButton("SELESAI MASAK & PANGGIL TOA C++", COLOR_GREEN, COLOR_GREEN.brighter(), 10);
            btnFinish.setFont(new Font("Segoe UI", Font.BOLD, 11));'''
new_btn_finish = '''            ModernButton btnFinish = new ModernButton("SELESAI MASAK & PANGGIL TOA", UITheme.COLOR_ACCENT_SECONDARY, UITheme.COLOR_ACCENT_SECONDARY.darker(), 12);
            btnFinish.setFont(new Font("Segoe UI", Font.BOLD, 11));
            btnFinish.setForeground(Color.WHITE);'''
content = content.replace(old_btn_finish, new_btn_finish)

old_btn_recall = '''            ModernButton btnRecall = new ModernButton("PANGGIL ULANG", COLOR_CARD_LIGHT, COLOR_BORDER, 8);
            btnRecall.setFont(new Font("Segoe UI", Font.BOLD, 10));'''
new_btn_recall = '''            ModernButton btnRecall = new ModernButton("PANGGIL ULANG", UITheme.COLOR_BG_INPUT, UITheme.COLOR_BG_CARD, 8);
            btnRecall.setFont(new Font("Segoe UI", Font.BOLD, 10));
            btnRecall.setForeground(UITheme.COLOR_TEXT_PRIMARY);'''
content = content.replace(old_btn_recall, new_btn_recall)

# Important: The return value of buildOrderQueueCard should be cardOuter, not card.
old_return_card = '''        card.add(footerPanel, BorderLayout.SOUTH);

        return card;
    }'''
new_return_card = '''        card.add(footerPanel, BorderLayout.SOUTH);

        return cardOuter;
    }'''
content = content.replace(old_return_card, new_return_card)

with open(file_path, 'w', encoding='utf-8') as f:
    f.write(content)
print('Done refactoring KDS and Menu Cards.')
