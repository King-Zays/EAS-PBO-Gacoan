package gacoan;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;
import javax.swing.JComponent;
import javax.swing.border.Border;

public class UITheme {

    
    public static final Color COLOR_BG_APP = Color.decode("#F3F4F6");
    
    public static final Color COLOR_BG_CARD = Color.decode("#FFFFFF");
    
    public static final Color COLOR_ACCENT_PRIMARY = Color.decode("#EA580C");
    
    public static final Color COLOR_ACCENT_SECONDARY = Color.decode("#22C55E");
    
    public static final Color COLOR_TEXT_PRIMARY = Color.decode("#1E293B");
    
    public static final Color COLOR_TEXT_SECONDARY = Color.decode("#64748B");
    
    public static final Color COLOR_BG_INPUT = Color.decode("#F1F5F9");
    
    public static final int DEFAULT_RADIUS = 24;

    
    public static void applyAntigravityEffect(JComponent component) {
        component.setOpaque(false);
        component.setBackground(COLOR_BG_CARD);
        component.setBorder(new AntigravityBorder(DEFAULT_RADIUS));
    }

    
    public static class AntigravityBorder implements Border {
        private final int radius;
        private final int shadowSize = 12;
        private final int shadowOffset = 4;
        private final Insets insets;

        public AntigravityBorder(int radius) {
            this.radius = radius;
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
