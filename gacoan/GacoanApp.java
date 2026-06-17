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

    private static final Color COLOR_BG = new Color(18, 18, 24);
    private static final Color COLOR_CARD = new Color(28, 28, 38);
    private static final Color COLOR_CARD_LIGHT = new Color(40, 40, 52);
    private static final Color COLOR_ACCENT = new Color(241, 90, 36);
    private static final Color COLOR_ACCENT_HOVER = new Color(255, 115, 60);
    private static final Color COLOR_TEXT_PRIMARY = new Color(255, 255, 255);
    private static final Color COLOR_TEXT_MUTED = new Color(150, 150, 165);
    private static final Color COLOR_GREEN = new Color(34, 197, 94);
    private static final Color COLOR_CYAN = new Color(6, 182, 212);
    private static final Color COLOR_PURPLE = new Color(168, 85, 247);
    private static final Color COLOR_BORDER = new Color(48, 48, 62);

    private int nomorMejaLocked = 0;
    private final List<Menu> daftarMenu = new ArrayList<>();
    private final List<ItemPesanan> keranjangBelanja = new ArrayList<>();
    private final List<TransaksiPesanan> antreanDapur = new ArrayList<>();
    private final List<TransaksiPesanan> riwayatPanggilan = new ArrayList<>();

    private final GacoanEngine billingEngine = new GacoanEngine();

    private CardLayout mainCardLayout;
    private JPanel mainCardPanel;
    private JTabbedPane appTabbedPane;

    private DefaultTableModel modelTabelKeranjang;
    private JTable tableKeranjang;
    private JLabel lblSubtotal;
    private JLabel lblTax;
    private JLabel lblTotal;
    
    private JPanel panelGridMenu;
    private String currentCategoryFilter = "Semua";

    private JLabel lblTableStatus;
    private ModernButton btnScanQr;

    private JPanel panelKdsActiveQueue;
    private JPanel panelKdsHistoryQueue;

    public GacoanApp() {
        initIconicMenus();

        setTitle("MIE GACOAN - Self Ordering Kiosk & Kitchen Display System");
        setSize(1240, 820);
        setMinimumSize(new Dimension(1024, 768));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        mainCardLayout = new CardLayout();
        mainCardPanel = new JPanel(mainCardLayout);
        mainCardPanel.setBackground(UITheme.COLOR_BG_APP);

        JPanel panelPreFlight = buildPreFlightScreen();
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

            int pillRadius = Math.min(getWidth(), getHeight());
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), pillRadius, pillRadius);
            g2.dispose();

            super.paintComponent(g);
        }
    }

    static class ModernTextField extends JTextField {
        public ModernTextField(String placeholder) {
            setBackground(COLOR_CARD_LIGHT);
            setForeground(COLOR_TEXT_PRIMARY);
            setCaretColor(COLOR_TEXT_PRIMARY);
            setFont(new Font("Segoe UI", Font.PLAIN, 12));
            setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1),
                    BorderFactory.createEmptyBorder(6, 12, 6, 12)
            ));
            
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

    static class ModernComboBox<E> extends JComboBox<E> {
        public ModernComboBox() {
            setBackground(UITheme.COLOR_BG_CARD);
            setForeground(UITheme.COLOR_TEXT_PRIMARY);
            setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));
            setFocusable(false);
            setFont(new Font("Segoe UI", Font.PLAIN, 12));

            setUI(new javax.swing.plaf.basic.BasicComboBoxUI() {
                @Override
                protected JButton createArrowButton() {
                    JButton btn = new JButton() {
                        @Override
                        protected void paintComponent(Graphics g) {
                            Graphics2D g2 = (Graphics2D) g.create();
                            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                            g2.setColor(UITheme.COLOR_BG_CARD);
                            g2.fillRect(0, 0, getWidth(), getHeight());
                            g2.setColor(UITheme.COLOR_TEXT_SECONDARY);
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
                    g2.setColor(UITheme.COLOR_BG_CARD);
                    g2.fillRect(bounds.x, bounds.y, bounds.width, bounds.height);
                    g2.dispose();
                }
            });

            setRenderer(new DefaultListCellRenderer() {
                @Override
                public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                    JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                    label.setOpaque(true);
                    label.setBackground(isSelected ? UITheme.COLOR_ACCENT_PRIMARY : UITheme.COLOR_BG_CARD);
                    label.setForeground(UITheme.COLOR_TEXT_PRIMARY);
                    label.setBorder(new EmptyBorder(6, 10, 6, 10));
                    return label;
                }
            });

            try {
                Object child = getAccessibleContext().getAccessibleChild(0);
                if (child instanceof javax.swing.plaf.basic.ComboPopup) {
                    JList<?> list = ((javax.swing.plaf.basic.ComboPopup) child).getList();
                    list.setBackground(UITheme.COLOR_BG_CARD);
                    list.setForeground(UITheme.COLOR_TEXT_PRIMARY);
                }
            } catch (Exception e) {
            }
        }
    }

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
        }

        @Override
        protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(new Color(180, 180, 195));
            g2.fillRoundRect(thumbBounds.x + 2, thumbBounds.y + 2, thumbBounds.width - 4, thumbBounds.height - 4, 8, 8);
            g2.dispose();
        }
    }

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

    private JPanel buildPreFlightScreen() {
        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setBackground(UITheme.COLOR_BG_APP);

        JPanel boxOuter = new JPanel(new BorderLayout());
        UITheme.applyAntigravityEffect(boxOuter);
        boxOuter.setPreferredSize(new Dimension(640, 580));

        JPanel box = new JPanel();
        box.setLayout(new BoxLayout(box, BoxLayout.Y_AXIS));
        box.setOpaque(false);
        box.setBorder(new EmptyBorder(40, 45, 40, 45));
        boxOuter.add(box, BorderLayout.CENTER);

        JLabel logo = new JLabel("MIE GACOAN");
        logo.setFont(new Font("Segoe UI Black", Font.BOLD, 38));
        logo.setForeground(UITheme.COLOR_ACCENT_PRIMARY);
        logo.setAlignmentX(Component.CENTER_ALIGNMENT);
        box.add(logo);

        JLabel subtext = new JLabel("Sistem Self-Ordering & KDS Pure Java");
        subtext.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 14));
        subtext.setForeground(UITheme.COLOR_TEXT_SECONDARY);
        subtext.setAlignmentX(Component.CENTER_ALIGNMENT);
        box.add(Box.createRigidArea(new Dimension(0, 5)));
        box.add(subtext);

        box.add(Box.createRigidArea(new Dimension(0, 30)));

        PreFlightCheck.DiagnosticResult diag = PreFlightCheck.runCheck();

        box.add(buildDiagnosticPanel("Java Runtime (JDK/JRE) Check", 
                diag.javaOk ? "PASSED (v" + System.getProperty("java.version") + ")" : "FAILED", diag.javaOk));
        box.add(Box.createRigidArea(new Dimension(0, 16)));

        box.add(buildDiagnosticPanel("Java Swing GUI Engine",
                diag.guiStatus, diag.guiOk));
        box.add(Box.createRigidArea(new Dimension(0, 16)));

        box.add(buildDiagnosticPanel("GacoanEngine Billing Check",
                diag.engineStatus, diag.engineOk));
        box.add(Box.createRigidArea(new Dimension(0, 10)));

        JLabel runtimeDetail = new JLabel("Runtime Mode: Pure Java - NetBeans ready");
        runtimeDetail.setFont(new Font("Monospaced", Font.PLAIN, 10));
        runtimeDetail.setForeground(UITheme.COLOR_ACCENT_SECONDARY);
        runtimeDetail.setAlignmentX(Component.CENTER_ALIGNMENT);
        box.add(runtimeDetail);

        box.add(Box.createRigidArea(new Dimension(0, 35)));

        String btnText = "BUKA APLIKASI UTAMA (PURE JAVA)";
        ModernButton btnGo = new ModernButton(btnText, UITheme.COLOR_ACCENT_SECONDARY, UITheme.COLOR_ACCENT_SECONDARY.brighter(), 16);
        btnGo.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnGo.setForeground(Color.WHITE);
        btnGo.setPreferredSize(new Dimension(500, 48));
        btnGo.setMaximumSize(new Dimension(500, 48));
        btnGo.setAlignmentX(Component.CENTER_ALIGNMENT);

        btnGo.addActionListener(e -> mainCardLayout.show(mainCardPanel, "MAIN_APP"));

        box.add(btnGo);

        JLabel warning = new JLabel("UAS Pemrograman Berorientasi Objek - Teknik Informatika");
        warning.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        warning.setForeground(UITheme.COLOR_TEXT_SECONDARY);
        warning.setAlignmentX(Component.CENTER_ALIGNMENT);
        box.add(Box.createRigidArea(new Dimension(0, 20)));
        box.add(warning);

        wrapper.add(boxOuter);
        return wrapper;
    }

    private JPanel buildDiagnosticPanel(String titleText, String statusText, boolean isOk) {
        ModernCard rowCard = new ModernCard(16, UITheme.COLOR_BG_INPUT, new Color(209, 213, 219), 1);
        rowCard.setLayout(new BorderLayout(15, 0));
        rowCard.setBorder(new EmptyBorder(14, 18, 14, 18));
        rowCard.setMaximumSize(new Dimension(540, 60));
        rowCard.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel title = new JLabel(titleText);
        title.setFont(new Font("Segoe UI Semibold", Font.BOLD, 13));
        title.setForeground(UITheme.COLOR_TEXT_PRIMARY);
        rowCard.add(title, BorderLayout.WEST);

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        rightPanel.setOpaque(false);

        JLabel statusLabel = new JLabel(statusText);
        statusLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        statusLabel.setForeground(isOk ? UITheme.COLOR_ACCENT_SECONDARY : UITheme.COLOR_ACCENT_PRIMARY);

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
    }

    private JPanel buildMainAppScreen() {
        JPanel workspace = new JPanel(new BorderLayout());
        workspace.setBackground(UITheme.COLOR_BG_APP);

        JPanel navBar = new JPanel(new BorderLayout());
        navBar.setBackground(UITheme.COLOR_BG_CARD);
        navBar.setBorder(new EmptyBorder(16, 24, 16, 24));

        JLabel appLogo = new JLabel("MIE GACOAN");
        appLogo.setFont(new Font("Segoe UI Black", Font.BOLD, 24));
        appLogo.setForeground(UITheme.COLOR_ACCENT_PRIMARY);
        navBar.add(appLogo, BorderLayout.WEST);

        JPanel tabButtonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        tabButtonsPanel.setOpaque(false);

        ModernButton btnTabPelanggan = new ModernButton("PESAN MANDIRI", UITheme.COLOR_ACCENT_PRIMARY, UITheme.COLOR_ACCENT_PRIMARY.darker(), 10);
        btnTabPelanggan.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnTabPelanggan.setForeground(Color.WHITE);
        btnTabPelanggan.setPreferredSize(new Dimension(140, 36));

        ModernButton btnTabKds = new ModernButton("KITCHEN DISPLAY (KDS)", UITheme.COLOR_BG_INPUT, UITheme.COLOR_BG_CARD, 10);
        btnTabKds.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnTabKds.setForeground(UITheme.COLOR_TEXT_PRIMARY);
        btnTabKds.setPreferredSize(new Dimension(180, 36));

        btnTabPelanggan.addActionListener(e -> {
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
        });

        tabButtonsPanel.add(btnTabPelanggan);
        tabButtonsPanel.add(btnTabKds);
        navBar.add(tabButtonsPanel, BorderLayout.EAST);

        workspace.add(navBar, BorderLayout.NORTH);

        appTabbedPane = new JTabbedPane();
        appTabbedPane.setUI(new javax.swing.plaf.basic.BasicTabbedPaneUI() {
            @Override
            protected int calculateTabAreaHeight(int tabPlacement, int runCount, int maxTabHeight) {
                return 0;
            }
        });
        appTabbedPane.setBackground(UITheme.COLOR_BG_APP);
        appTabbedPane.setBorder(null);

        JPanel panelPelanggan = buildCustomerOrderingTab();
        JPanel panelDapur = buildKitchenTab();

        appTabbedPane.addTab(null, panelPelanggan);
        appTabbedPane.addTab(null, panelDapur);

        workspace.add(appTabbedPane, BorderLayout.CENTER);

        return workspace;
    }

    private JPanel buildCustomerOrderingTab() {
        JPanel tab = new JPanel(new BorderLayout(20, 20));
        tab.setBackground(UITheme.COLOR_BG_APP);
        tab.setBorder(new EmptyBorder(20, 24, 20, 24));

        JPanel leftLayout = new JPanel(new BorderLayout(15, 15));
        leftLayout.setOpaque(false);

        JPanel qrCardOuter = new JPanel(new BorderLayout());
        UITheme.applyAntigravityEffect(qrCardOuter);
        JPanel qrCard = new JPanel(new BorderLayout(15, 15));
        qrCard.setOpaque(false);
        qrCard.setBorder(new EmptyBorder(16, 20, 16, 20));
        qrCardOuter.add(qrCard, BorderLayout.CENTER);

        lblTableStatus = new JLabel("SILAKAN SCAN BARCODE QR PADA MEJA KIOSK");
        lblTableStatus.setFont(new Font("Segoe UI Semibold", Font.BOLD, 13));
        lblTableStatus.setForeground(UITheme.COLOR_TEXT_SECONDARY);
        qrCard.add(lblTableStatus, BorderLayout.WEST);

        btnScanQr = new ModernButton("PINDAI BARCODE QR MEJA", UITheme.COLOR_ACCENT_PRIMARY, UITheme.COLOR_ACCENT_PRIMARY.darker(), 12);
        btnScanQr.setFont(new Font("Segoe UI", Font.BOLD, 11));
        btnScanQr.setForeground(Color.WHITE);
        btnScanQr.setPreferredSize(new Dimension(180, 32));
        btnScanQr.addActionListener(e -> {
            Random r = new Random();
            nomorMejaLocked = r.nextInt(25) + 1;
            lblTableStatus.setText("QR TERVERIFIKASI MEJA: MEJA " + nomorMejaLocked + " (TERKUNCI ✓)");
            lblTableStatus.setForeground(UITheme.COLOR_ACCENT_SECONDARY);
            btnScanQr.setEnabled(false);
            btnScanQr.setText("QR TERVERIFIKASI");
            btnScanQr.setNormalBg(UITheme.COLOR_BG_INPUT);
            btnScanQr.setBackground(UITheme.COLOR_BG_INPUT);
            btnScanQr.setForeground(UITheme.COLOR_TEXT_SECONDARY);
            revalidate();
            repaint();
        });
        qrCard.add(btnScanQr, BorderLayout.EAST);

        leftLayout.add(qrCardOuter, BorderLayout.NORTH);

        JPanel catalogPanel = new JPanel(new BorderLayout(15, 15));
        catalogPanel.setOpaque(false);

        JPanel filterRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        filterRow.setOpaque(false);

        String[] cats = {"Semua", "Makanan", "Dimsum", "Minuman"};
        for (String c : cats) {
            ModernButton btnF = new ModernButton(c.toUpperCase(), UITheme.COLOR_BG_CARD, UITheme.COLOR_BG_INPUT, 16);
            btnF.setFont(new Font("Segoe UI", Font.BOLD, 11));
            btnF.setForeground(UITheme.COLOR_TEXT_PRIMARY);
            btnF.setBorder(new EmptyBorder(6, 18, 6, 18));
            btnF.setPreferredSize(new Dimension(110, 32));
            btnF.addActionListener(ev -> {
                currentCategoryFilter = c;
                renderModernCatalogGrid();
            });
            filterRow.add(btnF);
        }
        catalogPanel.add(filterRow, BorderLayout.NORTH);

        panelGridMenu = new JPanel(new GridLayout(0, 2, 16, 16));
        panelGridMenu.setBackground(UITheme.COLOR_BG_APP);
        
        JPanel gridWrapper = new JPanel(new BorderLayout());
        gridWrapper.setBackground(UITheme.COLOR_BG_APP);
        gridWrapper.add(panelGridMenu, BorderLayout.NORTH);

        JScrollPane catalogScroll = new JScrollPane(gridWrapper);
        catalogScroll.setBorder(null);
        catalogScroll.setOpaque(false);
        catalogScroll.getViewport().setOpaque(false);
        catalogScroll.getVerticalScrollBar().setUI(new ModernScrollbarUI());
        catalogScroll.getVerticalScrollBar().setUnitIncrement(18);

        catalogPanel.add(catalogScroll, BorderLayout.CENTER);
        leftLayout.add(catalogPanel, BorderLayout.CENTER);

        renderModernCatalogGrid();
        tab.add(leftLayout, BorderLayout.CENTER);

        JPanel cartCardOuter = new JPanel(new BorderLayout());
        cartCardOuter.setPreferredSize(new Dimension(420, 0));
        UITheme.applyAntigravityEffect(cartCardOuter);
        JPanel cartCard = new JPanel(new BorderLayout(20, 20));
        cartCard.setOpaque(false);
        cartCard.setBorder(new EmptyBorder(24, 20, 24, 20));
        cartCardOuter.add(cartCard, BorderLayout.CENTER);

        JLabel lblCartTitle = new JLabel("Detail Keranjang Belanja");
        lblCartTitle.setFont(new Font("Segoe UI Black", Font.BOLD, 18));
        lblCartTitle.setForeground(UITheme.COLOR_TEXT_PRIMARY);
        cartCard.add(lblCartTitle, BorderLayout.NORTH);

        String[] columns = {"Pesanan", "Porsi", "Harga"};
        modelTabelKeranjang = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tableKeranjang = new JTable(modelTabelKeranjang);
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
        cartCard.add(cartScroll, BorderLayout.CENTER);

        JPanel totalsPanel = new JPanel();
        totalsPanel.setLayout(new BoxLayout(totalsPanel, BoxLayout.Y_AXIS));
        totalsPanel.setOpaque(false);

        lblSubtotal = new JLabel("Subtotal: Rp 0");
        lblSubtotal.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblSubtotal.setForeground(UITheme.COLOR_TEXT_SECONDARY);

        lblTax = new JLabel("Pajak Restoran (PB1 10%): Rp 0");
        lblTax.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblTax.setForeground(UITheme.COLOR_TEXT_SECONDARY);

        lblTotal = new JLabel("TOTAL TAGIHAN: Rp 0");
        lblTotal.setFont(new Font("Segoe UI Black", Font.BOLD, 22));
        lblTotal.setForeground(UITheme.COLOR_TEXT_PRIMARY);

        totalsPanel.add(lblSubtotal);
        totalsPanel.add(Box.createRigidArea(new Dimension(0, 4)));
        totalsPanel.add(lblTax);
        totalsPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        totalsPanel.add(lblTotal);
        totalsPanel.add(Box.createRigidArea(new Dimension(0, 16)));

        ModernButton btnCheckout = new ModernButton("KONFIRMASI BAYAR & STRUK", UITheme.COLOR_ACCENT_PRIMARY, UITheme.COLOR_ACCENT_PRIMARY.darker(), 16);
        btnCheckout.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnCheckout.setForeground(Color.WHITE);
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

            String timeStamp = new SimpleDateFormat("yyyyMMdd-HHmmss").format(new Date());
            String idNota = "GCN-" + timeStamp;
            
            TransaksiPesanan transaksi = new TransaksiPesanan(idNota, nomorMejaLocked);
            for (ItemPesanan it : keranjangBelanja) {
                transaksi.tambahItem(it);
            }

            String receiptText = billingEngine.hitungNota(transaksi);

            antreanDapur.add(transaksi);

            showAnimatedReceiptDialog(receiptText);

            keranjangBelanja.clear();
            modelTabelKeranjang.setRowCount(0);
            updateCartSummary();

            nomorMejaLocked = 0;
            btnScanQr.setEnabled(true);
            btnScanQr.setNormalBg(UITheme.COLOR_ACCENT_PRIMARY);
            btnScanQr.setBackground(UITheme.COLOR_ACCENT_PRIMARY);
            btnScanQr.setForeground(Color.WHITE);
            btnScanQr.setText("PINDAI BARCODE QR MEJA");
            lblTableStatus.setText("SILAKAN SCAN BARCODE QR PADA MEJA KIOSK");
            lblTableStatus.setForeground(UITheme.COLOR_TEXT_SECONDARY);

            refreshKdsViews();
        });

        totalsPanel.add(btnCheckout);
        cartCard.add(totalsPanel, BorderLayout.SOUTH);

        tab.add(cartCardOuter, BorderLayout.EAST);

        return tab;
    }

    private void renderModernCatalogGrid() {
        panelGridMenu.removeAll();

        for (Menu item : daftarMenu) {
            if (!currentCategoryFilter.equals("Semua") && !item.getKategori().equalsIgnoreCase(currentCategoryFilter)) {
                continue;
            }

            JPanel cardOuter = new JPanel(new BorderLayout());
            UITheme.applyAntigravityEffect(cardOuter);
            JPanel card = new JPanel(new BorderLayout(15, 12));
            card.setOpaque(false);
            card.setBorder(new EmptyBorder(24, 24, 24, 24));
            cardOuter.add(card, BorderLayout.CENTER);

            JPanel top = new JPanel(new BorderLayout(5, 5));
            top.setOpaque(false);

            JPanel textWrapper = new JPanel(new GridLayout(2, 1, 2, 2));
            textWrapper.setOpaque(false);

            JLabel name = new JLabel(item.getNama());
            name.setFont(new Font("Segoe UI Black", Font.BOLD, 17));
            name.setForeground(UITheme.COLOR_TEXT_PRIMARY);
            textWrapper.add(name);

            JPanel tagWrapper = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
            tagWrapper.setOpaque(false);

            Color tagColor = UITheme.COLOR_ACCENT_PRIMARY;
            if (item.getKategori().equalsIgnoreCase("Dimsum")) tagColor = new Color(6, 182, 212);
            if (item.getKategori().equalsIgnoreCase("Minuman")) tagColor = new Color(168, 85, 247);

            JLabel lblCat = new JLabel("  " + item.getKategori().toUpperCase() + "  ");
            lblCat.setFont(new Font("Segoe UI Black", Font.BOLD, 9));
            lblCat.setForeground(Color.WHITE);
            
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

            String priceText = item.getKategori().equalsIgnoreCase("Makanan") ? "Rp 11K-13K" : (item.getKategori().equalsIgnoreCase("Dimsum") ? "Rp 10K" : "Rp 9K");
            JLabel price = new JLabel(priceText);
            price.setFont(new Font("Segoe UI Black", Font.BOLD, 16));
            price.setForeground(UITheme.COLOR_ACCENT_SECONDARY);
            top.add(price, BorderLayout.EAST);

            card.add(top, BorderLayout.NORTH);

            boolean isFood = item.getKategori().equalsIgnoreCase("Makanan");
            JPanel optionsPanel = new JPanel();
            optionsPanel.setLayout(new BoxLayout(optionsPanel, BoxLayout.Y_AXIS));
            optionsPanel.setOpaque(false);

            JLabel lblNotes = new JLabel("Catatan Konsumen:");
            lblNotes.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 12));
            lblNotes.setForeground(UITheme.COLOR_TEXT_SECONDARY);

            ModernTextField txtNote = new ModernTextField("Contoh: Tanpa kuah, sendok...");
            txtNote.setBackground(UITheme.COLOR_BG_INPUT);
            txtNote.setForeground(UITheme.COLOR_TEXT_PRIMARY);
            txtNote.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(209, 213, 219), 1),
                    BorderFactory.createEmptyBorder(10, 15, 10, 15)
            ));
            txtNote.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));

            ModernComboBox<String> cmbLvl = new ModernComboBox<>();
            cmbLvl.setBackground(UITheme.COLOR_BG_CARD);
            cmbLvl.setForeground(UITheme.COLOR_TEXT_PRIMARY);
            cmbLvl.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));
            cmbLvl.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
            
            if (isFood) {
                JLabel lblLvl = new JLabel("Pilih Level Pedas:");
                lblLvl.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 12));
                lblLvl.setForeground(UITheme.COLOR_TEXT_SECONDARY);

                for (int i = 0; i <= 8; i++) {
                    if (i == 0) cmbLvl.addItem("Original (Lvl 0) - Rp 11.000");
                    else if (i <= 4) cmbLvl.addItem("Level " + i + " - Rp 11.000");
                    else cmbLvl.addItem("Level " + i + " - Rp 13.000");
                }

                optionsPanel.add(lblLvl);
                optionsPanel.add(Box.createRigidArea(new Dimension(0, 4)));
                optionsPanel.add(cmbLvl);
                optionsPanel.add(Box.createRigidArea(new Dimension(0, 10)));
            }

            optionsPanel.add(lblNotes);
            optionsPanel.add(Box.createRigidArea(new Dimension(0, 4)));
            optionsPanel.add(txtNote);

            card.add(optionsPanel, BorderLayout.CENTER);

            ModernButton btnAdd = new ModernButton("TAMBAHKAN KAN", UITheme.COLOR_ACCENT_PRIMARY, UITheme.COLOR_ACCENT_PRIMARY.darker(), 16);
            btnAdd.setFont(new Font("Segoe UI", Font.BOLD, 12));
            btnAdd.setForeground(Color.WHITE);
            btnAdd.setPreferredSize(new Dimension(0, 36));
            
            JPanel btnWrapper = new JPanel(new BorderLayout());
            btnWrapper.setOpaque(false);
            btnWrapper.setBorder(new EmptyBorder(8, 12, 8, 12));
            btnWrapper.add(btnAdd, BorderLayout.CENTER);
            
            btnAdd.addActionListener(e -> {
                int selectedLevel = isFood ? cmbLvl.getSelectedIndex() : 0;
                String userNote = txtNote.getText().trim();
                if (userNote.equals("Contoh: Tanpa kuah, sendok...")) {
                    userNote = "";
                }

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
                txtNote.setForeground(UITheme.COLOR_TEXT_SECONDARY);
                updateCartUI();
            });

            card.add(btnWrapper, BorderLayout.SOUTH);

            panelGridMenu.add(cardOuter);
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
        dialog.setBackground(new Color(0, 0, 0, 0));
        ModernCard container = new ModernCard(24, UITheme.COLOR_BG_CARD, new Color(229, 231, 235), 2);
        container.setLayout(new BorderLayout(15, 15));
        container.setBorder(new EmptyBorder(24, 24, 24, 24));

        JLabel title = new JLabel("TRANSAKSI BERHASIL");
        title.setFont(new Font("Segoe UI Black", Font.BOLD, 18));
        title.setForeground(COLOR_GREEN);
        title.setHorizontalAlignment(JLabel.CENTER);
        container.add(title, BorderLayout.NORTH);

        JTextArea tx = new JTextArea(formatStr);
        tx.setFont(new Font("Monospaced", Font.PLAIN, 12));
        tx.setBackground(new Color(12, 12, 16));
        tx.setForeground(new Color(245, 158, 11));
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

    private JPanel buildKitchenTab() {
        JPanel tab = new JPanel(new GridLayout(1, 2, 24, 24));
        tab.setBackground(UITheme.COLOR_BG_APP);
        tab.setBorder(new EmptyBorder(24, 24, 24, 24));

        JPanel colActive = new JPanel(new BorderLayout(15, 15));
        colActive.setOpaque(false);

        JLabel titleActive = new JLabel("ANTREAN MASUK MASAK (KITCHEN QUEUE)");
        titleActive.setFont(new Font("Segoe UI Black", Font.BOLD, 14));
        titleActive.setForeground(UITheme.COLOR_ACCENT_PRIMARY);
        colActive.add(titleActive, BorderLayout.NORTH);

        panelKdsActiveQueue = new JPanel();
        panelKdsActiveQueue.setLayout(new BoxLayout(panelKdsActiveQueue, BoxLayout.Y_AXIS));
        panelKdsActiveQueue.setBackground(UITheme.COLOR_BG_APP);

        JScrollPane scrollActive = new JScrollPane(panelKdsActiveQueue);
        scrollActive.setBorder(null);
        scrollActive.setBackground(UITheme.COLOR_BG_APP);
        scrollActive.getViewport().setBackground(UITheme.COLOR_BG_APP);
        scrollActive.getVerticalScrollBar().setUI(new ModernScrollbarUI());
        colActive.add(scrollActive, BorderLayout.CENTER);

        tab.add(colActive);

        JPanel colHistory = new JPanel(new BorderLayout(15, 15));
        colHistory.setOpaque(false);

        JLabel titleHistory = new JLabel("RIWAYAT PANGGILAN SUARA (COMPLETED)");
        titleHistory.setFont(new Font("Segoe UI Black", Font.BOLD, 14));
        titleHistory.setForeground(UITheme.COLOR_ACCENT_SECONDARY);
        colHistory.add(titleHistory, BorderLayout.NORTH);

        panelKdsHistoryQueue = new JPanel();
        panelKdsHistoryQueue.setLayout(new BoxLayout(panelKdsHistoryQueue, BoxLayout.Y_AXIS));
        panelKdsHistoryQueue.setBackground(UITheme.COLOR_BG_APP);

        JScrollPane scrollHistory = new JScrollPane(panelKdsHistoryQueue);
        scrollHistory.setBorder(null);
        scrollHistory.setBackground(UITheme.COLOR_BG_APP);
        scrollHistory.getViewport().setBackground(UITheme.COLOR_BG_APP);
        scrollHistory.getVerticalScrollBar().setUI(new ModernScrollbarUI());
        colHistory.add(scrollHistory, BorderLayout.CENTER);

        tab.add(colHistory);

        refreshKdsViews();

        return tab;
    }

    private void refreshKdsViews() {
        panelKdsActiveQueue.removeAll();
        if (antreanDapur.isEmpty()) {
            panelKdsActiveQueue.add(createKdsPlaceholder("Belum ada antrean makanan masuk. Dapur bersih!"));
        } else {
            for (TransaksiPesanan trans : antreanDapur) {
                panelKdsActiveQueue.add(buildOrderQueueCard(trans, true));
                panelKdsActiveQueue.add(Box.createRigidArea(new Dimension(0, 16)));
            }
        }

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
    }

    private JPanel buildOrderQueueCard(TransaksiPesanan trans, boolean isActive) {
        JPanel cardOuter = new JPanel(new BorderLayout());
        UITheme.applyAntigravityEffect(cardOuter);
        cardOuter.setMaximumSize(new Dimension(Integer.MAX_VALUE, 300));
        cardOuter.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JPanel card = new JPanel(new BorderLayout(15, 12));
        card.setOpaque(false);
        card.setBorder(new EmptyBorder(16, 20, 16, 20));
        cardOuter.add(card, BorderLayout.CENTER);

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);

        JLabel lblTable = new JLabel("NOMOR MEJA: " + trans.getNomorMeja());
        lblTable.setFont(new Font("Segoe UI Black", Font.BOLD, 15));
        lblTable.setForeground(UITheme.COLOR_TEXT_PRIMARY);
        headerPanel.add(lblTable, BorderLayout.WEST);

        JLabel lblId = new JLabel(trans.getIdNota());
        lblId.setFont(new Font("Monospaced", Font.BOLD, 11));
        lblId.setForeground(UITheme.COLOR_TEXT_SECONDARY);
        headerPanel.add(lblId, BorderLayout.EAST);

        card.add(headerPanel, BorderLayout.NORTH);

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
            lblRow.setForeground(UITheme.COLOR_TEXT_PRIMARY);
            bodyPanel.add(lblRow);
            bodyPanel.add(Box.createRigidArea(new Dimension(0, 3)));
        }
        card.add(bodyPanel, BorderLayout.CENTER);

        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        footerPanel.setOpaque(false);

        if (isActive) {
            ModernButton btnFinish = new ModernButton("SELESAI MASAK & PANGGIL TOA", UITheme.COLOR_ACCENT_SECONDARY, UITheme.COLOR_ACCENT_SECONDARY.darker(), 12);
            btnFinish.setFont(new Font("Segoe UI", Font.BOLD, 11));
            btnFinish.setForeground(Color.WHITE);
            btnFinish.setPreferredSize(new Dimension(240, 34));
            btnFinish.addActionListener(e -> {
                antreanDapur.remove(trans);
                trans.setStatus("SELESAI");
                riwayatPanggilan.add(trans);

                new Thread(() -> {
                    try {
                        SistemNotifikasi.panggilAntrean(trans.getNomorMeja());
                    } catch (Exception ex) {
                        System.err.println("[Notification Exception] " + ex.getMessage());
                    }
                }).start();

                refreshKdsViews();
            });
            footerPanel.add(btnFinish);
        } else {
            JLabel lblStatus = new JLabel("PANGGILAN DIKIRIM ✓ ");
            lblStatus.setFont(new Font("Segoe UI Semibold", Font.BOLD, 12));
            lblStatus.setForeground(UITheme.COLOR_ACCENT_SECONDARY);
            footerPanel.add(lblStatus);

            ModernButton btnRecall = new ModernButton("PANGGIL ULANG", UITheme.COLOR_BG_INPUT, UITheme.COLOR_BG_CARD, 8);
            btnRecall.setFont(new Font("Segoe UI", Font.BOLD, 10));
            btnRecall.setForeground(UITheme.COLOR_TEXT_PRIMARY);
            btnRecall.setPreferredSize(new Dimension(130, 28));
            btnRecall.addActionListener(e -> {
                new Thread(() -> {
                    try {
                        SistemNotifikasi.panggilAntrean(trans.getNomorMeja());
                    } catch (Exception ex) {
                        System.err.println("[Notification Exception] " + ex.getMessage());
                    }
                }).start();
            });
            footerPanel.add(btnRecall);
        }

        card.add(footerPanel, BorderLayout.SOUTH);

        return cardOuter;
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
        }

        UIManager.put("TabbedPane.shadow", UITheme.COLOR_BG_APP);
        UIManager.put("TabbedPane.darkShadow", UITheme.COLOR_BG_APP);
        UIManager.put("TabbedPane.light", UITheme.COLOR_BG_APP);
        UIManager.put("TabbedPane.highlight", UITheme.COLOR_BG_APP);
        UIManager.put("TableHeader.cellBorder", BorderFactory.createLineBorder(new Color(209, 213, 219), 1));
        UIManager.put("Table.gridColor", new Color(229, 231, 235));

        SwingUtilities.invokeLater(() -> {
            GacoanApp app = new GacoanApp();
            app.setVisible(true);
        });
    }
}
