package gacoan;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;
import javax.swing.JComponent;
import javax.swing.border.Border;

/**
 * Helper class for defining global UI theme and styling.
 * Based on "Modern Minimalism Light Mode" with "Antigravity" effect.
 */
public class UITheme {

    // =========================================================================
    // GLOBAL COLOR PALETTE
    // =========================================================================
    
    // Background App: Off-White / Light Gray
    public static final Color COLOR_BG_APP = Color.decode("#F3F4F6");
    
    // Background Komponen (Cards/Panels): Putih Bersih
    public static final Color COLOR_BG_CARD = Color.decode("#FFFFFF");
    
    // Warna Aksen Utama: Vibrant Orange Gacoan
    public static final Color COLOR_ACCENT_PRIMARY = Color.decode("#EA580C");
    
    // Warna Aksen Sekunder: Success Green
    public static final Color COLOR_ACCENT_SECONDARY = Color.decode("#22C55E");
    
    // Teks Primer (Judul/Nama Menu): Dark Slate / Charcoal
    public static final Color COLOR_TEXT_PRIMARY = Color.decode("#1E293B");
    
    // Teks Sekunder (Catatan/Sub-judul): Medium Gray
    public static final Color COLOR_TEXT_SECONDARY = Color.decode("#64748B");
    
    // Warna tambahan untuk field input atau hover
    public static final Color COLOR_BG_INPUT = Color.decode("#F1F5F9");
    
    // Default border radius (12px to 16px)
    public static final int DEFAULT_RADIUS = 24;

    // =========================================================================
    // STYLE HELPER METHODS
    // =========================================================================

    /**
     * Applies the antigravity effect (rounded corners + soft drop shadow)
     * to a JComponent (e.g., JPanel or JButton).
     * 
     * @param component The JComponent to style
     */
    public static void applyAntigravityEffect(JComponent component) {
        component.setOpaque(false); // Make transparent so custom border can draw the rounded background
        component.setBackground(COLOR_BG_CARD); // Default component background
        component.setBorder(new AntigravityBorder(DEFAULT_RADIUS));
    }

    /**
     * Custom Border that renders a soft diffused drop shadow and a rounded background.
     */
    public static class AntigravityBorder implements Border {
        private final int radius;
        private final int shadowSize = 12;
        private final int shadowOffset = 4; // Move shadow slightly down
        private final Insets insets;

        public AntigravityBorder(int radius) {
            this.radius = radius;
            // Padding around the component to fit the shadow
            this.insets = new Insets(shadowSize, shadowSize, shadowSize + shadowOffset, shadowSize);
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int panelWidth = width - insets.left - insets.right;
            int panelHeight = height - insets.top - insets.bottom;
            int panelX = x + insets.left;
            int panelY = y + insets.top;

            // 1. Draw soft drop shadow layers
            // Shadow gets lighter as it expands outwards
            for (int i = 0; i < shadowSize; i++) {
                int opacity = (int) (6.0 * (1.0 - ((double) i / shadowSize))); 
                g2.setColor(new Color(0, 0, 0, opacity));
                
                int shadowRadius = radius + i;
                g2.fillRoundRect(
                    panelX - i, 
                    panelY - i + shadowOffset, 
                    panelWidth + (i * 2), 
                    panelHeight + (i * 2), 
                    shadowRadius, 
                    shadowRadius
                );
            }

            // 2. Draw the component's rounded background
            g2.setColor(c.getBackground());
            g2.fillRoundRect(panelX, panelY, panelWidth, panelHeight, radius, radius);

            g2.dispose();
        }

        @Override
        public Insets getBorderInsets(Component c) {
            return insets;
        }

        @Override
        public boolean isBorderOpaque() {
            return false;
        }
    }
}
