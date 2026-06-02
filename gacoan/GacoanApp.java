package gacoan;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicScrollBarUI;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class GacoanApp extends JFrame {

    // Color Palette - Sleek Modern Dark Mode (Gacoan Premium Theme)
    private static final Color COLOR_BG = new Color(18, 18, 24);        // Dark Charcoal
    private static final Color COLOR_CARD = new Color(28, 28, 38);      // Sleek Grey
    private static final Color COLOR_CARD_LIGHT = new Color(40, 40, 52); // Accent Card
    private static final Color COLOR_ACCENT = new Color(241, 90, 36);    // Neon Gacoan Orange
    private static final Color COLOR_ACCENT_HOVER = new Color(255, 115, 60);
    private static final Color COLOR_TEXT_PRIMARY = new Color(255, 255, 255);
    private static final Color COLOR_TEXT_MUTED = new Color(150, 150, 165);
    private static final Color COLOR_GREEN = new Color(34, 197, 94);     // Success Emerald
    private static final Color COLOR_CYAN = new Color(6, 182, 212);      // Dimsum Accent
    private static final Color COLOR_PURPLE = new Color(168, 85, 247);   // Minuman Accent
    private static final Color COLOR_BORDER = new Color(48, 48, 62);

    // Business Variables
    private int nomorMejaLocked = 0;
    private final List<Menu> daftarMenu = new ArrayList<>();
    private final List<ItemPesanan> keranjangBelanja = new ArrayList<>();
    private final List<TransaksiPesanan> antreanDapur = new ArrayList<>();
    private final List<TransaksiPesanan> riwayatPanggilan = new ArrayList<>();

    // JNI Calculation Engine
    private final GacoanEngine billingEngine = new GacoanEngine();

    // UI Navigation Components
    private CardLayout mainCardLayout;
    private JPanel mainCardPanel;
    private JTabbedPane appTabbedPane;

    // Cart UI elements
    private DefaultTableModel modelTabelKeranjang;
    private JTable tableKeranjang;
    private JLabel lblSubtotal;
    private JLabel lblTax;
    private JLabel lblTotal;
    
    // Grid list reference for re-rendering on category filtering
    private JPanel panelGridMenu;
    private String currentCategoryFilter = "Semua";

    // Diagnostic/QR elements
    private JLabel lblTableStatus;
    private ModernButton btnScanQr;

    // KDS Queue Lists
    private JPanel panelKdsActiveQueue;
    private JPanel panelKdsHistoryQueue;

    public GacoanApp() {
        // Initialize iconic menus
        initIconicMenus();

        // Window properties
        setTitle("MIE GACOAN - Self Ordering Kiosk & Kitchen Display System");
        setSize(1240, 820);
        setMinimumSize(new Dimension(1024, 768));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Core Layout - Multi Screen
        mainCardLayout = new CardLayout();
        mainCardPanel = new JPanel(mainCardLayout);
        mainCardPanel.setBackground(COLOR_BG);

        // 1. Screen 1: Diagnostic Pre-Flight Checker Screen
        JPanel panelPreFlight = buildPreFlightScreen();
        // 2. Screen 2: Main Application Screen
        JPanel panelMainApp = buildMainAppScreen();

        mainCardPanel.add(panelPreFlight, "PRE_FLIGHT");
        mainCardPanel.add(panelMainApp, "MAIN_APP");

        add(mainCardPanel);
        mainCardLayout.show(mainCardPanel, "PRE_FLIGHT");
    }

    private void initIconicMenus() {
        daftarMenu.add(new Menu("MIE_HOMPIMPA", "Mie Hompimpa", 11000, "Makanan"));
        daftarMenu.add(new Menu("MIE_GACOAN", "Mie Gacoan", 11000, "Makanan"));
        daftarMenu.add(new Menu("DIMSUM_SIOMAY", "Dimsum Siomay", 10000, "Dimsum"));
        daftarMenu.add(new Menu("DIMSUM_RAMBUTAN", "Udang Rambutan", 10000, "Dimsum"));
        daftarMenu.add(new Menu("ES_GOBAC_SODOR", "Es Gobak Sodor", 9000, "Minuman"));
        daftarMenu.add(new Menu("ES_TEKLEK", "Es Teklek", 9000, "Minuman"));
    }

    // =========================================================================
    // GRAPHICAL CUSTOM SWING COMPONENT CLASSES
    // =========================================================================

    // 1. Premium Rounded Card Panel
    static class ModernCard extends JPanel {
        private int radius;
        private Color bgColor;
        private Color borderColor;
        private int borderThickness;

        public ModernCard(int radius, Color bgColor, Color borderColor, int borderThickness) {
            this.radius = radius;
            this.bgColor = bgColor;
            this.borderColor = borderColor;
            this.borderThickness = borderThickness;
            setOpaque(false);
        }

        public ModernCard(int radius, Color bgColor) {
            this(radius, bgColor, null, 0);
        }

        public void setBgColor(Color bgColor) {
            this.bgColor = bgColor;
            repaint();
        }

        public void setBorderColor(Color borderColor) {
            this.borderColor = borderColor;
            repaint();
        }

        @Override
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
        }
    }

    // 2. High-Performance Custom Button with Animations
    static class ModernButton extends JButton {
        private Color normalBg;
        private Color hoverBg;
        private Color pressedBg;
        private int radius;

        public ModernButton(String text, Color bg, Color hover, int radius) {
            super(text);
            this.normalBg = bg;
            this.hoverBg = hover;
            this.pressedBg = bg.darker();
            this.radius = radius;

            setContentAreaFilled(false);
            setFocusPainted(false);
            setBorderPainted(false);
            setOpaque(false);
            setForeground(COLOR_TEXT_PRIMARY);
            setFont(new Font("Segoe UI", Font.BOLD, 12));
            setCursor(new Cursor(Cursor.HAND_CURSOR));

            // Visual feedback on hover/click
            addMouseListener(new MouseAdapter() {
                private boolean hover = false;
                @Override
                public void mouseEntered(MouseEvent e) {
                    hover = true;
                    repaint();
                }
                @Override
                public void mouseExited(MouseEvent e) {
                    hover = false;
                    repaint();
                }
            });
        }

        public void setNormalBg(Color bg) {
            this.normalBg = bg;
            this.pressedBg = bg.darker();
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            if (getModel().isPressed()) {
                g2.setColor(pressedBg);
            } else if (getModel().isRollover()) {
                g2.setColor(hoverBg);
            } else {
                g2.setColor(normalBg);
            }

            g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);
            g2.dispose();

            // Render labels
            super.paintComponent(g);
        }
    }

    // 3. Flat Custom Text Field
    static class ModernTextField extends JTextField {
        public ModernTextField(String placeholder) {
            setBackground(COLOR_CARD_LIGHT);
            setForeground(COLOR_TEXT_PRIMARY);
            setCaretColor(COLOR_TEXT_PRIMARY);
            setFont(new Font("Segoe UI", Font.PLAIN, 12));
            setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(COLOR_BORDER, 1),
                    BorderFactory.createEmptyBorder(6, 12, 6, 12)
            ));
            
            // Set text hint / placeholder
            setText(placeholder);
            setForeground(COLOR_TEXT_MUTED);
            addFocusListener(new java.awt.event.FocusAdapter() {
                @Override
                public void focusGained(java.awt.event.FocusEvent evt) {
                    if (getText().equals(placeholder)) {
                        setText("");
                        setForeground(COLOR_TEXT_PRIMARY);
                    }
                }
                @Override
                public void focusLost(java.awt.event.FocusEvent evt) {
                    if (getText().isEmpty()) {
                        setText(placeholder);
                        setForeground(COLOR_TEXT_MUTED);
                    }
                }
            });
        }
    }

    // 4. Flat Custom JComboBox
    static class ModernComboBox<E> extends JComboBox<E> {
        public ModernComboBox() {
            setBackground(COLOR_CARD_LIGHT);
            setForeground(COLOR_TEXT_PRIMARY);
            setBorder(BorderFactory.createLineBorder(COLOR_BORDER, 1));
            setFocusable(false);
            setFont(new Font("Segoe UI", Font.PLAIN, 12));

            // Set custom basic UI to bypass native OS blue highlights and focus border
            setUI(new javax.swing.plaf.basic.BasicComboBoxUI() {
                @Override
                protected JButton createArrowButton() {
                    JButton btn = new JButton() {
                        @Override
                        protected void paintComponent(Graphics g) {
                            Graphics2D g2 = (Graphics2D) g.create();
                            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                            g2.setColor(COLOR_CARD_LIGHT);
                            g2.fillRect(0, 0, getWidth(), getHeight());
                            g2.setColor(COLOR_TEXT_MUTED);
                            int[] xPoints = {getWidth()/2 - 4, getWidth()/2, getWidth()/2 + 4};
                            int[] yPoints = {getHeight()/2 - 2, getHeight()/2 + 2, getHeight()/2 - 2};
                            g2.fillPolygon(xPoints, yPoints, 3);
                            g2.dispose();
                        }
                    };
                    btn.setBorderPainted(false);
                    btn.setContentAreaFilled(false);
                    btn.setFocusPainted(false);
                    return btn;
                }

                @Override
                public void paintCurrentValueBackground(Graphics g, Rectangle bounds, boolean hasFocus) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setColor(COLOR_CARD_LIGHT);
                    g2.fillRect(bounds.x, bounds.y, bounds.width, bounds.height);
                    g2.dispose();
                }
            });

            // Custom Item Renderer
            setRenderer(new DefaultListCellRenderer() {
                @Override
                public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                    JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                    label.setOpaque(true);
                    label.setBackground(isSelected ? COLOR_ACCENT : COLOR_CARD_LIGHT);
                    label.setForeground(COLOR_TEXT_PRIMARY);
                    label.setBorder(new EmptyBorder(6, 10, 6, 10));
                    return label;
                }
            });

            // Modern dropdown list background fix
            try {
                Object child = getAccessibleContext().getAccessibleChild(0);
                if (child instanceof javax.swing.plaf.basic.ComboPopup) {
                    JList<?> list = ((javax.swing.plaf.basic.ComboPopup) child).getList();
                    list.setBackground(COLOR_CARD_LIGHT);
                    list.setForeground(COLOR_TEXT_PRIMARY);
                }
            } catch (Exception e) {
                // Ignore fallback
            }
        }
    }

    // 5. Custom Thin Modern Scrollbar UI
    static class ModernScrollbarUI extends BasicScrollBarUI {
        @Override
        protected JButton createDecreaseButton(int orientation) {
            return createZeroButton();
        }

        @Override
        protected JButton createIncreaseButton(int orientation) {
            return createZeroButton();
        }

        private JButton createZeroButton() {
            JButton button = new JButton();
            button.setPreferredSize(new Dimension(0, 0));
            button.setMinimumSize(new Dimension(0, 0));
            button.setMaximumSize(new Dimension(0, 0));
            return button;
        }

        @Override
        protected void paintTrack(Graphics g, JComponent c, Rectangle trackBounds) {
            // Keep background transparent
        }

        @Override
        protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(COLOR_BORDER);
            g2.fillRoundRect(thumbBounds.x + 2, thumbBounds.y + 2, thumbBounds.width - 4, thumbBounds.height - 4, 8, 8);
            g2.dispose();
        }
    }

    // 6. Custom Modern Table Cell Renderer (To fix white background bug on Windows LF)
    static class ModernTableCellRenderer extends DefaultTableCellRenderer {
        public ModernTableCellRenderer(int alignment) {
            setHorizontalAlignment(alignment);
            setOpaque(true);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            c.setBackground(isSelected ? COLOR_ACCENT : COLOR_CARD);
            c.setForeground(COLOR_TEXT_PRIMARY);
            setBorder(new EmptyBorder(4, 10, 4, 10));
            return c;
        }
    }

    // 7. Custom Modern Table Header Renderer (To fix white background bug on Windows LF)
    static class ModernTableHeaderRenderer extends DefaultTableCellRenderer {
        public ModernTableHeaderRenderer() {
            setOpaque(true);
            setBackground(COLOR_CARD_LIGHT);
            setForeground(COLOR_TEXT_PRIMARY);
            setFont(new Font("Segoe UI", Font.BOLD, 12));
            setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COLOR_BORDER, 1),
                BorderFactory.createEmptyBorder(6, 12, 6, 12)
            ));
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            if (column == 0) setHorizontalAlignment(JLabel.LEFT);
            else if (column == 1) setHorizontalAlignment(JLabel.CENTER);
            else setHorizontalAlignment(JLabel.RIGHT);
            return this;
        }
    }

    // =========================================================================
    // SCREEN 1: PRE-FLIGHT COMPATIBILITY CHECKER (MODERN DESIGN)
    // =========================================================================
    private JPanel buildPreFlightScreen() {
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
        btnGo.setAlignmentX(Component.CENTER_ALIGNMENT);

        btnGo.addActionListener(e -> {
            if (!diag.dllOk) {
                JOptionPane.showMessageDialog(GacoanApp.this, 
                        "Sistem JNI GacoanEngine.dll gagal dimuat!\n" +
                        "Aplikasi akan dijalankan menggunakan program simulasi internal Java (Fallback).\n\n" +
                        "Untuk hasil optimal, silakan jalankan 'compile.bat' dari terminal\n" +
                        "guna merakit pustaka C++ dll secara otomatis.", 
                        "Pre-flight Library Fallback", JOptionPane.WARNING_MESSAGE);
            }
            mainCardLayout.show(mainCardPanel, "MAIN_APP");
        });

        box.add(btnGo);

        JLabel warning = new JLabel("UAS Pemrograman Berorientasi Objek — Teknik Informatika");
        warning.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        warning.setForeground(COLOR_TEXT_MUTED);
        warning.setAlignmentX(Component.CENTER_ALIGNMENT);
        box.add(Box.createRigidArea(new Dimension(0, 20)));
        box.add(warning);

        wrapper.add(box);
        return wrapper;
    }

    private JPanel buildDiagnosticPanel(String titleText, String statusText, boolean isOk) {
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
    }

    // =========================================================================
    // SCREEN 2: MAIN APPLICATION WORKSPACE
    // =========================================================================
    private JPanel buildMainAppScreen() {
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
        navBar.add(appLogo, BorderLayout.WEST);

        // Custom High-End Flat Tabs Buttons (Eliminating standard Java tabs)
        JPanel tabButtonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        tabButtonsPanel.setOpaque(false);

        ModernButton btnTabPelanggan = new ModernButton("PESAN MANDIRI", COLOR_ACCENT, COLOR_ACCENT_HOVER, 10);
        btnTabPelanggan.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnTabPelanggan.setPreferredSize(new Dimension(140, 36));

        ModernButton btnTabKds = new ModernButton("KITCHEN DISPLAY (KDS)", COLOR_CARD_LIGHT, COLOR_BORDER, 10);
        btnTabKds.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnTabKds.setPreferredSize(new Dimension(180, 36));

        btnTabPelanggan.addActionListener(e -> {
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
        });

        tabButtonsPanel.add(btnTabPelanggan);
        tabButtonsPanel.add(btnTabKds);
        navBar.add(tabButtonsPanel, BorderLayout.EAST);

        workspace.add(navBar, BorderLayout.NORTH);

        // JTabbedPane inside (Hide actual tabs to show our custom styled flat buttons)
        appTabbedPane = new JTabbedPane();
        appTabbedPane.setUI(new javax.swing.plaf.basic.BasicTabbedPaneUI() {
            @Override
            protected int calculateTabAreaHeight(int tabPlacement, int runCount, int maxTabHeight) {
                return 0; // Hides JTabbedPane default ugly top tabs completely!
            }
        });
        appTabbedPane.setBackground(COLOR_BG);
        appTabbedPane.setBorder(null);

        // Customer Panel
        JPanel panelPelanggan = buildCustomerOrderingTab();
        // Kitchen Panel
        JPanel panelDapur = buildKitchenTab();

        appTabbedPane.addTab(null, panelPelanggan);
        appTabbedPane.addTab(null, panelDapur);

        workspace.add(appTabbedPane, BorderLayout.CENTER);

        return workspace;
    }

    // =========================================================================
    // SUBPANEL: CUSTOMER SELF-ORDERING PORTAL
    // =========================================================================
    private JPanel buildCustomerOrderingTab() {
        JPanel tab = new JPanel(new BorderLayout(20, 20));
        tab.setBackground(COLOR_BG);
        tab.setBorder(new EmptyBorder(20, 24, 20, 24));

        // Subsystem: Left Side (QR Scan simulation & Catalog), Right Side (Floating Bill Cart)
        JPanel leftLayout = new JPanel(new BorderLayout(15, 15));
        leftLayout.setOpaque(false);

        // A. Floating QR Scan Simulation Card
        ModernCard qrCard = new ModernCard(18, COLOR_CARD, COLOR_BORDER, 1);
        qrCard.setLayout(new BorderLayout(15, 15));
        qrCard.setBorder(new EmptyBorder(16, 20, 16, 20));

        // Left Label
        lblTableStatus = new JLabel("SILAKAN SCAN BARCODE QR PADA MEJA KIOSK");
        lblTableStatus.setFont(new Font("Segoe UI Semibold", Font.BOLD, 13));
        lblTableStatus.setForeground(COLOR_ACCENT);
        qrCard.add(lblTableStatus, BorderLayout.WEST);

        // Right simulation action button
        btnScanQr = new ModernButton("PINDAI BARCODE QR MEJA", COLOR_ACCENT, COLOR_ACCENT_HOVER, 10);
        btnScanQr.setFont(new Font("Segoe UI", Font.BOLD, 11));
        btnScanQr.setPreferredSize(new Dimension(180, 32));
        btnScanQr.addActionListener(e -> {
            Random r = new Random();
            nomorMejaLocked = r.nextInt(25) + 1; // 1 to 25
            lblTableStatus.setText("QR TERVERIFIKASI MEJA: MEJA " + nomorMejaLocked + " (TERKUNCI ✓)");
            lblTableStatus.setForeground(COLOR_GREEN);
            btnScanQr.setEnabled(false);
            btnScanQr.setText("QR TERVERIFIKASI");
            btnScanQr.setNormalBg(COLOR_CARD_LIGHT);
            btnScanQr.setBackground(COLOR_CARD_LIGHT);
            revalidate();
            repaint();
        });
        qrCard.add(btnScanQr, BorderLayout.EAST);

        leftLayout.add(qrCard, BorderLayout.NORTH);

        // B. Catalog Panel (Filter + Food Grid Cards)
        JPanel catalogPanel = new JPanel(new BorderLayout(15, 15));
        catalogPanel.setOpaque(false);

        // Rounded Category filter buttons
        JPanel filterRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        filterRow.setOpaque(false);

        String[] cats = {"Semua", "Makanan", "Dimsum", "Minuman"};
        for (String c : cats) {
            ModernButton btnF = new ModernButton(c.toUpperCase(), COLOR_CARD, COLOR_CARD_LIGHT, 12);
            btnF.setFont(new Font("Segoe UI", Font.BOLD, 11));
            btnF.setBorder(new EmptyBorder(6, 18, 6, 18));
            btnF.setPreferredSize(new Dimension(110, 32));
            btnF.addActionListener(ev -> {
                currentCategoryFilter = c;
                renderModernCatalogGrid();
            });
            filterRow.add(btnF);
        }
        catalogPanel.add(filterRow, BorderLayout.NORTH);

        // Food Grid Scroll Pane
        panelGridMenu = new JPanel(new GridLayout(0, 2, 16, 16));
        panelGridMenu.setBackground(COLOR_BG);

        JScrollPane catalogScroll = new JScrollPane(panelGridMenu);
        catalogScroll.setBorder(null);
        catalogScroll.setOpaque(false);
        catalogScroll.getViewport().setOpaque(false);
        catalogScroll.getVerticalScrollBar().setUI(new ModernScrollbarUI());
        catalogScroll.getVerticalScrollBar().setUnitIncrement(18);

        catalogPanel.add(catalogScroll, BorderLayout.CENTER);
        leftLayout.add(catalogPanel, BorderLayout.CENTER);

        renderModernCatalogGrid(); // Initial display
        tab.add(leftLayout, BorderLayout.CENTER);

        // C. Sidebar Floating Cart Panel
        ModernCard cartCard = new ModernCard(20, COLOR_CARD, COLOR_BORDER, 1);
        cartCard.setLayout(new BorderLayout(20, 20));
        cartCard.setPreferredSize(new Dimension(390, 0));
        cartCard.setBorder(new EmptyBorder(24, 20, 24, 20));

        JLabel lblCartTitle = new JLabel("Detail Keranjang Belanja");
        lblCartTitle.setFont(new Font("Segoe UI Black", Font.BOLD, 18));
        lblCartTitle.setForeground(COLOR_TEXT_PRIMARY);
        cartCard.add(lblCartTitle, BorderLayout.NORTH);

        // Table Model Flat Design without borders and lines
        String[] columns = {"Pesanan", "Porsi", "Harga"};
        modelTabelKeranjang = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tableKeranjang = new JTable(modelTabelKeranjang);
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
        cartCard.add(cartScroll, BorderLayout.CENTER);

        // Billing totals panel
        JPanel totalsPanel = new JPanel();
        totalsPanel.setLayout(new BoxLayout(totalsPanel, BoxLayout.Y_AXIS));
        totalsPanel.setOpaque(false);

        lblSubtotal = new JLabel("Subtotal: Rp 0");
        lblSubtotal.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblSubtotal.setForeground(COLOR_TEXT_MUTED);

        lblTax = new JLabel("Pajak Restoran (PB1 10%): Rp 0");
        lblTax.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblTax.setForeground(COLOR_TEXT_MUTED);

        lblTotal = new JLabel("TOTAL TAGIHAN: Rp 0");
        lblTotal.setFont(new Font("Segoe UI Black", Font.BOLD, 20));
        lblTotal.setForeground(COLOR_GREEN);

        totalsPanel.add(lblSubtotal);
        totalsPanel.add(Box.createRigidArea(new Dimension(0, 4)));
        totalsPanel.add(lblTax);
        totalsPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        totalsPanel.add(lblTotal);
        totalsPanel.add(Box.createRigidArea(new Dimension(0, 16)));

        // Large Checkout Button
        ModernButton btnCheckout = new ModernButton("KONFIRMASI BAYAR & STRUK", COLOR_ACCENT, COLOR_ACCENT_HOVER, 14);
        btnCheckout.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnCheckout.setPreferredSize(new Dimension(0, 48));
        btnCheckout.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));
        btnCheckout.addActionListener(e -> {
            if (nomorMejaLocked == 0) {
                JOptionPane.showMessageDialog(this, 
                        "Silakan lakukan simulasi scan meja terlebih dahulu!", 
                        "QR Scan Required", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (keranjangBelanja.isEmpty()) {
                JOptionPane.showMessageDialog(this, 
                        "Keranjang belanja masih kosong!", 
                        "Empty Cart", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Create Transaction Object
            String timeStamp = new SimpleDateFormat("yyyyMMdd-HHmmss").format(new Date());
            String idNota = "GCN-" + timeStamp;
            
            TransaksiPesanan transaksi = new TransaksiPesanan(idNota, nomorMejaLocked);
            for (ItemPesanan it : keranjangBelanja) {
                transaksi.tambahItem(it);
            }

            // Compute receipt ASCII via JNI C++ DLL
            String receiptText = "";
            if (GacoanEngine.isLibraryLoaded()) {
                try {
                    receiptText = billingEngine.hitungNota(transaksi);
                } catch (Exception ex) {
                    receiptText = generateJavaFallbackNota(transaksi);
                }
            } else {
                receiptText = generateJavaFallbackNota(transaksi);
            }

            // Add order to Kitchen KDS queue
            antreanDapur.add(transaksi);

            // Pop gorgeous receipt Dialog
            showAnimatedReceiptDialog(receiptText);

            // Reset cart state
            keranjangBelanja.clear();
            modelTabelKeranjang.setRowCount(0);
            updateCartSummary();

            // Unlock scanner UI
            nomorMejaLocked = 0;
            btnScanQr.setEnabled(true);
            btnScanQr.setNormalBg(COLOR_ACCENT);
            btnScanQr.setBackground(COLOR_ACCENT);
            btnScanQr.setText("PINDAI BARCODE QR MEJA");
            lblTableStatus.setText("SILAKAN SCAN BARCODE QR PADA MEJA KIOSK");
            lblTableStatus.setForeground(COLOR_ACCENT);

            // Sync kitchen panels
            refreshKdsViews();
        });

        totalsPanel.add(btnCheckout);
        cartCard.add(totalsPanel, BorderLayout.SOUTH);

        tab.add(cartCard, BorderLayout.EAST);

        return tab;
    }

    private void renderModernCatalogGrid() {
        panelGridMenu.removeAll();

        for (Menu item : daftarMenu) {
            if (!currentCategoryFilter.equals("Semua") && !item.getKategori().equalsIgnoreCase(currentCategoryFilter)) {
                continue;
            }

            // Clean modern product card
            ModernCard card = new ModernCard(18, COLOR_CARD, COLOR_BORDER, 1);
            card.setLayout(new BorderLayout(15, 12));
            card.setBorder(new EmptyBorder(16, 16, 16, 16));

            // Product Details (Title, Category Tag, Price)
            JPanel top = new JPanel(new BorderLayout(5, 5));
            top.setOpaque(false);

            JPanel textWrapper = new JPanel(new GridLayout(2, 1, 2, 2));
            textWrapper.setOpaque(false);

            JLabel name = new JLabel(item.getNama());
            name.setFont(new Font("Segoe UI Semibold", Font.BOLD, 15));
            name.setForeground(COLOR_TEXT_PRIMARY);
            textWrapper.add(name);

            // Styled Category Pill
            JPanel tagWrapper = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
            tagWrapper.setOpaque(false);

            Color tagColor = COLOR_ACCENT;
            if (item.getKategori().equalsIgnoreCase("Dimsum")) tagColor = COLOR_CYAN;
            if (item.getKategori().equalsIgnoreCase("Minuman")) tagColor = COLOR_PURPLE;

            JLabel lblCat = new JLabel("  " + item.getKategori().toUpperCase() + "  ");
            lblCat.setFont(new Font("Segoe UI Black", Font.BOLD, 9));
            lblCat.setForeground(COLOR_TEXT_PRIMARY);
            
            final Color tColor = tagColor;
            JPanel pill = new JPanel() {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(tColor);
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                    g2.dispose();
                }
            };
            pill.setLayout(new BorderLayout());
            pill.setOpaque(false);
            pill.add(lblCat, BorderLayout.CENTER);
            tagWrapper.add(pill);
            textWrapper.add(tagWrapper);

            top.add(textWrapper, BorderLayout.WEST);

            // Large Green Price Label
            String priceText = item.getKategori().equalsIgnoreCase("Makanan") ? "Rp 11K-13K" : (item.getKategori().equalsIgnoreCase("Dimsum") ? "Rp 10K" : "Rp 9K");
            JLabel price = new JLabel(priceText);
            price.setFont(new Font("Segoe UI Black", Font.BOLD, 15));
            price.setForeground(COLOR_GREEN);
            top.add(price, BorderLayout.EAST);

            card.add(top, BorderLayout.NORTH);

            // Choice parameters panel (Notes and level selection)
            boolean isFood = item.getKategori().equalsIgnoreCase("Makanan");
            JPanel optionsPanel = new JPanel(new GridLayout(isFood ? 4 : 2, 1, 4, 4));
            optionsPanel.setOpaque(false);

            JLabel lblNotes = new JLabel("Catatan Konsumen:");
            lblNotes.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 12));
            lblNotes.setForeground(COLOR_TEXT_MUTED);

            ModernTextField txtNote = new ModernTextField("Contoh: Tanpa kuah, sendok...");

            ModernComboBox<String> cmbLvl = new ModernComboBox<>();
            if (isFood) {
                JLabel lblLvl = new JLabel("Pilih Level Pedas:");
                lblLvl.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 12));
                lblLvl.setForeground(COLOR_TEXT_MUTED);

                for (int i = 0; i <= 8; i++) {
                    if (i == 0) cmbLvl.addItem("Original (Lvl 0) - Rp 11.000");
                    else if (i <= 4) cmbLvl.addItem("Level " + i + " - Rp 11.000");
                    else cmbLvl.addItem("Level " + i + " - Rp 13.000");
                }

                optionsPanel.add(lblLvl);
                optionsPanel.add(cmbLvl);
            }

            optionsPanel.add(lblNotes);
            optionsPanel.add(txtNote);

            card.add(optionsPanel, BorderLayout.CENTER);

            // Card Bottom: Large add button
            ModernButton btnAdd = new ModernButton("TAMBAHKAN KAN", COLOR_ACCENT, COLOR_ACCENT_HOVER, 10);
            btnAdd.setFont(new Font("Segoe UI", Font.BOLD, 12));
            btnAdd.setPreferredSize(new Dimension(0, 36));
            btnAdd.addActionListener(e -> {
                int selectedLevel = isFood ? cmbLvl.getSelectedIndex() : 0;
                String userNote = txtNote.getText().trim();
                if (userNote.equals("Contoh: Tanpa kuah, sendok...")) {
                    userNote = "";
                }

                // Check cart for duplicates
                boolean duplicate = false;
                for (ItemPesanan it : keranjangBelanja) {
                    if (it.getMenu().getId().equals(item.getId()) && it.getLevelPedas() == selectedLevel) {
                        it.setKuantitas(it.getKuantitas() + 1);
                        if (!userNote.isEmpty()) {
                            it.setCatatan(it.getCatatan() + "; " + userNote);
                        }
                        duplicate = true;
                        break;
                    }
                }

                if (!duplicate) {
                    double finalPrice = item.getHargaDasar();
                    if (isFood) {
                        finalPrice = (selectedLevel >= 5) ? 13000 : 11000;
                    } else if (item.getKategori().equalsIgnoreCase("Dimsum")) {
                        finalPrice = 10000;
                    } else if (item.getKategori().equalsIgnoreCase("Minuman")) {
                        finalPrice = 9000;
                    }

                    Menu localMenu = new Menu(item.getId(), item.getNama(), finalPrice, item.getKategori());
                    keranjangBelanja.add(new ItemPesanan(localMenu, 1, selectedLevel, userNote));
                }

                txtNote.setText("Contoh: Tanpa kuah, sendok...");
                txtNote.setForeground(COLOR_TEXT_MUTED);
                updateCartUI();
            });

            card.add(btnAdd, BorderLayout.SOUTH);

            panelGridMenu.add(card);
        }

        panelGridMenu.revalidate();
        panelGridMenu.repaint();
    }

    private void updateCartUI() {
        modelTabelKeranjang.setRowCount(0);
        for (ItemPesanan it : keranjangBelanja) {
            String title = it.getMenu().getNama();
            if (it.getMenu().getKategori().equalsIgnoreCase("Makanan")) {
                title += " (Lvl " + it.getLevelPedas() + ")";
            }
            double itemTotal = it.getKuantitas() * it.getMenu().getHargaDasar();
            modelTabelKeranjang.addRow(new Object[]{
                    title,
                    it.getKuantitas() + "x",
                    "Rp " + (long)itemTotal
            });
        }
        updateCartSummary();
    }

    private void updateCartSummary() {
        double subtotal = 0;
        for (ItemPesanan it : keranjangBelanja) {
            subtotal += it.getKuantitas() * it.getMenu().getHargaDasar();
        }
        double tax = subtotal * 0.10;
        double total = subtotal + tax;

        lblSubtotal.setText("Subtotal: Rp " + (long)subtotal);
        lblTax.setText("Pajak Restoran (PB1 10%): Rp " + (long)tax);
        lblTotal.setText("TOTAL TAGIHAN: Rp " + (long)total);
    }

    private void showAnimatedReceiptDialog(String formatStr) {
        JDialog dialog = new JDialog(this, "Nota Belanja Resmi Mie Gacoan", true);
        dialog.setSize(480, 580);
        dialog.setUndecorated(true);
        dialog.setShape(new RoundRectangle2D.Double(0, 0, 480, 580, 24, 24));
        dialog.setLocationRelativeTo(this);

        ModernCard container = new ModernCard(24, COLOR_BG, COLOR_BORDER, 2);
        container.setLayout(new BorderLayout(15, 15));
        container.setBorder(new EmptyBorder(24, 24, 24, 24));

        JLabel title = new JLabel("TRANSAKSI BERHASIL");
        title.setFont(new Font("Segoe UI Black", Font.BOLD, 18));
        title.setForeground(COLOR_GREEN);
        title.setHorizontalAlignment(JLabel.CENTER);
        container.add(title, BorderLayout.NORTH);

        // Stylized Amber monospaced receipt text area mimicking a POS terminal
        JTextArea tx = new JTextArea(formatStr);
        tx.setFont(new Font("Monospaced", Font.PLAIN, 12));
        tx.setBackground(new Color(12, 12, 16));
        tx.setForeground(new Color(245, 158, 11)); // Amber POS neon color
        tx.setEditable(false);
        tx.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COLOR_BORDER, 1),
                BorderFactory.createEmptyBorder(12, 12, 12, 12)
        ));

        JScrollPane scroll = new JScrollPane(tx);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUI(new ModernScrollbarUI());
        container.add(scroll, BorderLayout.CENTER);

        ModernButton btnClose = new ModernButton("TUTUP STRUK BELANJA", COLOR_ACCENT, COLOR_ACCENT_HOVER, 12);
        btnClose.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnClose.setPreferredSize(new Dimension(0, 40));
        btnClose.addActionListener(e -> dialog.dispose());
        container.add(btnClose, BorderLayout.SOUTH);

        dialog.add(container);
        dialog.setVisible(true);
    }

    private String generateJavaFallbackNota(TransaksiPesanan trans) {
        double sub = 0;
        StringBuilder sb = new StringBuilder();
        sb.append("================================================\n");
        sb.append("                  MIE GACOAN                    \n");
        sb.append("         PEMESANAN MANDIRI (FALLBACK)           \n");
        sb.append("================================================\n");
        sb.append(" ID NOTA : ").append(trans.getIdNota()).append("\n");
        sb.append(" MEJA    : ").append(trans.getNomorMeja()).append("\n");
        sb.append("------------------------------------------------\n");

        for (ItemPesanan it : trans.getDaftarBelanja()) {
            double price = it.getMenu().getHargaDasar();
            double subRow = it.getKuantitas() * price;
            sub += subRow;

            sb.append(" ").append(it.getMenu().getNama());
            if (it.getMenu().getKategori().equalsIgnoreCase("Makanan")) {
                sb.append(" (Lvl ").append(it.getLevelPedas()).append(")");
            }
            sb.append("\n");
            sb.append("   ").append(it.getKuantitas()).append(" x Rp ").append((long)price)
              .append("                      Rp ").append((long)subRow).append("\n");
            if (!it.getCatatan().isEmpty()) {
                sb.append("   *Catatan: ").append(it.getCatatan()).append("\n");
            }
            sb.append("\n");
        }

        double tax = sub * 0.10;
        double total = sub + tax;

        sb.append("------------------------------------------------\n");
        sb.append(" Subtotal            :        Rp ").append((long)sub).append("\n");
        sb.append(" Pajak Resto (PB1 10%):       Rp ").append((long)tax).append("\n");
        sb.append(" Biaya Layanan       :        Rp 0\n");
        sb.append("------------------------------------------------\n");
        sb.append(" TOTAL AKHIR         :        Rp ").append((long)total).append("\n");
        sb.append("================================================\n");
        sb.append("      Terima kasih atas pesanan Anda!           \n");
        sb.append("================================================\n");
        return sb.toString();
    }

    // =========================================================================
    // SUBPANEL: KITCHEN DISPLAY SYSTEM (KDS) & MONITOR
    // =========================================================================
    private JPanel buildKitchenTab() {
        JPanel tab = new JPanel(new GridLayout(1, 2, 24, 24));
        tab.setBackground(COLOR_BG);
        tab.setBorder(new EmptyBorder(24, 24, 24, 24));

        // Subpanel 1: Active Dapur Board (Trello-like active column)
        JPanel colActive = new JPanel(new BorderLayout(15, 15));
        colActive.setOpaque(false);

        JLabel titleActive = new JLabel("ANTREAN MASUK MASAK (KITCHEN QUEUE)");
        titleActive.setFont(new Font("Segoe UI Black", Font.BOLD, 14));
        titleActive.setForeground(COLOR_ACCENT);
        colActive.add(titleActive, BorderLayout.NORTH);

        panelKdsActiveQueue = new JPanel();
        panelKdsActiveQueue.setLayout(new BoxLayout(panelKdsActiveQueue, BoxLayout.Y_AXIS));
        panelKdsActiveQueue.setBackground(COLOR_BG);

        JScrollPane scrollActive = new JScrollPane(panelKdsActiveQueue);
        scrollActive.setBorder(null);
        scrollActive.setBackground(COLOR_BG);
        scrollActive.getViewport().setBackground(COLOR_BG);
        scrollActive.getVerticalScrollBar().setUI(new ModernScrollbarUI());
        colActive.add(scrollActive, BorderLayout.CENTER);

        tab.add(colActive);

        // Subpanel 2: History Dapur Board (Called history column)
        JPanel colHistory = new JPanel(new BorderLayout(15, 15));
        colHistory.setOpaque(false);

        JLabel titleHistory = new JLabel("RIWAYAT PANGGILAN SUARA (COMPLETED)");
        titleHistory.setFont(new Font("Segoe UI Black", Font.BOLD, 14));
        titleHistory.setForeground(COLOR_GREEN);
        colHistory.add(titleHistory, BorderLayout.NORTH);

        panelKdsHistoryQueue = new JPanel();
        panelKdsHistoryQueue.setLayout(new BoxLayout(panelKdsHistoryQueue, BoxLayout.Y_AXIS));
        panelKdsHistoryQueue.setBackground(COLOR_BG);

        JScrollPane scrollHistory = new JScrollPane(panelKdsHistoryQueue);
        scrollHistory.setBorder(null);
        scrollHistory.setBackground(COLOR_BG);
        scrollHistory.getViewport().setBackground(COLOR_BG);
        scrollHistory.getVerticalScrollBar().setUI(new ModernScrollbarUI());
        colHistory.add(scrollHistory, BorderLayout.CENTER);

        tab.add(colHistory);

        // Load content
        refreshKdsViews();

        return tab;
    }

    private void refreshKdsViews() {
        // 1. Sync Active Queue Cards
        panelKdsActiveQueue.removeAll();
        if (antreanDapur.isEmpty()) {
            panelKdsActiveQueue.add(createKdsPlaceholder("Belum ada antrean makanan masuk. Dapur bersih!"));
        } else {
            for (TransaksiPesanan trans : antreanDapur) {
                panelKdsActiveQueue.add(buildOrderQueueCard(trans, true));
                panelKdsActiveQueue.add(Box.createRigidArea(new Dimension(0, 16)));
            }
        }

        // 2. Sync History Queue Cards
        panelKdsHistoryQueue.removeAll();
        if (riwayatPanggilan.isEmpty()) {
            panelKdsHistoryQueue.add(createKdsPlaceholder("Belum ada riwayat panggilan TOA."));
        } else {
            for (int i = riwayatPanggilan.size() - 1; i >= 0; i--) {
                TransaksiPesanan trans = riwayatPanggilan.get(i);
                panelKdsHistoryQueue.add(buildOrderQueueCard(trans, false));
                panelKdsHistoryQueue.add(Box.createRigidArea(new Dimension(0, 16)));
            }
        }

        panelKdsActiveQueue.revalidate();
        panelKdsActiveQueue.repaint();
        panelKdsHistoryQueue.revalidate();
        panelKdsHistoryQueue.repaint();
    }

    private JPanel createKdsPlaceholder(String text) {
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
    }

    private JPanel buildOrderQueueCard(TransaksiPesanan trans, boolean isActive) {
        // Round Card with glowing accent border
        ModernCard card = new ModernCard(18, COLOR_CARD, isActive ? COLOR_ACCENT : COLOR_GREEN, 2);
        card.setLayout(new BorderLayout(15, 12));
        card.setBorder(new EmptyBorder(16, 20, 16, 20));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 220));

        // Header (Table lock indicator & invoice timestamp)
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);

        JLabel lblTable = new JLabel("NOMOR MEJA: " + trans.getNomorMeja());
        lblTable.setFont(new Font("Segoe UI Black", Font.BOLD, 15));
        lblTable.setForeground(COLOR_TEXT_PRIMARY);
        headerPanel.add(lblTable, BorderLayout.WEST);

        JLabel lblId = new JLabel(trans.getIdNota());
        lblId.setFont(new Font("Monospaced", Font.BOLD, 11));
        lblId.setForeground(COLOR_TEXT_MUTED);
        headerPanel.add(lblId, BorderLayout.EAST);

        card.add(headerPanel, BorderLayout.NORTH);

        // Body (Items to cook / prepare)
        JPanel bodyPanel = new JPanel();
        bodyPanel.setLayout(new BoxLayout(bodyPanel, BoxLayout.Y_AXIS));
        bodyPanel.setOpaque(false);

        for (ItemPesanan it : trans.getDaftarBelanja()) {
            StringBuilder s = new StringBuilder("  " + it.getKuantitas() + "x  " + it.getMenu().getNama());
            if (it.getMenu().getKategori().equalsIgnoreCase("Makanan")) {
                s.append(" (Lvl ").append(it.getLevelPedas()).append(")");
            }
            if (!it.getCatatan().isEmpty()) {
                s.append("  [Notes: ").append(it.getCatatan()).append("]");
            }

            JLabel lblRow = new JLabel(s.toString());
            lblRow.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 12));
            lblRow.setForeground(COLOR_TEXT_PRIMARY);
            bodyPanel.add(lblRow);
            bodyPanel.add(Box.createRigidArea(new Dimension(0, 3)));
        }
        card.add(bodyPanel, BorderLayout.CENTER);

        // Footer Actions (Selesai Masak / Panggil Ulang)
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        footerPanel.setOpaque(false);

        if (isActive) {
            ModernButton btnFinish = new ModernButton("SELESAI MASAK & PANGGIL TOA C++", COLOR_GREEN, COLOR_GREEN.brighter(), 10);
            btnFinish.setFont(new Font("Segoe UI", Font.BOLD, 11));
            btnFinish.setPreferredSize(new Dimension(240, 34));
            btnFinish.addActionListener(e -> {
                antreanDapur.remove(trans);
                trans.setStatus("SELESAI");
                riwayatPanggilan.add(trans);

                // Run Windows SAPI Call JNI in an asynchronous worker thread so the UI is 100% fluid!
                new Thread(() -> {
                    if (PreFlightCheck.runCheck().dllOk) {
                        try {
                            SistemNotifikasi.panggilAntrean(trans.getNomorMeja());
                        } catch (Exception ex) {
                            System.err.println("[TTS Exception] " + ex.getMessage());
                        }
                    } else {
                        System.out.println("[Fallback TTS] Panggilan Meja " + trans.getNomorMeja() + " terpantau.");
                    }
                }).start();

                refreshKdsViews();
            });
            footerPanel.add(btnFinish);
        } else {
            JLabel lblStatus = new JLabel("PANGGILAN DIKIRIM ✓ ");
            lblStatus.setFont(new Font("Segoe UI Semibold", Font.BOLD, 12));
            lblStatus.setForeground(COLOR_GREEN);
            footerPanel.add(lblStatus);

            ModernButton btnRecall = new ModernButton("PANGGIL ULANG", COLOR_CARD_LIGHT, COLOR_BORDER, 8);
            btnRecall.setFont(new Font("Segoe UI", Font.BOLD, 10));
            btnRecall.setPreferredSize(new Dimension(130, 28));
            btnRecall.addActionListener(e -> {
                new Thread(() -> {
                    if (PreFlightCheck.runCheck().dllOk) {
                        try {
                            SistemNotifikasi.panggilAntrean(trans.getNomorMeja());
                        } catch (Exception ex) {
                            System.err.println("[TTS Exception] " + ex.getMessage());
                        }
                    } else {
                        System.out.println("[Fallback TTS Recall] Panggilan Meja " + trans.getNomorMeja() + " terpantau.");
                    }
                }).start();
            });
            footerPanel.add(btnRecall);
        }

        card.add(footerPanel, BorderLayout.SOUTH);

        return card;
    }

    public static void main(String[] args) {
        // System Look & Feel setup
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            // Ignore
        }

        // Set beautiful dark theme properties for defaults
        UIManager.put("TabbedPane.shadow", COLOR_BORDER);
        UIManager.put("TabbedPane.darkShadow", COLOR_BG);
        UIManager.put("TabbedPane.light", COLOR_BG);
        UIManager.put("TabbedPane.highlight", COLOR_BORDER);
        UIManager.put("TableHeader.cellBorder", BorderFactory.createLineBorder(COLOR_BORDER, 1));
        UIManager.put("Table.gridColor", COLOR_BORDER);

        SwingUtilities.invokeLater(() -> {
            GacoanApp app = new GacoanApp();
            app.setVisible(true);
        });
    }
}
