import sys

app_path = r'c:\Users\refia\koding\PBO\EAS-PBO-Gacoan\gacoan\GacoanApp.java'
with open(app_path, 'r', encoding='utf-8') as f:
    content = f.read()

# =============================================================================
# BUG 1: KDS Placeholder - text squashed because MaximumSize height=80 is too
# small once the AntigravityBorder insets (top=12, bottom=16) are subtracted,
# leaving only ~52px for the inner panel which itself has 25px top+bottom padding.
# Also the BoxLayout + setMaximumSize combo is problematic.
# Fix: increase MaximumSize, add setPreferredSize, and use AlignmentX.
# =============================================================================

old_kds_ph = '''    private JPanel createKdsPlaceholder(String text) {
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

new_kds_ph = '''    private JPanel createKdsPlaceholder(String text) {
        JPanel phOuter = new JPanel(new BorderLayout());
        UITheme.applyAntigravityEffect(phOuter);
        phOuter.setPreferredSize(new Dimension(0, 100));
        phOuter.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));
        phOuter.setAlignmentX(Component.LEFT_ALIGNMENT);
        JPanel ph = new JPanel(new BorderLayout());
        ph.setOpaque(false);
        ph.setBorder(new EmptyBorder(12, 20, 12, 20));

        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI Semibold", Font.ITALIC, 13));
        lbl.setForeground(UITheme.COLOR_TEXT_SECONDARY);
        lbl.setHorizontalAlignment(JLabel.CENTER);
        ph.add(lbl, BorderLayout.CENTER);
        phOuter.add(ph, BorderLayout.CENTER);

        return phOuter;
    }'''
content = content.replace(old_kds_ph, new_kds_ph)

# Also fix KDS order card sizing - same issue
old_kds_card = '''        JPanel cardOuter = new JPanel(new BorderLayout());
        UITheme.applyAntigravityEffect(cardOuter);
        cardOuter.setMaximumSize(new Dimension(Integer.MAX_VALUE, 240));'''
new_kds_card = '''        JPanel cardOuter = new JPanel(new BorderLayout());
        UITheme.applyAntigravityEffect(cardOuter);
        cardOuter.setMaximumSize(new Dimension(Integer.MAX_VALUE, 300));
        cardOuter.setAlignmentX(Component.LEFT_ALIGNMENT);'''
content = content.replace(old_kds_card, new_kds_card)


# =============================================================================
# BUG 2: Text Area "Catatan Konsumen" border too kaku
# Fix: use a more refined compound border with rounded feel, Off-White bg,
# and generous inner padding (10px top/bottom, 15px left/right)
# =============================================================================

old_txtNote = '''            ModernTextField txtNote = new ModernTextField("Contoh: Tanpa kuah, sendok...");
            txtNote.setBackground(UITheme.COLOR_BG_INPUT);
            txtNote.setForeground(UITheme.COLOR_TEXT_PRIMARY);
            txtNote.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1),
                    BorderFactory.createEmptyBorder(6, 12, 6, 12)
            ));
            txtNote.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));'''

new_txtNote = '''            ModernTextField txtNote = new ModernTextField("Contoh: Tanpa kuah, sendok...");
            txtNote.setBackground(UITheme.COLOR_BG_INPUT);
            txtNote.setForeground(UITheme.COLOR_TEXT_PRIMARY);
            txtNote.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(209, 213, 219), 1),
                    BorderFactory.createEmptyBorder(10, 15, 10, 15)
            ));
            txtNote.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));'''
content = content.replace(old_txtNote, new_txtNote)


# =============================================================================
# BUG 3: PreFlight screen still dark mode with oval painting bug
# The ModernCard.paintComponent uses pillRadius = Math.min(w,h) which causes
# the big diagnostic box (600x560) to paint a huge oval instead of a rectangle.
# Fix: Completely rewrite buildPreFlightScreen to use Light Mode colors
# and use applyAntigravityEffect instead of ModernCard for the box.
# Also fix the ModernCard paintComponent to use radius, not pillRadius.
# =============================================================================

# Fix ModernCard to use the stored radius, NOT pill radius
old_moderncard_paint = '''        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(bgColor);
            int pillRadius = Math.min(getWidth(), getHeight());
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), pillRadius, pillRadius);

            if (borderColor != null && borderThickness > 0) {
                g2.setColor(borderColor);
                g2.setStroke(new BasicStroke(borderThickness));
                g2.drawRoundRect(borderThickness / 2, borderThickness / 2, 
                                 getWidth() - borderThickness, getHeight() - borderThickness, radius, radius);
            }
            g2.dispose();
            super.paintComponent(g);
        }'''

new_moderncard_paint = '''        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(bgColor);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);

            if (borderColor != null && borderThickness > 0) {
                g2.setColor(borderColor);
                g2.setStroke(new BasicStroke(borderThickness));
                g2.drawRoundRect(borderThickness / 2, borderThickness / 2, 
                                 getWidth() - borderThickness, getHeight() - borderThickness, radius, radius);
            }
            g2.dispose();
            super.paintComponent(g);
        }'''
content = content.replace(old_moderncard_paint, new_moderncard_paint)

# Now rewrite the PreFlight screen to Light Mode
old_preflight = '''    private JPanel buildPreFlightScreen() {
        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setBackground(COLOR_BG);

        // Core Glassmorphic Diagnostic Box
        ModernCard box = new ModernCard(28, COLOR_CARD, COLOR_BORDER, 2);
        box.setLayout(new BoxLayout(box, BoxLayout.Y_AXIS));
        box.setBorder(new EmptyBorder(40, 45, 40, 45));
        box.setPreferredSize(new Dimension(600, 560));

        // Pulsing Neon Gacoan Header
        JLabel logo = new JLabel("MIE GACOAN");
        logo.setFont(new Font("Segoe UI Black", Font.BOLD, 38));
        logo.setForeground(COLOR_ACCENT);
        logo.setAlignmentX(Component.CENTER_ALIGNMENT);
        box.add(logo);

        JLabel subtext = new JLabel("Sistem Self-Ordering & KDS JNI Hybrid");
        subtext.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 14));
        subtext.setForeground(COLOR_TEXT_MUTED);
        subtext.setAlignmentX(Component.CENTER_ALIGNMENT);
        box.add(Box.createRigidArea(new Dimension(0, 5)));
        box.add(subtext);

        box.add(Box.createRigidArea(new Dimension(0, 30)));

        // Run Pre-flight Checks
        PreFlightCheck.DiagnosticResult diag = PreFlightCheck.runCheck();

        // 1. JDK Row
        box.add(buildDiagnosticPanel("Java Runtime (JDK/JRE) Check", 
                diag.javaOk ? "PASSED (v" + System.getProperty("java.version") + ")" : "FAILED", diag.javaOk));
        box.add(Box.createRigidArea(new Dimension(0, 16)));

        // 2. GCC/g++ Row
        box.add(buildDiagnosticPanel("C++ Compiler Status (g++)", 
                diag.gccOk ? "PASSED (MinGW-w64)" : "FAILED (g++ compiler missing on PATH)", diag.gccOk));
        box.add(Box.createRigidArea(new Dimension(0, 16)));

        // 3. JNI DLL Row
        box.add(buildDiagnosticPanel("GacoanEngine JNI Library Check", 
                diag.dllOk ? "LOADED SUCCESSFUL" : "NOT LOADED / NEEDS COMPILATION", diag.dllOk));
        box.add(Box.createRigidArea(new Dimension(0, 10)));

        // DLL Detail Text
        JLabel dllDetail = new JLabel("Library Status: " + diag.dllPath);
        dllDetail.setFont(new Font("Monospaced", Font.PLAIN, 10));
        dllDetail.setForeground(diag.dllOk ? COLOR_GREEN : COLOR_ACCENT);
        dllDetail.setAlignmentX(Component.CENTER_ALIGNMENT);
        box.add(dllDetail);

        box.add(Box.createRigidArea(new Dimension(0, 35)));

        // Checkout enter application button
        String btnText = diag.dllOk ? "BUKA APLIKASI UTAMA (JNI AKTIF)" : "BUKA APLIKASI (MODE FALLBACK)";
        ModernButton btnGo = new ModernButton(btnText, diag.dllOk ? COLOR_GREEN : COLOR_ACCENT, diag.dllOk ? COLOR_GREEN.brighter() : COLOR_ACCENT_HOVER, 16);
        btnGo.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnGo.setPreferredSize(new Dimension(500, 48));
        btnGo.setMaximumSize(new Dimension(500, 48));
        btnGo.setAlignmentX(Component.CENTER_ALIGNMENT);'''

new_preflight = '''    private JPanel buildPreFlightScreen() {
        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setBackground(UITheme.COLOR_BG_APP);

        // Floating Light Mode Diagnostic Card
        JPanel boxOuter = new JPanel(new BorderLayout());
        UITheme.applyAntigravityEffect(boxOuter);
        boxOuter.setPreferredSize(new Dimension(640, 580));

        JPanel box = new JPanel();
        box.setLayout(new BoxLayout(box, BoxLayout.Y_AXIS));
        box.setOpaque(false);
        box.setBorder(new EmptyBorder(40, 45, 40, 45));
        boxOuter.add(box, BorderLayout.CENTER);

        // Gacoan Header (Orange branding)
        JLabel logo = new JLabel("MIE GACOAN");
        logo.setFont(new Font("Segoe UI Black", Font.BOLD, 38));
        logo.setForeground(UITheme.COLOR_ACCENT_PRIMARY);
        logo.setAlignmentX(Component.CENTER_ALIGNMENT);
        box.add(logo);

        JLabel subtext = new JLabel("Sistem Self-Ordering & KDS JNI Hybrid");
        subtext.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 14));
        subtext.setForeground(UITheme.COLOR_TEXT_SECONDARY);
        subtext.setAlignmentX(Component.CENTER_ALIGNMENT);
        box.add(Box.createRigidArea(new Dimension(0, 5)));
        box.add(subtext);

        box.add(Box.createRigidArea(new Dimension(0, 30)));

        // Run Pre-flight Checks
        PreFlightCheck.DiagnosticResult diag = PreFlightCheck.runCheck();

        // 1. JDK Row
        box.add(buildDiagnosticPanel("Java Runtime (JDK/JRE) Check", 
                diag.javaOk ? "PASSED (v" + System.getProperty("java.version") + ")" : "FAILED", diag.javaOk));
        box.add(Box.createRigidArea(new Dimension(0, 16)));

        // 2. GCC/g++ Row
        box.add(buildDiagnosticPanel("C++ Compiler Status (g++)", 
                diag.gccOk ? "PASSED (MinGW-w64)" : "FAILED (g++ compiler missing on PATH)", diag.gccOk));
        box.add(Box.createRigidArea(new Dimension(0, 16)));

        // 3. JNI DLL Row
        box.add(buildDiagnosticPanel("GacoanEngine JNI Library Check", 
                diag.dllOk ? "LOADED SUCCESSFUL" : "NOT LOADED / NEEDS COMPILATION", diag.dllOk));
        box.add(Box.createRigidArea(new Dimension(0, 10)));

        // DLL Detail Text
        JLabel dllDetail = new JLabel("Library Status: " + diag.dllPath);
        dllDetail.setFont(new Font("Monospaced", Font.PLAIN, 10));
        dllDetail.setForeground(diag.dllOk ? UITheme.COLOR_ACCENT_SECONDARY : UITheme.COLOR_ACCENT_PRIMARY);
        dllDetail.setAlignmentX(Component.CENTER_ALIGNMENT);
        box.add(dllDetail);

        box.add(Box.createRigidArea(new Dimension(0, 35)));

        // Checkout enter application button
        String btnText = diag.dllOk ? "BUKA APLIKASI UTAMA (JNI AKTIF)" : "BUKA APLIKASI (MODE FALLBACK)";
        ModernButton btnGo = new ModernButton(btnText, diag.dllOk ? UITheme.COLOR_ACCENT_SECONDARY : UITheme.COLOR_ACCENT_PRIMARY, diag.dllOk ? UITheme.COLOR_ACCENT_SECONDARY.brighter() : UITheme.COLOR_ACCENT_PRIMARY.brighter(), 16);
        btnGo.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnGo.setForeground(Color.WHITE);
        btnGo.setPreferredSize(new Dimension(500, 48));
        btnGo.setMaximumSize(new Dimension(500, 48));
        btnGo.setAlignmentX(Component.CENTER_ALIGNMENT);'''

content = content.replace(old_preflight, new_preflight)

# Fix the end of buildPreFlightScreen - change footer text and add boxOuter to wrapper instead of box
old_preflight_end = '''        JLabel warning = new JLabel("UAS Pemrograman Berorientasi Objek \u2014 Teknik Informatika");
        warning.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        warning.setForeground(COLOR_TEXT_MUTED);
        warning.setAlignmentX(Component.CENTER_ALIGNMENT);
        box.add(Box.createRigidArea(new Dimension(0, 20)));
        box.add(warning);

        wrapper.add(box);
        return wrapper;
    }'''

new_preflight_end = '''        JLabel warning = new JLabel("UAS Pemrograman Berorientasi Objek \u2014 Teknik Informatika");
        warning.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        warning.setForeground(UITheme.COLOR_TEXT_SECONDARY);
        warning.setAlignmentX(Component.CENTER_ALIGNMENT);
        box.add(Box.createRigidArea(new Dimension(0, 20)));
        box.add(warning);

        wrapper.add(boxOuter);
        return wrapper;
    }'''
content = content.replace(old_preflight_end, new_preflight_end)

# Fix buildDiagnosticPanel to use Light Mode colors
old_diag_panel = '''    private JPanel buildDiagnosticPanel(String titleText, String statusText, boolean isOk) {
        ModernCard rowCard = new ModernCard(16, COLOR_CARD_LIGHT, COLOR_BORDER, 1);
        rowCard.setLayout(new BorderLayout(15, 0));
        rowCard.setBorder(new EmptyBorder(12, 18, 12, 18));
        rowCard.setMaximumSize(new Dimension(500, 60));

        // Check Title
        JLabel title = new JLabel(titleText);
        title.setFont(new Font("Segoe UI Semibold", Font.BOLD, 13));
        title.setForeground(COLOR_TEXT_PRIMARY);
        rowCard.add(title, BorderLayout.WEST);

        // Check Status Indicator
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        rightPanel.setOpaque(false);

        JLabel statusLabel = new JLabel(statusText);
        statusLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        statusLabel.setForeground(isOk ? COLOR_GREEN : COLOR_ACCENT);

        // Neon dot indicator
        JPanel dot = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(isOk ? COLOR_GREEN : COLOR_ACCENT);
                g2.fillOval(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
        dot.setPreferredSize(new Dimension(10, 10));
        dot.setOpaque(false);

        rightPanel.add(statusLabel);
        rightPanel.add(dot);
        rowCard.add(rightPanel, BorderLayout.EAST);

        return rowCard;
    }'''

new_diag_panel = '''    private JPanel buildDiagnosticPanel(String titleText, String statusText, boolean isOk) {
        ModernCard rowCard = new ModernCard(16, UITheme.COLOR_BG_INPUT, new Color(209, 213, 219), 1);
        rowCard.setLayout(new BorderLayout(15, 0));
        rowCard.setBorder(new EmptyBorder(14, 18, 14, 18));
        rowCard.setMaximumSize(new Dimension(540, 60));
        rowCard.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Check Title
        JLabel title = new JLabel(titleText);
        title.setFont(new Font("Segoe UI Semibold", Font.BOLD, 13));
        title.setForeground(UITheme.COLOR_TEXT_PRIMARY);
        rowCard.add(title, BorderLayout.WEST);

        // Check Status Indicator
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        rightPanel.setOpaque(false);

        JLabel statusLabel = new JLabel(statusText);
        statusLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        statusLabel.setForeground(isOk ? UITheme.COLOR_ACCENT_SECONDARY : UITheme.COLOR_ACCENT_PRIMARY);

        // Dot indicator
        JPanel dot = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(isOk ? UITheme.COLOR_ACCENT_SECONDARY : UITheme.COLOR_ACCENT_PRIMARY);
                g2.fillOval(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
        dot.setPreferredSize(new Dimension(10, 10));
        dot.setOpaque(false);

        rightPanel.add(statusLabel);
        rightPanel.add(dot);
        rowCard.add(rightPanel, BorderLayout.EAST);

        return rowCard;
    }'''
content = content.replace(old_diag_panel, new_diag_panel)


# =============================================================================
# Also fix the main app navbar and workspace to use Light Mode colors
# =============================================================================

old_main_app = '''    private JPanel buildMainAppScreen() {
        JPanel workspace = new JPanel(new BorderLayout());
        workspace.setBackground(COLOR_BG);

        // Header Navigation Bar
        JPanel navBar = new JPanel(new BorderLayout());
        navBar.setBackground(COLOR_CARD);
        navBar.setBorder(new EmptyBorder(16, 24, 16, 24));

        // Logo
        JLabel appLogo = new JLabel("MIE GACOAN");
        appLogo.setFont(new Font("Segoe UI Black", Font.BOLD, 24));
        appLogo.setForeground(COLOR_ACCENT);
        navBar.add(appLogo, BorderLayout.WEST);'''

new_main_app = '''    private JPanel buildMainAppScreen() {
        JPanel workspace = new JPanel(new BorderLayout());
        workspace.setBackground(UITheme.COLOR_BG_APP);

        // Header Navigation Bar
        JPanel navBar = new JPanel(new BorderLayout());
        navBar.setBackground(UITheme.COLOR_BG_CARD);
        navBar.setBorder(new EmptyBorder(16, 24, 16, 24));

        // Logo
        JLabel appLogo = new JLabel("MIE GACOAN");
        appLogo.setFont(new Font("Segoe UI Black", Font.BOLD, 24));
        appLogo.setForeground(UITheme.COLOR_ACCENT_PRIMARY);
        navBar.add(appLogo, BorderLayout.WEST);'''
content = content.replace(old_main_app, new_main_app)

# Fix tab nav buttons to use Light Mode
old_tab_btns = '''        ModernButton btnTabPelanggan = new ModernButton("PESAN MANDIRI", COLOR_ACCENT, COLOR_ACCENT_HOVER, 10);
        btnTabPelanggan.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnTabPelanggan.setPreferredSize(new Dimension(140, 36));

        ModernButton btnTabKds = new ModernButton("KITCHEN DISPLAY (KDS)", COLOR_CARD_LIGHT, COLOR_BORDER, 10);
        btnTabKds.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnTabKds.setPreferredSize(new Dimension(180, 36));'''

new_tab_btns = '''        ModernButton btnTabPelanggan = new ModernButton("PESAN MANDIRI", UITheme.COLOR_ACCENT_PRIMARY, UITheme.COLOR_ACCENT_PRIMARY.darker(), 10);
        btnTabPelanggan.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnTabPelanggan.setForeground(Color.WHITE);
        btnTabPelanggan.setPreferredSize(new Dimension(140, 36));

        ModernButton btnTabKds = new ModernButton("KITCHEN DISPLAY (KDS)", UITheme.COLOR_BG_INPUT, UITheme.COLOR_BG_CARD, 10);
        btnTabKds.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnTabKds.setForeground(UITheme.COLOR_TEXT_PRIMARY);
        btnTabKds.setPreferredSize(new Dimension(180, 36));'''
content = content.replace(old_tab_btns, new_tab_btns)

# Fix tab switching colors
old_tab_switch1 = '''        btnTabPelanggan.addActionListener(e -> {
            appTabbedPane.setSelectedIndex(0);
            btnTabPelanggan.setBackground(COLOR_ACCENT);
            btnTabPelanggan.setNormalBg(COLOR_ACCENT);
            btnTabKds.setBackground(COLOR_CARD_LIGHT);
            btnTabKds.setNormalBg(COLOR_CARD_LIGHT);
        });

        btnTabKds.addActionListener(e -> {
            appTabbedPane.setSelectedIndex(1);
            btnTabKds.setBackground(COLOR_ACCENT);
            btnTabKds.setNormalBg(COLOR_ACCENT);
            btnTabPelanggan.setBackground(COLOR_CARD_LIGHT);
            btnTabPelanggan.setNormalBg(COLOR_CARD_LIGHT);
        });'''

new_tab_switch1 = '''        btnTabPelanggan.addActionListener(e -> {
            appTabbedPane.setSelectedIndex(0);
            btnTabPelanggan.setBackground(UITheme.COLOR_ACCENT_PRIMARY);
            btnTabPelanggan.setNormalBg(UITheme.COLOR_ACCENT_PRIMARY);
            btnTabPelanggan.setForeground(Color.WHITE);
            btnTabKds.setBackground(UITheme.COLOR_BG_INPUT);
            btnTabKds.setNormalBg(UITheme.COLOR_BG_INPUT);
            btnTabKds.setForeground(UITheme.COLOR_TEXT_PRIMARY);
        });

        btnTabKds.addActionListener(e -> {
            appTabbedPane.setSelectedIndex(1);
            btnTabKds.setBackground(UITheme.COLOR_ACCENT_PRIMARY);
            btnTabKds.setNormalBg(UITheme.COLOR_ACCENT_PRIMARY);
            btnTabKds.setForeground(Color.WHITE);
            btnTabPelanggan.setBackground(UITheme.COLOR_BG_INPUT);
            btnTabPelanggan.setNormalBg(UITheme.COLOR_BG_INPUT);
            btnTabPelanggan.setForeground(UITheme.COLOR_TEXT_PRIMARY);
        });'''
content = content.replace(old_tab_switch1, new_tab_switch1)

# Fix the JTabbedPane background
content = content.replace('appTabbedPane.setBackground(COLOR_BG);', 'appTabbedPane.setBackground(UITheme.COLOR_BG_APP);')

# Fix mainCardPanel background
content = content.replace('mainCardPanel.setBackground(COLOR_BG);', 'mainCardPanel.setBackground(UITheme.COLOR_BG_APP);')

# Fix the UIManager defaults at the bottom to use Light Mode colors
old_uimanager = '''        // Set beautiful dark theme properties for defaults
        UIManager.put("TabbedPane.shadow", COLOR_BORDER);
        UIManager.put("TabbedPane.darkShadow", COLOR_BG);
        UIManager.put("TabbedPane.light", COLOR_BG);
        UIManager.put("TabbedPane.highlight", COLOR_BORDER);
        UIManager.put("TableHeader.cellBorder", BorderFactory.createLineBorder(COLOR_BORDER, 1));
        UIManager.put("Table.gridColor", COLOR_BORDER);'''

new_uimanager = '''        // Set Light Mode theme properties for defaults
        UIManager.put("TabbedPane.shadow", UITheme.COLOR_BG_APP);
        UIManager.put("TabbedPane.darkShadow", UITheme.COLOR_BG_APP);
        UIManager.put("TabbedPane.light", UITheme.COLOR_BG_APP);
        UIManager.put("TabbedPane.highlight", UITheme.COLOR_BG_APP);
        UIManager.put("TableHeader.cellBorder", BorderFactory.createLineBorder(new Color(209, 213, 219), 1));
        UIManager.put("Table.gridColor", new Color(229, 231, 235));'''
content = content.replace(old_uimanager, new_uimanager)

# Fix unlock scanner - still uses raw COLOR_ACCENT
old_unlock = '''            btnScanQr.setNormalBg(COLOR_ACCENT);
            btnScanQr.setBackground(COLOR_ACCENT);'''
new_unlock = '''            btnScanQr.setNormalBg(UITheme.COLOR_ACCENT_PRIMARY);
            btnScanQr.setBackground(UITheme.COLOR_ACCENT_PRIMARY);
            btnScanQr.setForeground(Color.WHITE);'''
content = content.replace(old_unlock, new_unlock)

# Fix scrollbar thumb to use a visible light gray
old_scrollbar = '''            g2.setColor(COLOR_BORDER);
            g2.fillRoundRect(thumbBounds.x + 2, thumbBounds.y + 2, thumbBounds.width - 4, thumbBounds.height - 4, 8, 8);'''
new_scrollbar = '''            g2.setColor(new Color(180, 180, 195));
            g2.fillRoundRect(thumbBounds.x + 2, thumbBounds.y + 2, thumbBounds.width - 4, thumbBounds.height - 4, 8, 8);'''
content = content.replace(old_scrollbar, new_scrollbar)

with open(app_path, 'w', encoding='utf-8') as f:
    f.write(content)
print('All 3 bugs fixed successfully.')
